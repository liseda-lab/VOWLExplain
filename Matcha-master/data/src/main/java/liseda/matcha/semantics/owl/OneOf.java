/******************************************************************************
* An enumeration of individuals.                                              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class OneOf extends ClassExpression
{

//Attributes
	
	//We preserve the order of elements in an enumeration, even though it is not really relevant
	private Vector<Expression> enumeration;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new ClassUnion from the given set of class expressions
	 * @param uri: the class expressions in the union
	 */
	public OneOf(List<Individual> enumeration)
	{
		super();
		this.enumeration = new Vector<Expression>(enumeration);
		stringForm = "{";
		for(Individual i : enumeration)
		{
			elements.addAll(i.getElements());
			stringForm += i.toString() + ", ";
		}
		stringForm = stringForm.substring(0, stringForm.lastIndexOf(',')) + "}";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof OneOf &&
				((OneOf)o).enumeration.equals(this.enumeration);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return enumeration;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}