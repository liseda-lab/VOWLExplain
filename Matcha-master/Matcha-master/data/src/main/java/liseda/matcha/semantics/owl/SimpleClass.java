/******************************************************************************
* A simple class.                                                             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;

import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Expression;

public class SimpleClass extends ClassExpression
{

//Constructor
	
	/**
	 * Constructs a new SimpleClass from the given uri
	 * @param uri: the URI of the class
	 */
	public SimpleClass(String uri)
	{
		super();
		elements.add(uri);
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof SimpleClass &&
				((SimpleClass)o).elements.equals(this.elements);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return null;
	}
	
	@Override
	public EntityType getEntityType()
	{
		return EntityType.CLASS;
	}
	
	@Override
	public String toString()
	{
		return elements.iterator().next();
	}
}