/******************************************************************************
* Application of an operator to a list of arguments (which can be any type of *
* Expression).                                                                *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.owl.ValueExpression;

public class Apply extends AbstractExpression implements ValueExpression
{

//Attributes
	
	private String operator;
	private Vector<ValueExpression> arguments;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new Apply with the given operator and arguments
	 * @param operator: the uri of the operator to apply
	 * @param arguments: the expressions that are arguments of the operation
	 */
	public Apply(String operator, Vector<ValueExpression> arguments)
	{
		super();
		this.operator = operator;
		this.arguments = arguments;
		stringForm = operator + " [";
		for(ValueExpression e : arguments)
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
		return o instanceof Apply &&
				((Apply)o).operator.equals(this.operator) &&
				((Apply)o).arguments.equals(this.arguments);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ValueExpression> getComponents()
	{
		return arguments;
	}
	
	/**
	 * @return the operator of this apply
	 */
	public String getOperator()
	{
		return operator;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}