/*******************************************************************************
 * Association rule-based matcher that finds simple class mappings based on    *
 * their shared individuals.                                                   *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.SimpleClass;

public class ClassARMatcher extends AbstractARMatcher 
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches classes based on their shared individuals";
	protected static final String NAME = "Class Association Rule Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS};
	
//Constructor

	public ClassARMatcher()
	{
		super();
	}

//Protected Methods

	@Override
	protected void computeSupport(Ontology o1, Ontology o2) 
	{
		Set<String> sharedInd = ARMap.getSharedIndividuals(o1,o2);
		SemanticMap rels = SemanticMap.getInstance();
		//Iterate through the shared individuals
		for(String si : sharedInd) 
		{	
			//Iterate through all classes associated with the individual
			List<String> cList = new ArrayList<String>(rels.getIndividualClassesTransitive(si));
			for(int i = 0; i < cList.size(); i++) 
			{
				//Create a simple class with one
				SimpleClass c1 = new SimpleClass(cList.get(i));
				//Increment the entity support for it
				rules.incrementEntitySupport(c1);
				//Iterate through the others
				for (int j = i + 1; j < cList.size(); j++) 
				{
					//Create a simple class with the other
					SimpleClass c2 = new SimpleClass(cList.get(j));
					// Make sure we are not mapping entities from the same ontology
					if((o1.contains(cList.get(j)) && o2.contains(cList.get(i)))
							| (o2.contains(cList.get(j)) && o1.contains(cList.get(i))))
						//Increment the rule support
						rules.incrementRuleSupport(c1, c2);
				}
			}
		}
	}
}