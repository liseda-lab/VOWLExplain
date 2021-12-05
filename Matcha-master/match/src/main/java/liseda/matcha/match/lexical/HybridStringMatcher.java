/******************************************************************************
* Matching algorithm that maps Ontology entities by comparing their Lexicon   *
* entries through String- and Word-Matching algorithms with the optional use  *
* of WordNet.                                                                 *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.lexical;

import java.util.HashSet;
import java.util.Set;

import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.io.ResourceManager;
import liseda.matcha.match.AbstractParallelMatcher;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.settings.LanguageSetting;
import liseda.matcha.settings.Settings;
import liseda.matcha.similarity.Similarity;
import liseda.matcha.similarity.WordNet;

public class HybridStringMatcher extends AbstractParallelMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches entities by comparing their Lexicon\n" +
											  "entries through a combination of string- and\n" +
											  "word-matching algorithms, with the optional\n" +
											  "use of WordNet";
	protected static final String NAME = "Hybrid String Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS,EntityType.DATA_PROP,EntityType.INDIVIDUAL,EntityType.OBJECT_PROP};
	private WordNet wordNet;
	
//Constructors
	
	public HybridStringMatcher(boolean useWordNet)
	{
		super();
		if(useWordNet)
			wordNet = new WordNet(ResourceManager.getWordNetRoot()); //TODO: Throw and catch exception if not found
		else
			wordNet = null;
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}
	
	
//Protected Methods
	
	@Override
	protected Mapping mapTwoEntities(String sId, String tId)
	{
		double maxSim = 0.0;
		
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
				if(sourceNames != null && targetNames != null)
					for(String s : sourceNames)
						for(String t : targetNames)
							maxSim = Math.max(maxSim,Similarity.nameSimilarity(s,t,wordNet));
			}
		}
		else
		{
			Set<String> sourceNames = sLex.getNames(sId);
			Set<String> targetNames = tLex.getNames(tId);
			if(sourceNames != null && targetNames != null)
				for(String s : sourceNames)
					for(String t : targetNames)
						maxSim = Math.max(maxSim,Similarity.nameSimilarity(s,t,wordNet));
		}
		return new Mapping(sId, tId, maxSim, MappingRelation.EQUIVALENCE);
	}
}