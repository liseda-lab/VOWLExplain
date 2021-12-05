/*******************************************************************************
 * Association rule-based matcher that finds class - object some values        *
 * mappings based on their shared individuals.                                 *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.HashSet;
import java.util.Set;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.ObjectSomeValues;
import liseda.matcha.semantics.owl.SimpleClass;
import liseda.matcha.semantics.owl.SimpleObjectProperty;



public class ObjectSomeValuesARMatcher extends AbstractARMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches classes to ObjectSomeValues restrictions, based on their shared individuals";
	protected static final String NAME = "ObjectSomeValues Association Rule Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS_EXPRESSION};
	private SemanticMap rels;
	
//Constructor

	public ObjectSomeValuesARMatcher()
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
			Set<ObjectSomeValues> patterns = findPatterns(si);
			if(patterns.isEmpty())
				continue;
			//For each class
			for(String cURI: iClasses) 
			{
				SimpleClass c = new SimpleClass(cURI);
				//icrement entity support
				rules.incrementEntitySupport(c);
				//For each pattern
				for(ObjectSomeValues osv: patterns) 
				{
					//Check that the class and pattern are from opposite ontologies
					Set<String> elements = osv.getElements();
					if((o1.contains(cURI) && !o2.containsAll(elements)) || (o2.contains(cURI) && !o1.containsAll(elements)))
						continue;
					//and increment rule support
					rules.incrementRuleSupport(c, osv);
				}
			}
			//Increment entity support for all patterns
			for(ObjectSomeValues osv: patterns) 
				rules.incrementEntitySupport(osv);
		}
	}
	
//Private Methods
	
	private Set<ObjectSomeValues> findPatterns(String ind) 
	{
		Set<ObjectSomeValues> patterns = new HashSet<ObjectSomeValues>();
		Set<String> activeRelations = rels.getIndividualActiveRelations(ind);
		for(String iURI: activeRelations) 
		{
			Set<String> iClasses = rels.getIndividualClassesTransitive(iURI);
			for(String cURI : iClasses)
			{
				SimpleClass c = new SimpleClass(cURI);
				for(String pURI : rels.getIndividualProperties(ind, iURI))
				{
					SimpleObjectProperty p = new SimpleObjectProperty(pURI);
					if(rels.isInRange(cURI,pURI) && (rels.getRange(pURI) == null || !rels.getRange(pURI).equals(cURI)))
						patterns.add(new ObjectSomeValues(p, c));
				}
			}
		}
		return patterns;
	}
}