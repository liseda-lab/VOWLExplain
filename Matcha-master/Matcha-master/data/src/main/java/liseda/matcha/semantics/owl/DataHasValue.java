/******************************************************************************
* A class expression that represents the set of individuals who have the      *
* specified value for the restricted data property.                           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class DataHasValue extends ClassExpression
{

//Attributes
	
	private DataPropertyExpression onProperty;
	private Literal value;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new HasValueRestriction on the given property with the given value
	 * @param onProperty: the restricted property
	 * @param value: the value of the property
	 */
	public DataHasValue(DataPropertyExpression onProperty, Literal value)
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
		return o instanceof DataHasValue &&
				((DataHasValue)o).value.equals(this.value) &&
				((DataHasValue)o).onProperty.equals(this.onProperty);
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