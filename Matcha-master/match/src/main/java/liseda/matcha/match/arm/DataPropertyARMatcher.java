/*******************************************************************************
 * Association rule-based matcher that finds simple property mappings based on *
 * their shared individuals.                                                   *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.Set;

import liseda.matcha.ontology.AttributeMap;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.owl.SimpleDataProperty;

public class DataPropertyARMatcher extends AbstractARMatcher 
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches data properties based on their shared individuals";
	protected static final String NAME = "Data Property Association Rule Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.DATA_PROP};
	
//Constructor
	
	public DataPropertyARMatcher()
	{
		super();
	}

//Protected methods

	@Override
	protected void computeSupport(Ontology o1, Ontology o2) 
	{
		Set<String> sharedInd = ARMap.getSharedIndividuals(o1, o2);
		AttributeMap srcAttributes = o1.getAttributeMap();
		AttributeMap tgtAttributes = o2.getAttributeMap();

		for(String si : sharedInd) 
		{
			//Find all data properties associated to the individual
			Set<String> srcDataProperties = srcAttributes.getProperties(si);
			Set<String> tgtDataProperties = tgtAttributes.getProperties(si);

			//Iterate through the source data properties
			for(String srcUri : srcDataProperties) 
			{
				SimpleDataProperty p1 = new SimpleDataProperty(srcUri);
				rules.incrementEntitySupport(p1);

				Set<String> srcValues = srcAttributes.getValues(si, srcUri);
				if(srcValues.isEmpty())
					continue;
				for(String tgtUri: tgtDataProperties) 
				{
					Set<String> tgtValues = tgtAttributes.getValues(si, tgtUri);
					//Only map properties if they have values of the same datatype
					//TODO: revise this - matching the value matters more than the datatype
					//(we could have numbers encoded as plain literals, etc)
					for(String srcV: srcValues)
					{
						for(String tgtV: tgtValues) 
						{
							if(srcAttributes.getDataType(si, srcUri, srcV).equals(tgtAttributes.getDataType(si, tgtUri, tgtV)))
							{
								SimpleDataProperty p2 = new SimpleDataProperty(tgtUri);
								rules.incrementRuleSupport(p1, p2);
								break;
							}
						}
					}
				} 
			}
			//Also increment entity support for target data properties
			for (String tgtUri : tgtDataProperties) 
			{
				SimpleDataProperty p = new SimpleDataProperty(tgtUri);
				rules.incrementEntitySupport(p);
			}
		}
	}
}