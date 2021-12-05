/******************************************************************************
* Matches Ontologies by measuring the word similarity between their classes,  *
* using a weighted Jaccard index.                                             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.lexical;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.data.Map2MapComparable;
import liseda.matcha.data.Map2Set;
import liseda.matcha.match.AbstractParallelMatcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.WordLexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.InstanceMatchingCategory;
import liseda.matcha.settings.Settings;
import liseda.matcha.settings.WordMatchStrategy;

public class WordMatcher extends AbstractParallelMatcher //TODO: Extend this to generate subsumption mappings
{

//Attributes
	
	protected static final String DESCRIPTION = "Matches entities by checking for words\n" +
			  								  "they share in their Lexicon entries.\n" +
			  								  "Computes word similarity by entity, by\n" +
			  								  "by entry, or combined";
	protected static final String NAME = "Word Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS,EntityType.INDIVIDUAL,EntityType.DATA_PROP,EntityType.OBJECT_PROP};
	private WordLexicon sourceLex;
	private WordLexicon targetLex;
	private WordMatchStrategy strategy = WordMatchStrategy.AVERAGE;
	private String language;

//Constructors
	
	/**
	 * Constructs a new WordMatcher for the given language
	 * @param lang: the language on which to match Ontologies
	 */
	public WordMatcher(String lang)
	{
		super();
		language = lang;
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}
	
	/**
	 * Constructs a new WordMatcher for the given language
	 * @param lang: the language on which to match Ontologies
	 * @param s: the WordMatchStrategy to use
	 */
	public WordMatcher(String lang, WordMatchStrategy s)
	{
		this(lang);
		strategy = s;
	}
	
//Public Methods
	
	@Override
	public Alignment extendAlignment(Alignment a, EntityType e, double thresh)
	{
		source = a.getSourceOntology();
		target = a.getTargetOntology();
		toMatch = e;
		Alignment b = match(source,target,e,thresh);
		HashSet<Mapping> toRemove = new HashSet<Mapping>();
		for(Mapping m : b)
			if(a.containsConflict(m))
				toRemove.add(m);
		b.removeAll(toRemove);
		return b;			
	}
	
	@Override
	public Alignment match(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		source = o1;
		target = o2;
		toMatch = e;
		Alignment a = new Alignment(o1,o2);
		if(!checkEntityType(e))
			return a;
		System.out.println("Building Word Lexicons");
		long time = System.currentTimeMillis()/1000;
		sourceLex = o1.getWordLexicon(e,language);
		targetLex = o2.getWordLexicon(e,language);

		System.out.println("Running " + NAME + " in match mode");
		
		//If the strategy is BY_CLASS, the alignment can be computed
		//globally. Otherwise we need to compute a preliminary
		//alignment and then rematch according to the strategy.
		double t;
		if(strategy.equals(WordMatchStrategy.BY_ENTITY))
			t = thresh;
		else
			t = thresh * 0.5;
		//Global matching is done by chunks so as not to overload the memory
		System.out.println("Blocks to match: " + sourceLex.blockCount() +
				"x" + targetLex.blockCount());
		//Match each chunk of both WordLexicons
		for(int i = 0; i < sourceLex.blockCount(); i++)
		{
			//The word table (words->String, class indexes->String) for the current block
			Map2Set<String,String> sWLex = sourceLex.getWordTable(i);
			for(int j = 0; j < targetLex.blockCount(); j++)
			{
				//The word table (words->String, class indexes->String) for the current block
				Map2Set<String,String> tWLex = targetLex.getWordTable(j);
				Vector<Mapping> temp = matchBlocks(sWLex,tWLex,e,t);
				//If the strategy is BY_CLASS, just add the alignment
				if(strategy.equals(WordMatchStrategy.BY_ENTITY))
					a.addAll(temp);
				//Otherwise, update the similarity according to the strategy
				else
				{
					for(Mapping m : temp)
					{
						//First compute the name similarity
						double nameSim = entityNameSimilarity((String)m.getEntity1(),(String)m.getEntity2());
						//Then update the final similarity according to the strategy
						double sim = m.getSimilarity();
						if(strategy.equals(WordMatchStrategy.BY_NAME))
							sim = nameSim;
						else if(strategy.equals(WordMatchStrategy.AVERAGE))
							sim = Math.sqrt(nameSim * sim);
						else if(strategy.equals(WordMatchStrategy.MAXIMUM))
							sim = Math.max(nameSim,sim);
						else if(strategy.equals(WordMatchStrategy.MINIMUM))
							sim = Math.min(nameSim,sim);
						if(sim >= thresh)
							a.add(new Mapping(m.getEntity1(),m.getEntity2(),sim,MappingRelation.EQUIVALENCE));
					}
				}
				System.out.print("+");
			}
			System.out.println();
		}
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
		return a;
	}
	
	@Override
	public Alignment rematch(Alignment a, EntityType e)
	{
		System.out.println("Building Word Lexicons");
		System.out.println("Language: " + language);
		sourceLex = a.getSourceOntology().getWordLexicon(e,language);
		targetLex = a.getTargetOntology().getWordLexicon(e,language);
		return super.rematch(a, e);
	}
	
