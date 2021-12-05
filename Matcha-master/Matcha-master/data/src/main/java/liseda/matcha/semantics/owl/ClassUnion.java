/******************************************************************************
* A union class expressions.                                                  *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class ClassUnion extends ClassExpression implements Construction
{

//Attributes
	
	//We preserve the order of elements in a union, even though it is not really relevant
	private Vector<Expression> union;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new ClassUnion from the given set of class expressions
	 * @param uri: the class expressions in the union
	 */
	public ClassUnion(List<ClassExpression> union)
	{
		super();
		this.union = new Vector<Expression>(union);
		stringForm = "OR[";
		for(ClassExpression e : union)
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
		return o instanceof ClassUnion &&
				((ClassUnion)o).union.equals(this.union);
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