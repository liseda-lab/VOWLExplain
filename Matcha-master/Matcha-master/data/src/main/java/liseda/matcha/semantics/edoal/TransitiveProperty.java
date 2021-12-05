/******************************************************************************
* The transitive closure of an Object Property.                               *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.owl.ObjectPropertyExpression;

public class TransitiveProperty extends ObjectPropertyExpression
{

//Attributes
	
	private ObjectPropertyExpression trans;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new ReflexiveRelation from the given relation expression
	 * @param trans: the relation expression to reflect
	 */
	public TransitiveProperty(ObjectPropertyExpression trans)
	{
		super();
		this.trans = trans;
		elements.addAll(trans.getElements());
		stringForm = "TRANSITIVE[" + trans.toString() + "]";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof TransitiveProperty &&
				((TransitiveProperty)o).trans.equals(this.trans);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	/**
	 * A TransitiveRelation has as single subcomponent the transitive relation expression
	 */
	public List<ObjectPropertyExpression> getComponents()
	{
		Vector<ObjectPropertyExpression> components = new Vector<ObjectPropertyExpression>();
		components.add(trans);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}