/******************************************************************************
* A data property expression.                                                 *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.EntityType;

public abstract class DataPropertyExpression extends AbstractExpression implements PropertyExpression
{
//Constructors
	
	protected DataPropertyExpression()
	{
		super();
	}
	
//Public Methods
	
	@Override
	public EntityType getEntityType()
	{
		return EntityType.DATA_EXPRESSION;
	}
}