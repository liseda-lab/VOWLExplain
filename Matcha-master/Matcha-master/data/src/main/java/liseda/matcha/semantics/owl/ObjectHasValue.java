/******************************************************************************
* A class expression that represents the set of individuals who have the      *
* specified individual as object of the restricted object property.           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class ObjectHasValue extends ClassExpression
{

//Attributes
	
	private ObjectPropertyExpression onProperty;
	private Individual value;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new HasValueRestriction on the given property with the given value
	 * @param onProperty: the restricted property
	 * @param value: the value of the property
	 */
	public ObjectHasValue(ObjectPropertyExpression onProperty, Individual value)
	{
		super();
		this.onProperty = onProperty;
		this.value = value;
		elements.addAll(onProperty.getElements());
		elements.addAll(value.getElements());
		stringForm = onProperty.toString() + " " + value.toString();
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ObjectHasValue &&
				((ObjectHasValue)o).value.equals(this.value) &&
				((ObjectHasValue)o).onProperty.equals(this.onProperty);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		Vector<Expression> components = new Vector<Expression>();
		components.add(onProperty);
		components.add(value);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}