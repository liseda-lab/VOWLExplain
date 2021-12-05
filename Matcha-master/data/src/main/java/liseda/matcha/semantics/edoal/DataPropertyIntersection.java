/******************************************************************************
* An intersection of Data Properies.                                          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.owl.Construction;
import liseda.matcha.semantics.owl.DataPropertyExpression;

public class DataPropertyIntersection extends DataPropertyExpression implements Construction
{

//Attributes
	
	//We preserve the order of elements in an intersection, even though it is not really relevant
	private Vector<Expression> intersect;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new PropertyIntersection from the given set of property expressions
	 * @param intersect: the property expressions in the intersection
	 */
	public DataPropertyIntersection(List<DataPropertyExpression> intersect)
	{
		super();
		this.intersect = new Vector<Expression>(intersect);
		String stringForm = "AND[";
		for(DataPropertyExpression e : intersect)
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
		return o instanceof DataPropertyIntersection &&
				((DataPropertyIntersection)o).intersect.equals(this.intersect);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return intersect;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}