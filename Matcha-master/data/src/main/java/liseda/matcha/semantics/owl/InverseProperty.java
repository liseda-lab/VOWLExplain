/******************************************************************************
* The inverse of an Object Property.                                          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.Expression;

public class InverseProperty extends ObjectPropertyExpression
{

//Attributes
	
	private ObjectPropertyExpression inv;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new InverseRelation from the given relation expression
	 * @param inv: the relation expression to invert
	 */
	public InverseProperty(ObjectPropertyExpression inv)
	{
		super();
		this.inv = inv;
		elements.addAll(inv.getElements());
		stringForm = "INVERSE[" + inv.toString() + "]";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof InverseProperty &&
				((InverseProperty)o).inv.equals(this.inv);
	}
	
	@Override
	public List<Expression> getComponents()
	{
		Vector<Expression> components = new Vector<Expression>();
		components.add(inv);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}