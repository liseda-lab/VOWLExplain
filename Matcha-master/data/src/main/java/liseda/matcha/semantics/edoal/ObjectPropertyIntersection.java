/******************************************************************************
* An intersection of Object Properies.                                        *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.owl.Construction;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;

public class ObjectPropertyIntersection extends ObjectPropertyExpression implements Construction
{

//Attributes
	
	private Vector<Expression> intersect;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new RelationIntersection from the given set of relation expressions
	 * @param intersect: the relation expressions in the intersection
	 */
	public ObjectPropertyIntersection(List<ObjectPropertyExpression> intersect)
	{
		super();
		this.intersect = new Vector<Expression>(intersect);
		stringForm = "AND[";
		for(ObjectPropertyExpression e : intersect)
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
		return o instanceof ObjectPropertyIntersection &&
				((ObjectPropertyIntersection)o).intersect.equals(this.intersect);
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