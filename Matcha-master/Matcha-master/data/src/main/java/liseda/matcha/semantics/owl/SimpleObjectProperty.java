/******************************************************************************
* A simple object property.                                                   *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;

import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Expression;

public class SimpleObjectProperty extends ObjectPropertyExpression
{

//Constructor
	
	/**
	 * Constructs a new SimpleObjectProperty from the given uri
	 * @param uri: the URI of the data property
	 */
	public SimpleObjectProperty(String uri)
	{
		super();
		elements.add(uri);
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof SimpleObjectProperty &&
				((SimpleObjectProperty)o).elements.equals(this.elements);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return null;
	}

	@Override
	public EntityType getEntityType()
	{
		return EntityType.OBJECT_PROP;
	}
	
	@Override
	public String toString()
	{
		return elements.iterator().next();
	}
}