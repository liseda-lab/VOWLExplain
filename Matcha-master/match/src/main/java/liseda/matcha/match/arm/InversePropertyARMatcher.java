/*******************************************************************************
 * Association rule-based matcher that finds object property - inverse object  *
 * property mappings based on their shared individuals.                        *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.Set;

import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.InverseProperty;
import liseda.matcha.semantics.owl.SimpleObjectProperty;



public class InversePropertyARMatcher extends AbstractARMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches object properties to their inverse, based on their shared individuals";
	protected static final String NAME = "Inverse Property Association Rule Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.OBJECT_EXPRESSION};
	
//Constructor

	public InversePropertyARMatcher()
	{
		super();
	}

//Protected methods
	
	protected void computeSupport(Ontology o1, Ontology o2) 
	{
		Set<String> sharedInd = ARMap.getSharedIndividuals(o1,o2);
		SemanticMap map = SemanticMap.getInstance();
		//Iterate through the shared individuals
		for(String si : sharedInd) 
		{
			//Iterate through their related individuals
			for(String ri: map.getIndividualActiveRelations(si)) 
			{
				//Iterate through the object properties relating them directly
				for(String direct : map.getIndividualProperties(si, ri))
				{
					SimpleObjectProperty r1 = new SimpleObjectProperty(direct);
					rules.incrementEntitySupport(r1);
					//And through the object properties relating them inversely
					for(String inverse: map.getIndividualProperties(ri, si)) 
					{
						// Make sure we are not mapping entities from the same ontology
						if((o1.contains(direct) && o2.contains(inverse)) || 
								(o2.contains(direct) && o2.contains(inverse)))
						{							
							//Create the inverse property and increment the rule support
							InverseProperty r2Inv = new InverseProperty(new SimpleObjectProperty(inverse));
							rules.incrementRuleSupport(r1, r2Inv);
						}
					}
				}	
				//Compute the entity support of inverse properties
				for(String inverse: map.getIndividualProperties(ri, si)) 
				{
					InverseProperty r2Inv = new InverseProperty(new SimpleObjectProperty(inverse));
					rules.incrementEntitySupport(r2Inv);
				}
			}
		}
	}
}