//Protected Methods	

	@Override
	protected Mapping mapTwoEntities(String sourceId, String targetId)
	{
		//If the strategy is not by name, compute the class similarity
		double entitySim = 0.0;
		if(!strategy.equals(WordMatchStrategy.BY_NAME))
		{
			entitySim = entitySimilarity(sourceId,targetId);
			//If the class similarity is very low, return the mapping
			//so as not to waste time computing name similarity
			if(entitySim < 0.25)
				return new Mapping(sourceId, targetId, entitySim, MappingRelation.EQUIVALENCE);
		}
		//If the strategy is not by class, compute the name similarity
		double nameSim = 0.0;
		if(!strategy.equals(WordMatchStrategy.BY_ENTITY))
			nameSim = entityNameSimilarity(sourceId,targetId);
		
		//Combine the similarities according to the strategy
		double sim = 0.0;
		if(strategy.equals(WordMatchStrategy.BY_NAME))
			sim = nameSim;
		else if(strategy.equals(WordMatchStrategy.BY_ENTITY))
			sim = entitySim;
		else if(strategy.equals(WordMatchStrategy.AVERAGE))
			sim = Math.sqrt(nameSim * entitySim);
		else if(strategy.equals(WordMatchStrategy.MAXIMUM))
			sim = Math.max(nameSim,entitySim);
		else if(strategy.equals(WordMatchStrategy.MINIMUM))
			sim = Math.min(nameSim,entitySim);
		return new Mapping(sourceId, targetId, sim, MappingRelation.EQUIVALENCE);
	}
	
//Private Methods
	
	//Computes the word-based (bag-of-words) similarity between two
	//classes, for use by rematch()
	private double entitySimilarity(String sourceId, String targetId)
	{
		Set<String> sourceWords = sourceLex.getWordsByEntity(sourceId);
		Set<String> targetWords = targetLex.getWordsByEntity(targetId);
		double intersection = 0.0;
		double union = sourceLex.getEntityEC(sourceId) + 
				targetLex.getEntityEC(targetId);
		for(String w : sourceWords)
		{
			double weight = sourceLex.getWordEC(w) * sourceLex.getWordWeight(w,sourceId);
			if(targetWords.contains(w))
				intersection += Math.sqrt(weight * targetLex.getWordEC(w) *
						targetLex.getWordWeight(w,targetId));
		}			
		union -= intersection;
		return intersection / union;
	}
	
	//Computes the maximum word-based (bag-of-words) similarity between
	//two classes' names, for use by both match() and rematch()
	private double entityNameSimilarity(String sourceId, String targetId)
	{
		double nameSim = 0;
		double sim, weight;
		Set<String> sourceNames = sourceLex.getNames(sourceId);
		Set<String> targetNames = targetLex.getNames(targetId);
		for(String s : sourceNames)
		{
			weight = sourceLex.getNameWeight(s,sourceId);
			for(String t : targetNames)
			{
				sim = weight * targetLex.getNameWeight(t, targetId);
				sim *= nameSimilarity(s,t);
				if(sim > nameSim)
					nameSim = sim;
			}
		}
		return nameSim;
	}
	
	//Matches two WordLexicon blocks by class.
	//Used by match() method either to compute the final BY_CLASS alignment
	//or to compute a preliminary alignment which is then refined according
	//to the WordMatchStrategy.
	private Vector<Mapping> matchBlocks(Map2Set<String,String> sWLex,
			Map2Set<String,String> tWLex, EntityType e, double thresh)
	{
		Map2MapComparable<String,String,Double> maps = new Map2MapComparable<String,String,Double>();
		Settings set = Settings.getInstance();
		//To minimize iterations, we want to iterate through the smallest Lexicon
		boolean sourceIsSmaller = (sWLex.keyCount() <= tWLex.keyCount());
		Set<String> words;
		if(sourceIsSmaller)
			words = sWLex.keySet();
		else
			words = tWLex.keySet();
		
		for(String s : words)
		{
			Set<String> sourceIndexes = sWLex.get(s);
			Set<String> targetIndexes = tWLex.get(s);
			if(sourceIndexes == null || targetIndexes == null)
				continue;
			double ec = sourceLex.getWordEC(s) * targetLex.getWordEC(s);
			for(String i : sourceIndexes)
			{
				if(e.equals(EntityType.INDIVIDUAL) && !set.isToMatch(i))
					continue;
				double sim = ec * sourceLex.getWordWeight(s,i);
				for(String j : targetIndexes)
				{
					if(e.equals(EntityType.INDIVIDUAL) && (!set.isToMatch(j) ||
							(set.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_CLASSES) &&
							!SemanticMap.getInstance().shareClass(i,j))))
						continue;
					double finalSim = Math.sqrt(sim * targetLex.getWordWeight(s,j));
					Double previousSim = maps.get(i,j);
					if(previousSim == null)
						previousSim = 0.0;
					finalSim += previousSim;
					maps.add(i,j,finalSim);
				}
			}
		}
		Set<String> sources = maps.keySet();
		Vector<Mapping> a = new Vector<Mapping>();
		for(String i : sources)
		{
			Set<String> targets = maps.keySet(i);
			for(String j : targets)
			{
				double sim = maps.get(i,j);
				sim /= sourceLex.getEntityEC(i) + targetLex.getEntityEC(j) - sim;
				if(sim >= thresh)
					a.add(new Mapping(i, j, sim, MappingRelation.EQUIVALENCE));
			}
		}
		return a;
	}

	//Computes the word-based (bag-of-words) similarity between two names
	private double nameSimilarity(String s, String t)
	{
		Set<String> sourceWords = sourceLex.getWordsByName(s);
		Set<String> targetWords = targetLex.getWordsByName(t);
		double intersection = 0.0;
		double union = sourceLex.getNameEC(s) + targetLex.getNameEC(t);
		for(String w : sourceWords)
			if(targetWords.contains(w))
				intersection += Math.sqrt(sourceLex.getWordEC(w) * targetLex.getWordEC(w));
		union -= intersection;
		return intersection/union;
	}
}