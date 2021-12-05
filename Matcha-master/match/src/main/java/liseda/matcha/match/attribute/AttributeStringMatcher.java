/******************************************************************************
* Matching algorithm that maps individuals by comparing their ValueMap        *
* entries through the ISub string similarity metric.                          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.attribute;

import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.match.AbstractParallelMatcher;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.similarity.Similarity;

public class AttributeStringMatcher extends AbstractParallelMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches individuals by comparing their ValueMap\n" +
											  "entries using the ISub string similarity metric";
	protected static final String NAME = "Value String Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.INDIVIDUAL};
	
//Constructors
	
	public AttributeStringMatcher()
	{
		super();
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}
	
//Protected Methods
		
	@Override
	protected Mapping mapTwoEntities(String sId, String tId)
	{
		double dataSim = 0.0;
		for(String sd : source.getAttributeMap().getProperties(sId))
		{
			for(String sv : source.getAttributeMap().getValues(sId,sd))
			{
				for(String td : target.getAttributeMap().getProperties(tId))
				{
					double weight = (td==sd ? 1.0 : 0.9);
					for(String tv : target.getAttributeMap().getValues(tId,td))
						dataSim = Math.max(Similarity.nameSimilarity(sv, tv, null) * weight, dataSim);
				}
			}
		}
		return new Mapping(sId, tId, dataSim, MappingRelation.EQUIVALENCE);
	}
}