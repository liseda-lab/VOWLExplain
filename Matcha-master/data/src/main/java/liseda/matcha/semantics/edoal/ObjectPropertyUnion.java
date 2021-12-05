/******************************************************************************
* A union of Object Properies.                                                *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.owl.Construction;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;

public class ObjectPropertyUnion extends ObjectPropertyExpression implements Construction
{

//Attributes
	
	private Vector<Expression> union;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new RelationUnion from the given set of relation expressions
	 * @param union: the relation expressions in the union
	 */
	public ObjectPropertyUnion(List<ObjectPropertyExpression> union)
	{
		super();
		this.union = new Vector<Expression>(union);
		stringForm = "OR[";
		for(ObjectPropertyExpression e : union)
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
		return o instanceof ObjectPropertyUnion &&
				((ObjectPropertyUnion)o).union.equals(this.union);
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