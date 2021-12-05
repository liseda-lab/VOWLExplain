/******************************************************************************
* A negation of a Data Property.                                              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.owl.Construction;
import liseda.matcha.semantics.owl.DataPropertyExpression;

public class DataPropertyNegation extends DataPropertyExpression implements Construction
{

//Attributes
	
	private DataPropertyExpression neg;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new PropertyNegation from the given property expression
	 * @param neg: the property expression in the negation
	 */
	public DataPropertyNegation(DataPropertyExpression neg)
	{
		super();
		this.neg = neg;
		elements.addAll(neg.getElements());
		stringForm = "NOT[" + neg.toString() + "]";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof DataPropertyNegation &&
				((DataPropertyNegation)o).neg.equals(this.neg);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<DataPropertyExpression> getComponents()
	{
		Vector<DataPropertyExpression> components = new Vector<DataPropertyExpression>();
		components.add(neg);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}