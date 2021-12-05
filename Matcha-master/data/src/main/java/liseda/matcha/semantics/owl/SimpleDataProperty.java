/******************************************************************************
* A simple data property.                                                     *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;

import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Expression;

public class SimpleDataProperty extends DataPropertyExpression
{
	
//Constructor
	
	/**
	 * Constructs a new DataPropertyId from the given uri
	 * @param uri: the URI of the data property
	 * @param lang: the language of the data property
	 */
	public SimpleDataProperty(String uri)
	{
		super();
		elements.add(uri);
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof SimpleDataProperty))
			return false;
		SimpleDataProperty p = ((SimpleDataProperty)o);
		return p.elements.equals(this.elements);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return null;
	}

	@Override
	public EntityType getEntityType()
	{
		return EntityType.DATA_PROP;
	}
	
	@Override
	public String toString()
	{
		return elements.iterator().next();
	}
}