/******************************************************************************
* Abstract implementation of an expression in a complex mapping.              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractExpression implements Expression
{
	
//Attributes
	
	//To enable efficient hashing, an EDOAL Expression must list all elements that compose it
	protected HashSet<String> elements;
	
//Constructors
	
	protected AbstractExpression()
	{
		elements = new HashSet<String>();
	}
	
//Public Methods
	
	@Override
	public abstract boolean equals(Object o);
	
	@Override
	public Set<String> getElements()
	{
		return elements;
	}
	
	@Override
	public abstract <E extends Expression> List<E> getComponents();
	
	@Override
	public EntityType getEntityType()
	{
		return null;
	}
	
	@Override
	public int hashCode()
	{
		return this.toString().hashCode();
	}

	@Override
	public abstract String toString();
}