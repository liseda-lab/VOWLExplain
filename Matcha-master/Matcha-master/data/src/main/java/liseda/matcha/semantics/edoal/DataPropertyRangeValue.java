/******************************************************************************
* A DataPropertyRangeValue (Property Value Restriction in EDOAL) represents   *
* the set of properties whose range falls under the given value restriction.  *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.owl.DataPropertyExpression;
import liseda.matcha.semantics.owl.ValueExpression;

public class DataPropertyRangeValue extends DataPropertyExpression
{

//Attributes
	
	private Comparator comp;
	private ValueExpression val;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new PropertyValueRestriction on the given attribute with the given comparator and value
	 * @param comp: the comparator
	 * @param val: the value (must be a non-negative integer)
	 */
	public DataPropertyRangeValue(Comparator comp, ValueExpression val)
	{
		super();
		this.comp = comp;
		this.val = val;
		//The ValueExpression may be a literal or a PropertyExpression, so we must add its elements as well
		elements.addAll(val.getElements());
		stringForm = "Range(" + comp.toString() + " " + val.toString() + ")";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof DataPropertyRangeValue &&
				((DataPropertyRangeValue)o).comp.equals(this.comp) &&
				((DataPropertyRangeValue)o).val.equals(this.val);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		Vector<Expression> components = new Vector<Expression>();
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