/******************************************************************************
* Matches Ontologies by finding literal full-name matches between their       *
* Lexicons. Weighs matches according to the provenance of the names.          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.lexical;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.data.Map2Set;
import liseda.matcha.match.AbstractHashMatcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.InstanceMatchingCategory;
import liseda.matcha.settings.Settings;

public class SpacelessLexicalMatcher extends AbstractHashMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches entities that have one or more exact\n" +
											  "String matches between their Lexicon entries\n" +
											  "after removing their white spaces";
	protected static final String NAME = "Spaceless Lexical Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS,EntityType.INDIVIDUAL,EntityType.DATA_PROP,EntityType.OBJECT_PROP};
	private static final double WEIGHT = 0.99;
		
//Constructors

	public SpacelessLexicalMatcher()
	{
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}
	
//Protected Methods

	@Override
	protected Alignment hashMatch(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		//Initialize the alignment
		Alignment maps = new Alignment(o1,o2);
		//Get the lexicons of the source and target Ontologies
		Lexicon sLex = o1.getLexicon(e);
		Lexicon tLex = o2.getLexicon(e);
		Settings set = Settings.getInstance();
		//Create spaceless lexicons
		Map2Set<String,String> sourceConv = new Map2Set<String,String>();
		for(String n : sLex.getNames())
			sourceConv.add(n.replace(" ", ""), n);
		Map2Set<String,String> targetConv = new Map2Set<String,String>();
		for(String n : tLex.getNames())
			targetConv.add(n.replace(" ", ""), n);
		for(String c : sourceConv.keySet())
		{
			if(!targetConv.contains(c))
				continue;
			for(String s : sourceConv.get(c))
			{
				for(String i : sLex.getEntities(s))
				{
					if(e.equals(EntityType.INDIVIDUAL) && !set.isToMatch(i))
						continue;
					double weight = sLex.getCorrectedWeight(s, i) * WEIGHT;
					for(String t : targetConv.get(c))
					{
						for(String j : tLex.getEntities(t))
						{
							if(e.equals(EntityType.INDIVIDUAL) && (!set.isToMatch(j) ||
									(set.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_CLASSES) &&
									!SemanticMap.getInstance().shareClass(i,j))))
								continue;
							double similarity = tLex.getCorrectedWeight(t, j) * weight;
							if(similarity >= thresh)
								maps.add(new Mapping(i, j, similarity, MappingRelation.EQUIVALENCE));
						}
					}
				}
			}
		}
		return maps;
	}
}