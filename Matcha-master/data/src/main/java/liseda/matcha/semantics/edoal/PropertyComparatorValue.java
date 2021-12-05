/******************************************************************************
* A PropertyComparatorValue (AttributeValueExpression in EDOAL) is an         *
* extension of the OWL DataHasValue and ObjectHasValue contemplating a        *
* a comparator and a ValueExpression which includes Data/Object Properties in *
* addition to Literals/Individuals. It represents the set of individuals who  *
* have as value for the restricted property a value that compares favorably   *
* to the restricted ValueExpression.                                          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.PropertyExpression;
import liseda.matcha.semantics.owl.ValueExpression;

public class PropertyComparatorValue extends ClassExpression
{

//Attributes
	
	private PropertyExpression onProperty;
	private Comparator comp;
	private ValueExpression val;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new AttributeValueRestriction on the given attribute with the given comparator and value
	 * @param onAttribute: the restricted attribute
	 * @param comp: the comparator
	 * @param val: the value expression
	 */
	public PropertyComparatorValue(PropertyExpression onAttribute, Comparator comp, ValueExpression val)
	{
		super();
		this.onProperty = onAttribute;
		this.comp = comp;
		this.val = val;
		elements.addAll(onAttribute.getElements());
		elements.addAll(val.getElements());
		stringForm = onProperty.toString() + " " + comp.toString() + " " + val.toString();
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof PropertyComparatorValue &&
				((PropertyComparatorValue)o).comp.equals(this.comp) &&
				((PropertyComparatorValue)o).val.equals(this.val) &&
				((PropertyComparatorValue)o).onProperty.equals(this.onProperty);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		Vector<Expression> components = new Vector<Expression>();
		components.add(onProperty);
		components.add(comp);
		components.add(val);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}