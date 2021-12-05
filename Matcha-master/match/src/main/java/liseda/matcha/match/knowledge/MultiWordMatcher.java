/******************************************************************************
* Matches Ontologies by finding partial matches between their names with      *
* either 2 or 3 words. It checks whether the words are equal, synonyms in     *
* WordNet, or have a high Wu-Palmer score.                                    *
*                                                                             *
* WARNING: This matching algorithm takes O(N^2) time, and thus should be used *
* only to match small ontologies.                                             *
*                                                                             *
* @author Daniel Faria, Amruta Nanavaty                                       *
******************************************************************************/
package liseda.matcha.match.knowledge;

import java.util.HashSet;
import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.io.ResourceManager;
import liseda.matcha.match.AbstractParallelMatcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.ontology.lexicon.WordLexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.settings.StopList;
import liseda.matcha.similarity.WordNet;

public class MultiWordMatcher extends AbstractParallelMatcher //TODO: Extend this to generate subsumption mappings
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches entities that have English Lexicon\n" +
											  "entries with two words where one word is\n"+
											  "shared between them and the other word is\n" + 
											  "related through WordNet.";
	protected static final String NAME = "Multi-Word Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS,EntityType.INDIVIDUAL,EntityType.DATA_PROP,EntityType.OBJECT_PROP};
	private WordNet wn;
	//The Lexicons
	private Lexicon sourceLex, targetLex;
	private WordLexicon sourceWLex, targetWLex;
	//The language
	private static final String LANG = "en";
	//The confidence score
	private final double CONFIDENCE = 0.9;

//Constructors
	
	public MultiWordMatcher()
	{
		super();
		wn = new WordNet(ResourceManager.getWordNetRoot()); //TODO: throw and handle exception if not found
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}

//Public Methods
	
	@Override
	public Alignment match(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		sourceLex = o1.getLexicon(e);
		targetLex = o2.getLexicon(e);
		sourceWLex = o1.getWordLexicon(e, LANG);
		targetWLex = o2.getWordLexicon(e, LANG);
		
		System.out.println("Running Multi-Word Matcher");
		long time = System.currentTimeMillis()/1000;
		Alignment a = super.match(o1, o2, e, thresh);
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
		return a;
	}

//Protected Methods
	
	@Override
	protected Mapping mapTwoEntities(String uri1, String uri2)
	{
		double maxSim = 0.0;
		for(String sName : sourceLex.getNamesWithLanguage(uri1,LANG))
		{
			String[] sWords = sName.split(" ");
			if(sWords.length < 2 || sWords.length > 3)
				continue;
			
			for(String tName : targetLex.getNamesWithLanguage(uri2,LANG))
			{
				if(sName.equals(tName))
					continue;
				String[] tWords = tName.split(" ");
				if(tWords.length < 2 || tWords.length > 3 || sWords.length != tWords.length)
					continue;
				double sim = 0.0;
				for(int i = 0; i < sWords.length; i++)
				{
					String sw = sWords[i];
					String tw = tWords[i];
					if(StopList.contains(sw) || StopList.contains(tw))
						continue;
					if(sw.equals(tw))
					{
						sim++;
						continue;
					}
					HashSet<String> sList = getAllWordForms(sw);
					HashSet<String> tList = getAllWordForms(tw);
					if(sList.contains(tw) || tList.contains(sw))
					{
						sim+=0.8;
						continue;
					}
					if(sourceWLex.getWordEC(sw) < 0.75 && targetWLex.getWordEC(tw) < 0.75)
					{
						double score = wn.wuPalmerScore(sw,tw);
						if(score > 0.5)
							sim+=0.5;
					}
				}
				if(sim < 1.5)
					continue;
				sim /= sWords.length;
				double finalSim = sim * sourceLex.getCorrectedWeight(sName, uri1) *
								targetLex.getCorrectedWeight(tName, uri2);
				if(finalSim > maxSim)
					maxSim = finalSim;
			}
		}
		return new Mapping(uri1,uri2,maxSim*CONFIDENCE,MappingRelation.EQUIVALENCE);
	}
	
//Private Methods
	
	private HashSet<String> getAllWordForms(String s)
	{
		HashSet<String> wordForms = wn.getAllWordForms(s);
		wordForms.addAll(wn.getHypernyms(s));
		return wordForms;
	}
}