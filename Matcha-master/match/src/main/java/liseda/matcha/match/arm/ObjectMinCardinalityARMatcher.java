/*******************************************************************************
 * Association rule-based matcher that finds class - object min cardinality 1  *
 * (unqualified) mappings based on their shared individuals.                   *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.HashSet;
import java.util.Set;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.Cardinality;
import liseda.matcha.semantics.owl.ObjectMinCardinality;
import liseda.matcha.semantics.owl.SimpleClass;
import liseda.matcha.semantics.owl.SimpleObjectProperty;



public class ObjectMinCardinalityARMatcher extends AbstractARMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches classes to ObjectMinCardinality restrictions, based on their shared individuals";
	protected static final String NAME = "ObjectMinCardinality Association Rule Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS_EXPRESSION};
	private SemanticMap rels;
	
//Constructor

	public ObjectMinCardinalityARMatcher()
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
			Set<ObjectMinCardinality> patterns = findPatterns(si);
			if(patterns.isEmpty())
				continue;
			//For each class
			for(String cURI: iClasses) 
			{
				SimpleClass c = new SimpleClass(cURI);
				//icrement entity support
				rules.incrementEntitySupport(c);
				//For each pattern
				for(ObjectMinCardinality omc: patterns) 
				{
					//Check that the class and pattern are from opposite ontologies
					Set<String> elements = omc.getElements();
					if((o1.contains(cURI) && !o2.containsAll(elements)) || (o2.contains(cURI) && !o1.containsAll(elements)))
						continue;
					//and increment rule support
					rules.incrementRuleSupport(c, omc);
				}
			}
			//Increment entity support for all patterns
			for(ObjectMinCardinality omc: patterns) 
				rules.incrementEntitySupport(omc);
		}
	}
	
//Private Methods
	
	private Set<ObjectMinCardinality> findPatterns(String ind) 
	{
		Set<String> props = new HashSet<String>();
		for(String iURI: rels.getIndividualActiveRelations(ind))
			props.addAll(rels.getIndividualProperties(ind, iURI));
		Set<ObjectMinCardinality> patterns = new HashSet<ObjectMinCardinality>();
		for(String pURI : props)
			patterns.add(new ObjectMinCardinality(new SimpleObjectProperty(pURI), new Cardinality(1)));
		return patterns;
	}
}