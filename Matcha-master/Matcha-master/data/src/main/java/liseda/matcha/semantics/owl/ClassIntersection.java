/******************************************************************************
* An intersection of class expressions.                                       *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class ClassIntersection extends ClassExpression implements Construction
{
	
//Attributes
	
	//We preserve the order of elements in an intersection, even though it is not really relevant
	private Vector<Expression> intersect;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new ClassIntersection from the given set of class expressions
	 * @param intersect: the class expressions in the intersection
	 */
	public ClassIntersection(List<ClassExpression> intersect)
	{
		super();
		this.intersect = new Vector<Expression>(intersect);
		stringForm = "AND[";
		for(ClassExpression e : intersect)
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
		return o instanceof ClassIntersection &&
				((ClassIntersection)o).intersect.equals(this.intersect);
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