/*******************************************************************************
 * Matches Individuals by finding literal matches between the values of their  *
 * Annotation and Data Properties, as stored in the AttributMap.               *
 *                                                                             *
 * @author Daniel Faria                                                        *
 ******************************************************************************/
package liseda.matcha.match.attribute;

import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.match.AbstractHashMatcher;
import liseda.matcha.ontology.AttributeMap;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.InstanceMatchingCategory;
import liseda.matcha.settings.Settings;

public class AttributeMatcher extends AbstractHashMatcher
{

//Attributes

	protected static final String DESCRIPTION = "Matches individuals that have equal values for\n" +
											  "the same Annotation or Data Property, or for\n" +
											  "for matching properties (in secondary mode)";
	protected static final String NAME = "Value Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.INDIVIDUAL};

//Constructors

	public AttributeMatcher()
	{
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}
	
//Protected Methods

	@Override
	protected Alignment hashMatch(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		Alignment maps = new Alignment(o1,o2);
		AttributeMap sVal = o1.getAttributeMap();
		AttributeMap tVal = o2.getAttributeMap();
		Settings set = Settings.getInstance();
		for(String i : sVal.getProperties())
		{
			if(!tVal.getProperties().contains(i))
				continue;

			for(String s : sVal.getValues(i))
			{
				if(!tVal.getValues(i).contains(s))
					continue;
				Set<String> sourceIndexes = sVal.getIndividuals(i,s);
				sourceIndexes.retainAll(set.getIndividualsToMatch());
				Set<String> targetIndexes = tVal.getIndividuals(i,s);
				targetIndexes.retainAll(set.getIndividualsToMatch());
				int count = Math.min(sourceIndexes.size(), targetIndexes.size());
				for(String j : sourceIndexes)
				{
					for(String k : targetIndexes)
					{
						if(set.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_CLASSES) &&
								!SemanticMap.getInstance().shareClass(j,k))
							continue;
						double similarity = maps.getSimilarity(j, k);
						similarity = Math.max(similarity, 1.0/count);

						if(similarity >= thresh)
							maps.add(new Mapping(j, k, similarity, MappingRelation.EQUIVALENCE));
					}
				}
			}
		}
		return maps;
	}
}