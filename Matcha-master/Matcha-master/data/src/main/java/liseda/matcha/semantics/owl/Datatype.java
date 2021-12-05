/******************************************************************************
* A Datatype.                                                                 *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Expression;

public class Datatype extends AbstractExpression
{

//Attributes
	
	protected String uri;
	
//Constructor

	/**
	 * Constructs a new Datatype with the given uri
	 * @param uri: the uri of the Datatype
	 */
	public Datatype(String uri)
	{
		super();
		this.uri = uri;
	}

//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Datatype && ((Datatype)o).uri.equals(this.uri);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return null;
	}
	
	@Override
	public EntityType getEntityType()
	{
		return EntityType.DATATYPE;
	}
	
	@Override
	public String toString()
	{
		return uri;
	}
}