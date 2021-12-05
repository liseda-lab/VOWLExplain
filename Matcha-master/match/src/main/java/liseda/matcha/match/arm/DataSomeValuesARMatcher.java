/*******************************************************************************
 * Association rule-based matcher that finds class - data some values mappings *
 * based on their shared individuals.                                          *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.HashSet;
import java.util.Set;

import liseda.matcha.ontology.AttributeMap;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.DataSomeValues;
import liseda.matcha.semantics.owl.Datatype;
import liseda.matcha.semantics.owl.SimpleClass;
import liseda.matcha.semantics.owl.SimpleDataProperty;

public class DataSomeValuesARMatcher extends AbstractARMatcher
{

//Attributes
	
	protected static final String DESCRIPTION = "Matches classes to DataSomeValues restrictions, based on their shared individuals";
	protected static final String NAME = "DataSomeValues Association Rule Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS_EXPRESSION};
	
//Constructor

	public DataSomeValuesARMatcher()
	{
		super();
	}

//Protected methods
	
	@Override
	protected void computeSupport(Ontology o1, Ontology o2) 
	{
		Set<String> sharedInd = ARMap.getSharedIndividuals(o1,o2);
		SemanticMap sMap = SemanticMap.getInstance();
		AttributeMap srcAttributes = o1.getAttributeMap();
		AttributeMap tgtAttributes = o2.getAttributeMap();
		//Iterate through the shared individuals
		for(String si : sharedInd) 
		{
			//Get their classes
			Set<String> cSet = sMap.getIndividualClassesTransitive(si);
			if(cSet.isEmpty())
				continue;
			//And their data property occurrences
			Set<String> srcProps = srcAttributes.getProperties(si);
			srcProps.retainAll(o1.getEntities(EntityType.DATA_PROP));
			Set<String>	tgtProps = tgtAttributes.getProperties(si);
			tgtProps.retainAll(o1.getEntities(EntityType.DATA_PROP));
			//Find all property - data type patterns for the individual in both ontologies
			Set<DataSomeValues> sourcePatterns = findPatterns(si, srcProps, srcAttributes);
			Set<DataSomeValues> targetPatterns = findPatterns(si, tgtProps, tgtAttributes);
			//For each class, increment the rule support for all pairs of that class with patterns from the other ontology
			for(String cURI: cSet) 
			{
				SimpleClass c = new SimpleClass(cURI);
				rules.incrementEntitySupport(c);

				if(o1.contains(cURI))
					for(DataSomeValues dsv: targetPatterns)
						rules.incrementRuleSupport(c, dsv);
				else if(o2.contains(cURI))
					for(DataSomeValues dsv: sourcePatterns)
						rules.incrementRuleSupport(c, dsv);
			}	
			//Increment entity support for all patterns
			for(DataSomeValues dsv: sourcePatterns)
				rules.incrementEntitySupport(dsv);
			for(DataSomeValues dsv: targetPatterns)
				rules.incrementEntitySupport(dsv);
		}
	}
	
//Private Methods

	private Set<DataSomeValues> findPatterns(String ind, Set<String> properties, AttributeMap map) 
	{
		Set<DataSomeValues> patterns = new HashSet<DataSomeValues>();
		for(String pURI: properties) 
		{
			SimpleDataProperty p = new SimpleDataProperty(pURI);
			String range = SemanticMap.getInstance().getRange(pURI);

			for(String value: map.getValues(ind, pURI)) 
			{
				String dataType = map.getDataType(ind, pURI, value);
				//Note: we don't want to create a DataSomeValues restriction if the datatype
				//corresponds to the range of property; in that case, an unqualified
				//DataMinCardinality restriction is more appropriate
				if(range.equals(dataType.toString()))
					continue;
				Datatype dt = new Datatype(dataType.toString());
				DataSomeValues dsv = new DataSomeValues(p, dt);
				patterns.add(dsv);
			}
		}
		return patterns;
	}
}