/*******************************************************************************
 * Association rule-based matcher that finds simple object property mappings   *
 * based on their shared individuals.                                          *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.Set;
import java.util.Vector;

import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.SimpleObjectProperty;

public class ObjectPropertyARMatcher extends AbstractARMatcher
{

//Attributes
	
	protected static final String DESCRIPTION = "Matches object properties based on their shared individuals";
	protected static final String NAME = "Object Property Association Rule Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.OBJECT_PROP};
	
	
// Constructor
	
	public ObjectPropertyARMatcher()
	{
		super();
	}

//Protected methods
	
	protected void computeSupport(Ontology o1, Ontology o2) 
	{
		System.out.println("Get property support");
		Set<String> sharedInd = ARMap.getSharedIndividuals(o1,o2);
		SemanticMap rels = SemanticMap.getInstance();
		
		for(String si : sharedInd)
		{
			//Get related individuals
			Set<String> relIndividuals = rels.getIndividualActiveRelations(si);
			for(String ri : relIndividuals)
			{
				//Get the object properties relating them
				Vector<String> rList = new Vector<String>(rels.getIndividualProperties(si, ri));
				int len = rList.size();
				//Iterate through the object properties
				for(int i = 0; i < len; i++) 
				{
					//Create a simple object property object
					SimpleObjectProperty r1 = new SimpleObjectProperty(rList.get(i));
					//Increment its entity support
					rules.incrementEntitySupport(r1);
					//Iterate through every other object property
					for(int j = i+1; j < len; j++) 
					{
						//Create a simple object property object
						SimpleObjectProperty r2 = new SimpleObjectProperty(rList.get(j));
						//Make sure we are not mapping entities from the same ontology
						if((o1.contains(rList.get(j)) && o2.contains(rList.get(i))) ||
								(o2.contains(rList.get(j)) && o1.contains(rList.get(i))))
							//Increment the rule support
							rules.incrementRuleSupport(r1, r2);
					}			
				}
			}		
		}
	}
}