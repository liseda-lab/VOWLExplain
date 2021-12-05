/******************************************************************************
* Matches Ontologies by measuring the maximum String similarity between their *
* entities, using one of the four available String similarity measures.       *
*                                                                             *
* @authors Daniel Faria                                                       *
******************************************************************************/
package liseda.matcha.match.lexical;

import java.util.HashSet;
import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.data.Map2Set;
import liseda.matcha.match.AbstractParallelMatcher;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.LanguageSetting;
import liseda.matcha.settings.Settings;
import liseda.matcha.settings.StringSimMeasure;
import liseda.matcha.similarity.Similarity;

public class StringMatcher extends AbstractParallelMatcher
{

//Attributes

	protected static final String DESCRIPTION = "Matches entities by computing the maximum\n" +
											  "String similarity between their Lexicon\n" +
											  "entries, using a String similarity measure";
	protected static final String NAME = "String Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS,EntityType.INDIVIDUAL,EntityType.DATA_PROP,EntityType.OBJECT_PROP};
	//Similarity measure
	private StringSimMeasure measure = StringSimMeasure.ISUB;
	//Correction factor (to make string similarity values comparable to word similarity values
	//and thus enable their combination and proper selection; 0.8 is optimized for the ISub measure)
	private final double CORRECTION = 0.80;

//Constructors
	
	/**
	 * Constructs a new ParametricStringMatcher with default
	 * String similarity measure (ISub)
	 */
	public StringMatcher()
	{
		super();
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}

	/**
	 * Constructs a new ParametricStringMatcher with the given String similarity measure
	 * @args m: the string similarity measure
	 */
	public StringMatcher(StringSimMeasure m)
	{
		this();
		measure = m;
	}

//Public Methods
	
	@Override
	public Alignment extendAlignment(Alignment a, EntityType e, double thresh)
	{	
		showProgress = false;
		source = a.getSourceOntology();
		target = a.getTargetOntology();
		toMatch = e;
		Alignment ext = new Alignment(source,target);
		if(e.equals(EntityType.CLASS))
		{
			System.out.println("Running " + NAME  + " in alignment extension mode");
			long time = System.currentTimeMillis()/1000;
			System.out.println("Matching Children & Parents");
			ext.addAll(extendChildrenAndParents(a,thresh));
			Alignment aux = extendChildrenAndParents(ext,thresh);
			int size = 0;
			for(int i = 0; i < 10 && ext.size() > size; i++)
			{
				size = ext.size();
				for(Mapping m : aux)
					if(!a.containsConflict(m))
						ext.add(m);
				aux = extendChildrenAndParents(aux,thresh);
			}
			System.out.println("Matching Siblings");
			ext.addAll(extendSiblings(a,thresh));
			time = System.currentTimeMillis()/1000 - time;
			System.out.println("Finished in " + time + " seconds");
		}
		else if(e.equals(EntityType.INDIVIDUAL))
		{
			System.out.println("Running " + NAME  + " in alignment extension mode");
			long time = System.currentTimeMillis()/1000;
			ext = extendNeighbors(a,thresh);
			time = System.currentTimeMillis()/1000 - time;
			System.out.println("Finished in " + time + " seconds");
		}
		else
			ext = super.extendAlignment(a, e, thresh);
		return ext;
	}
	
//Protected Methods

	@Override
	protected Mapping mapTwoEntities(String sId, String tId)
	{
		double maxSim = 0.0;
		double sim, weight;
		Lexicon sLex = source.getLexicon(toMatch);
		Lexicon tLex = target.getLexicon(toMatch);
		if(Settings.getInstance().getLanguageSetting().equals(LanguageSetting.MULTI))
		{
			//Get the shared languages
			Set<String> languages = new HashSet<String>(sLex.getLanguages());
			languages.retainAll(tLex.getLanguages());
			for(String l : languages)
			{
				Set<String> sourceNames = sLex.getNamesWithLanguage(sId,l);
				Set<String> targetNames = tLex.getNamesWithLanguage(tId,l);
				if(sourceNames == null || targetNames == null)
					continue;
				for(String s : sourceNames)
				{
					if(sLex.isFormula(s))
						continue;
					weight = sLex.getCorrectedWeight(s, sId, l);
					for(String t : targetNames)
					{
						if(tLex.isFormula(t))
							continue;
						sim = weight * tLex.getCorrectedWeight(t, tId, l) * Similarity.stringSimilarity(s,t, measure);
						maxSim = Math.max(maxSim,sim);
					}
				}
			}
		}
		else
		{
			Set<String> sourceNames = sLex.getNames(sId);
			Set<String> targetNames = tLex.getNames(tId);
			if(sourceNames != null && targetNames != null)
			{
				for(String s : sourceNames)
				{
					if(sLex.isFormula(s))
						continue;
					weight = sLex.getCorrectedWeight(s, sId);
					for(String t : targetNames)
					{
						if(tLex.isFormula(t))
							continue;
						
						sim = weight * tLex.getCorrectedWeight(t, tId) * Similarity.stringSimilarity(s,t, measure);
						maxSim = Math.max(maxSim,sim);
					}
				}
			}
		}
		complete++;
		if(showProgress)
			printProgress();
		return new Mapping(sId, tId, maxSim * CORRECTION, MappingRelation.EQUIVALENCE);
	}
	
//Private Methods
	
