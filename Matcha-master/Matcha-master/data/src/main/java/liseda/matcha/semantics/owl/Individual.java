/******************************************************************************
* An individual.                                                              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Expression;

public class Individual extends AbstractExpression implements ValueExpression
{

//Constructor
	
	/**
	 * Constructs a new IndividualId from the given uri
	 * @param uri: the URI of the class
	 */
	public Individual(String uri)
	{
		super();
		elements.add(uri);
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Individual &&
				((Individual)o).elements.equals(this.elements);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return null;
	}
	
	@Override
	public EntityType getEntityType()
	{
		return EntityType.INDIVIDUAL;
	}
	
	@Override
	public String toString()
	{
		return elements.iterator().next();
	}
}