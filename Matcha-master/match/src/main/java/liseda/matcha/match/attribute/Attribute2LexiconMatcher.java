/******************************************************************************
* Matching algorithm that maps individuals by comparing the Lexicon entries   *
* of one with the ValueMap entries of the other using a combination of        *
* String- and Word-Matching algorithms and optionally the WordNet.            *
*                                                                             *
* @author Daniel Faria, Catia Pesquita                                        *
******************************************************************************/
package liseda.matcha.match.attribute;

import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.io.ResourceManager;
import liseda.matcha.match.AbstractParallelMatcher;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.similarity.Similarity;
import liseda.matcha.similarity.WordNet;

public class Attribute2LexiconMatcher extends AbstractParallelMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches individuals by comparing the Lexicon\n" +
											  "entries of one to the ValueMap entries of the\n" +
											  "other using a combination of string- and word-\n" +
											  "matching algorithms, and optionally the WordNet";
	protected static final String NAME = "Value-to-Lexicon Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.INDIVIDUAL};
	private WordNet wordNet = null;
	
//Constructors
	
	public Attribute2LexiconMatcher(boolean useWordNet)
	{
		super();
		if(useWordNet)
			this.wordNet = new WordNet(ResourceManager.getWordNetRoot()); //TODO: throw and handle exception if not found
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}
	
//Protected Methods
	
	//Computes the maximum String similarity between two Classes by doing a
	//pairwise comparison of all their names
	protected Mapping mapTwoEntities(String sId, String tId)
	{
		double crossSim = 0;
		for(String n1 : source.getLexicon(toMatch).getNames(sId))
			for(String td : target.getAttributeMap().getProperties(tId))
				for(String tv : target.getAttributeMap().getValues(tId,td))
					crossSim = Math.max(crossSim,Similarity.nameSimilarity(n1,tv,wordNet));
		for(String n2 : target.getLexicon(toMatch).getNames(tId))
			for(String sd : source.getAttributeMap().getProperties(sId))
				for(String sv : source.getAttributeMap().getValues(sId,sd))
					crossSim = Math.max(crossSim,Similarity.nameSimilarity(n2,sv,wordNet));
		return new Mapping(sId, tId, crossSim, MappingRelation.EQUIVALENCE);
	}
}