	private Alignment extendChildrenAndParents(Alignment a, double thresh)
	{
		SemanticMap rels = SemanticMap.getInstance();
		Map2Set<String,String> toMap = new Map2Set<String,String>();
		for(Mapping input : a)
		{
			if(rels.isClass(input.getEntity1()) && rels.isClass(input.getEntity2()))
			{
				Set<String> sourceChildren = rels.getSubclasses(input.getEntity1(),1,false);
				Set<String> targetChildren = rels.getSubclasses(input.getEntity2(),1,false);
				for(String s : sourceChildren)
				{
					if(a.containsSource(s))
						continue;
					for(String t : targetChildren)
					{
						if(!a.containsTarget(t))
							toMap.add(s,t);
					}
				}
				Set<String> sourceParents = rels.getSuperclasses(input.getEntity1(),1,false);
				Set<String> targetParents = rels.getSuperclasses(input.getEntity2(),1,false);
				for(String s : sourceParents)
				{
					if(a.containsSource(s))
						continue;
					for(String t : targetParents)
					{
						if(!a.containsTarget(t))
							toMap.add(s, t);
					}
				}
			}
		}
		return mapInParallel(toMap,thresh);
	}
	
	private Alignment extendNeighbors(Alignment a, double thresh)
	{
		SemanticMap rels = SemanticMap.getInstance();
		Map2Set<String,String> toMap = new Map2Set<String,String>();
		for(Mapping m : a)
		{
			String src = m.getEntity1();
			String tgt = m.getEntity2();
			if(!rels.isIndividual(src))
				continue;
			Set<String> sourceChildren = rels.getIndividualActiveRelations(src);
			Set<String> targetChildren = rels.getIndividualActiveRelations(tgt);
			for(String s : sourceChildren)
			{
				if(a.containsSource(s) || !rels.isIndividual(s))
					continue;
				for(String t : targetChildren)
				{
					if(a.containsTarget(t))
						continue;
					boolean checkRels = false;
					for(String r1 : rels.getIndividualProperties(src, s))
					{
						if(checkRels)
							break;
						for(String r2 : rels.getIndividualProperties(tgt, t))
						{
							if(r1.equals(r2) || a.contains(r1, r2))
							{
								checkRels = true;
								break;
							}
						}
					}
					if(checkRels)
						toMap.add(s,t);
				}
			}
			Set<String> sourceParents = rels.getIndividualPassiveRelations(src);
			Set<String> targetParents = rels.getIndividualPassiveRelations(tgt);
			for(String s : sourceParents)
			{
				if(a.containsSource(s))
					continue;
				for(String t : targetParents)
				{
					if(a.containsTarget(t))
						continue;
					boolean checkRels = false;
					for(String r1 : rels.getIndividualProperties(s, src))
					{
						if(checkRels)
							break;
						for(String r2 : rels.getIndividualProperties(t, tgt))
						{
							if(r1.equals(r2) || a.contains(r1, r2))
							{
								checkRels = true;
								break;
							}
						}
					}
					if(checkRels)
						toMap.add(s, t);
				}
			}
		}
		return mapInParallel(toMap,thresh);
	}
	
	private Alignment extendSiblings(Alignment a, double thresh)
	{		
		SemanticMap rels = SemanticMap.getInstance();
		Map2Set<String,String> toMap = new Map2Set<String,String>();
		for(Mapping input : a)
		{
			if(rels.isClass(input.getEntity1()) && rels.isClass(input.getEntity2()))
			{
				Set<String> sourceSiblings = rels.getSiblings(input.getEntity1());
				Set<String> targetSiblings = rels.getSiblings(input.getEntity2());
				if(sourceSiblings.size() > 200 || targetSiblings.size() > 200)
					continue;
				for(String s : sourceSiblings)
				{
					if(a.containsSource(s))
						continue;
					for(String t : targetSiblings)
					{
						if(!a.containsTarget(t))
							toMap.add(s, t);
					}
				}
			}
		}
		return mapInParallel(toMap,thresh);
	}
}