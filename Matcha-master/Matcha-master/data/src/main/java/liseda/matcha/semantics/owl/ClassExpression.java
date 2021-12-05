/******************************************************************************
* A class expression.                                                         *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.EntityType;

public abstract class ClassExpression extends AbstractExpression
{
	
//Constructors
	
	protected ClassExpression()
	{
		super();
	}
	
//Public Methods
	
	@Override
	public EntityType getEntityType()
	{
		return EntityType.CLASS_EXPRESSION;
	}
}