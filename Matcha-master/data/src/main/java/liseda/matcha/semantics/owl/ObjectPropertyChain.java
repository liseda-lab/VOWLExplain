/******************************************************************************
* An Object Property Chain is an ordered path of Object Properties            *
* representing the set of individuals that are the subject of the first       *
* Object Property and transitively connected through the chain to individuals *
* that are the object of the final Object Property.                           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class ObjectPropertyChain extends ObjectPropertyExpression implements Construction
{

//Attributes
	
	private Vector<Expression> path;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new RelationComposition from the given list of relation expressions
	 * @param path: the list of relation expressions in this composition
	 */
	public ObjectPropertyChain(List<ObjectPropertyExpression> path)
	{
		super();
		this.path = new Vector<Expression>(path);
		stringForm = "PATH[";
		for(ObjectPropertyExpression e : path)
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
		return o instanceof ObjectPropertyChain &&
				((ObjectPropertyChain)o).path.equals(this.path);
	}
	
	@Override
	/**
	 * The components of a RelationComposition are the list of relation
	 * expressions in the composition
	 */
	public List<Expression> getComponents()
	{
		return path;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}