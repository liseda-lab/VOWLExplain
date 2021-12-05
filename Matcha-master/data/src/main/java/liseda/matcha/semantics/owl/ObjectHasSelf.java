/******************************************************************************
* A class expression that represents the set of individuals who are connected *
* to themselves through the restricted object property.                       *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class ObjectHasSelf extends ClassExpression
{

//Attributes
	
	private ObjectPropertyExpression onProperty;
	
//Constructor
	
	/**
	 * Constructs a new HasValueRestriction on the given property with the given value
	 * @param onProperty: the restricted property
	 */
	public ObjectHasSelf(ObjectPropertyExpression onProperty)
	{
		super();
		this.onProperty = onProperty;
		elements.addAll(onProperty.getElements());
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ObjectHasSelf &&
				((ObjectHasSelf)o).onProperty.equals(this.onProperty);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		Vector<Expression> components = new Vector<Expression>();
		components.add(onProperty);
		return components;
	}
	
	@Override
	public String toString()
	{
		return onProperty.toString();
	}
}