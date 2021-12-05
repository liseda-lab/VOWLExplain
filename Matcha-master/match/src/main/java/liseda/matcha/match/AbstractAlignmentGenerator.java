/******************************************************************************
* Abstract class that englobes all algorithms for ontology mapping generation *
* (Matchers, Extenders and Rematchers) with EntityType checking methods.      *
*                                                                             *
* @authors Daniel Faria                                                       *
******************************************************************************/
package liseda.matcha.match;

import liseda.matcha.semantics.EntityType;

public abstract class AbstractAlignmentGenerator
{

//Attributes

	//The description of this matcher
	protected String description = "";
	//The name of this alignment generator
	protected String name = "Abstract Alignment Generator";
	//The support (empty since this cannot actually generate alignments)
	protected EntityType[] support = {}; //TODO: Update all subclasses to generate exceptions when asked to match unsupported types
	
//Public Methods
	
	/**
	 * @return this Matcher's textual description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @return this Matcher's name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return the list of EntityTypes supported by this Matcher
	 */
	public EntityType[] getSupportedEntityTypes()
	{
		return support;
	}
	
	
//Protected Methods
	
	protected boolean checkEntityType(EntityType e)
	{
		boolean check = false;
		for(EntityType t : support)
		{
			if(t.equals(e))
			{
				check = true;
				break;
			}
		}
		return check;
	}
}