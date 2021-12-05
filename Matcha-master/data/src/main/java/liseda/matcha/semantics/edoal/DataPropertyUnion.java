/******************************************************************************
* A union of Data Properies.                                                  *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.owl.Construction;
import liseda.matcha.semantics.owl.DataPropertyExpression;

public class DataPropertyUnion extends DataPropertyExpression implements Construction
{

//Attributes
	
	//We preserve the order of elements in a union, even though it is not really relevant
	private Vector<Expression> union;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new PropertyUnion from the given set of property expressions
	 * @param union: the property expressions in the union
	 */
	public DataPropertyUnion(List<DataPropertyExpression> union)
	{
		super();
		this.union = new Vector<Expression>(union);
		String stringForm = "OR[";
		for(DataPropertyExpression e : union)
		{
			elements.addAll(e.getElements());
			stringForm += e.toString() + ", ";
		}
		stringForm = stringForm.substring(0, stringForm.lastIndexOf(',')) + "]";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof DataPropertyUnion &&
				((DataPropertyUnion)o).union.equals(this.union);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return union;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}