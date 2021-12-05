/******************************************************************************
* A class expression that represents the set of individuals who have at most  *
* the specified cardinality for a given data property, optionally with the    *
* specified datatype.                                                         *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class DataMaxCardinality extends ClassExpression implements CardinalityRestriction
{

//Attributes
	
	private DataPropertyExpression onProperty;
	private Cardinality cardinality;
	private Datatype type;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new MaxCardinalityRestriction on the given property with the given cardinality
	 * @param onProperty: the restricted attribute
	 * @param cardinality: the restricted cardinality
	 */
	public DataMaxCardinality(DataPropertyExpression onProperty, Cardinality cardinality)
	{
		super();
		this.onProperty = onProperty;
		this.cardinality = cardinality;
		elements.addAll(onProperty.getElements());
		stringForm = onProperty.toString() + " max " + cardinality.toString();
	}
	
	/**
	 * Constructs a new MaxCardinalityRestriction on the given property with the given cardinality
	 * @param onProperty: the restricted attribute
	 * @param cardinality: the restricted cardinality
	 */
	public DataMaxCardinality(DataPropertyExpression onProperty, Cardinality cardinality, Datatype type)
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
		return o instanceof DataMaxCardinality &&
				((DataMaxCardinality)o).cardinality == this.cardinality &&
				((DataMaxCardinality)o).onProperty.equals(this.onProperty) &&
				(
					(((DataMaxCardinality)o).type == null && this.type == null) ||
					((DataMaxCardinality)o).type.equals(this.type)
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