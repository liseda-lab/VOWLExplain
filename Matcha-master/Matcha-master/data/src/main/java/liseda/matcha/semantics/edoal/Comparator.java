/******************************************************************************
* A Comparator such as "equals", "lower than" or "greater than".              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.Expression;

public class Comparator extends AbstractExpression
{

//Attributes
	
	//The Comparator is normally not part of the input ontologies
	//and thus its URI shouldn't be included in the entities set
	private String uri;
	
//Constructor

	/**
	 * Constructs a new Comparator from the given uri
	 * @param uri: the URI of the class
	 */
	public Comparator(String uri)
	{
		super();
		this.uri = uri;
	}

//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Comparator && ((Comparator)o).uri.equals(this.uri);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		return uri;
	}
}