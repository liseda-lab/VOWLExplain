/******************************************************************************
* A negation (or complement) of a class expression.                           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

public class ClassNegation extends ClassExpression implements Construction
{

//Attributes
	
	private ClassExpression neg;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new ClassNegation from the given class expression
	 * @param neg: the class expression in the negation
	 */
	public ClassNegation(ClassExpression neg)
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
		return o instanceof ClassNegation &&
				((ClassNegation)o).neg.equals(this.neg);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	/**
	 * A ClassNegation has as single subcomponent the negated class expression
	 */
	public List<ClassExpression> getComponents()
	{
		Vector<ClassExpression> components = new Vector<ClassExpression>();
		components.add(neg);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}