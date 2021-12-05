/******************************************************************************
* A class expression representing the set of individuals that have no         *
* instances for the restricted object property that are not of the specified  *
* class expression.                                                           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class ObjectAllValues extends ClassExpression
{

//Attributes
	
	private ObjectPropertyExpression onProperty;
	private ClassExpression type;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new AllValuesRestriction on the given property with the given type
	 * @param onProperty: the restricted property
	 * @param type: the type to restrict the range of the property
	 */
	public ObjectAllValues(ObjectPropertyExpression onAttribute, ClassExpression type)
	{
		super();
		this.onProperty = onAttribute;
		this.type = type;
		elements.addAll(onAttribute.getElements());
		stringForm = onProperty.toString() + " only " + type.toString();
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ObjectAllValues &&
				((ObjectAllValues)o).type.equals(this.type) &&
				((ObjectAllValues)o).onProperty.equals(this.onProperty);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		Vector<Expression> components = new Vector<Expression>();
		components.add(onProperty);
		components.add(type);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}