/*******************************************************************************
 * Association rule-based matcher that finds class - object has value mappings *
 * based on their shared individuals.                                          *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.HashSet;
import java.util.Set;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.Individual;
import liseda.matcha.semantics.owl.ObjectHasValue;
import liseda.matcha.semantics.owl.SimpleClass;
import liseda.matcha.semantics.owl.SimpleObjectProperty;



public class ObjectHasValueARMatcher extends AbstractARMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches classes to ObjectHasValue restrictions, based on their shared individuals";
	protected static final String NAME = "ObjectHasValue Association Rule Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS_EXPRESSION};
	private SemanticMap rels;
	
//Constructor

	public ObjectHasValueARMatcher()
	{
		super();
		rels = SemanticMap.getInstance();
	}

//Protected methods
	
	@Override
	protected void computeSupport(Ontology o1, Ontology o2) 
	{
		Set<String> sharedInd = ARMap.getSharedIndividuals(o1,o2);
		//Iterate through the shared individuals
		for(String si : sharedInd) 
		{
			//Find all classes associated to the individual
			Set<String> iClasses = rels.getIndividualClassesTransitive(si);
			if(iClasses.isEmpty())
				continue;
			//Find all object property - class patterns for the individual
			Set<ObjectHasValue> patterns = findPatterns(si);
			if(patterns.isEmpty())
				continue;
			//For each class
			for(String cURI: iClasses) 
			{
				SimpleClass c = new SimpleClass(cURI);
				//icrement entity support
				rules.incrementEntitySupport(c);
				//For each pattern
				for(ObjectHasValue ohv: patterns) 
				{
					//Check that the class and pattern are from opposite ontologies
					Set<String> elements = ohv.getElements();
					if((o1.contains(cURI) && !o2.containsAll(elements)) || (o2.contains(cURI) && !o1.containsAll(elements)))
						continue;
					//and increment rule support
					rules.incrementRuleSupport(c, ohv);
				}
			}
			//Increment entity support for all patterns
			for(ObjectHasValue ohv: patterns) 
				rules.incrementEntitySupport(ohv);
		}
	}
	
//Private Methods
	
	private Set<ObjectHasValue> findPatterns(String ind) 
	{
		Set<ObjectHasValue> patterns = new HashSet<ObjectHasValue>();
		Set<String> activeRelations = rels.getIndividualActiveRelations(ind);
		for(String iURI: activeRelations) 
		{
			Individual i = new Individual(iURI);
			for(String pURI : rels.getIndividualProperties(ind, iURI))
				patterns.add(new ObjectHasValue(new SimpleObjectProperty(pURI), i));
		}
		return patterns;
	}
}