/******************************************************************************
* An object property expression.                                              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.EntityType;

public abstract class ObjectPropertyExpression extends AbstractExpression implements PropertyExpression
{
//Constructors
	
	protected ObjectPropertyExpression()
	{
		super();
	}
	
//Public Methods
	
	@Override
	public EntityType getEntityType()
	{
		return EntityType.OBJECT_EXPRESSION;
	}
}