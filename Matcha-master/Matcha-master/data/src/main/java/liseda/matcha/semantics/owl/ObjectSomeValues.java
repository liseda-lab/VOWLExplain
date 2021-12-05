/******************************************************************************
* A class expression representing the set of individuals that have at least   *
* an instance of the specified class for the restricted object property.      *
* Note that some values restrictions are semantically equivalent to min 1     *
* cardinality restrictions on the same property and type.                     *     
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class ObjectSomeValues extends ClassExpression
{

//Attributes
	
	private ObjectPropertyExpression onProperty;
	private ClassExpression type;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new SomeValuesRestriction on the given property with the given type
	 * @param onProperty: the restricted property
	 * @param type: the type to restrict the range of the property
	 */
	public ObjectSomeValues(ObjectPropertyExpression onAttribute, ClassExpression type)
	{
		super();
		this.onProperty = onAttribute;
		this.type = type;
		elements.addAll(onAttribute.getElements());
		stringForm = onProperty.toString() + " some " + type.toString();
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ObjectSomeValues &&
				((ObjectSomeValues)o).type.equals(this.type) &&
				((ObjectSomeValues)o).onProperty.equals(this.onProperty);
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