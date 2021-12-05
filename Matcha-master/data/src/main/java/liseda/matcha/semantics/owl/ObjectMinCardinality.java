/******************************************************************************
* A class expression that represents the set of individuals who have at least *
* the specified cardinality for a given object property, optionally with the  *
* specified class.                                                            *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class ObjectMinCardinality extends ClassExpression implements CardinalityRestriction
{

//Attributes
	
	private ObjectPropertyExpression onProperty;
	private Cardinality cardinality;
	private ClassExpression type;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new MinCardinalityRestriction on the given property with the given cardinality
	 * @param onProperty: the restricted attribute
	 * @param cardinality: the restricted cardinality
	 */
	public ObjectMinCardinality(ObjectPropertyExpression onProperty, Cardinality cardinality)
	{
		super();
		this.onProperty = onProperty;
		this.cardinality = cardinality;
		elements.addAll(onProperty.getElements());
		stringForm = onProperty.toString() + " min " + cardinality.toString();
	}
	
	/**
	 * Constructs a new MinCardinalityRestriction on the given property with the given cardinality
	 * @param onProperty: the restricted attribute
	 * @param cardinality: the restricted cardinality
	 */
	public ObjectMinCardinality(ObjectPropertyExpression onProperty, Cardinality cardinality, ClassExpression type)
	{
		this(onProperty,cardinality);
		this.type = type;
		elements.addAll(type.getElements());
		if(type != null)
			stringForm += " " + type.toString();
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ObjectMinCardinality &&
				((ObjectMinCardinality)o).cardinality == this.cardinality &&
				((ObjectMinCardinality)o).onProperty.equals(this.onProperty) &&
				(
					(((ObjectMinCardinality)o).type == null && this.type == null) ||
					((ObjectMinCardinality)o).type.equals(this.type)
				);
	}
	
	@Override
	public int getCardinality()
	{
		return cardinality.getCardinality();
	}
	
	@Override
	public List<Expression> getComponents()
	{
		Vector<Expression> components = new Vector<Expression>();
		components.add(onProperty);
		components.add(cardinality);
		if(type != null)
			components.add(type);
		return components;
	}
	
	@Override
	public boolean isQualified()
	{
		return type != null;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}