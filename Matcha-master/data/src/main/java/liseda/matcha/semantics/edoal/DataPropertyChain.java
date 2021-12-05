/******************************************************************************
* A Data Property Chain (Property Composition in EDOAL) is an ordered path of *
* of Properties that starts with any number of Object Properties and ends     *
* with a Data Property. It is analogous to an OWL Object Property Chain, but  *
* applied to a Data Property. It represents the set of individuals that are   *
* the subject of the first Object Property and are transitively connected     *
* through the chain to the literals that are the object of the Data Property. *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.owl.Construction;
import liseda.matcha.semantics.owl.DataPropertyExpression;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;
import liseda.matcha.semantics.owl.PropertyExpression;

public class DataPropertyChain extends DataPropertyExpression implements Construction
{

//Attributes
	
	private Vector<Expression> path;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new DataPropertyChain from the given list of PropertyExpressions
	 * @param path: the list of PropertyExpressions in the chain, where the last must
	 * be a DataPropertyExpression and all others must be ObjectPropertyExpressions
	 */
	public DataPropertyChain(Vector<PropertyExpression> path)
	{
		super();
		this.path = new Vector<Expression>();
		stringForm = "PATH[";
		for(int i = 0; i < path.size(); i++)
		{
			PropertyExpression e = path.get(i);
			if(i < path.size()-1 && !(e instanceof ObjectPropertyExpression))
				throw new IllegalArgumentException("ERROR: All but the last element in a DataPropertyChain must be ObjectPropertyExpressions");
			if(i == path.size()-1 && !(e instanceof DataPropertyExpression))
				throw new IllegalArgumentException("ERROR: The last element in a DataPropertyChain must be a DataPropertyExpression");
			path.add(e);
			elements.addAll(e.getElements());
			stringForm += e.toString() + ", ";
		}
		stringForm = stringForm.substring(0, stringForm.lastIndexOf(',')) + "]";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof DataPropertyChain &&
				((DataPropertyChain)o).path.equals(this.path);
	}
	
	@Override
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