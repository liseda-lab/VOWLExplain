/******************************************************************************
* The reflexive closure of an Object Property.                                *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.owl.ObjectPropertyExpression;

public class ReflexiveProperty extends ObjectPropertyExpression
{

//Attributes
	
	private ObjectPropertyExpression ref;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new ReflexiveRelation from the given relation expression
	 * @param ref: the relation expression to reflect
	 */
	public ReflexiveProperty(ObjectPropertyExpression ref)
	{
		super();
		this.ref = ref;
		elements.addAll(ref.getElements());
		stringForm = "REFLEXIVE[" + ref.toString() + "]";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ReflexiveProperty &&
				((ReflexiveProperty)o).ref.equals(this.ref);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	/**
	 * A ReflexiveRelation has as single subcomponent the reflexive relation expression
	 */
	public List<ObjectPropertyExpression> getComponents()
	{
		Vector<ObjectPropertyExpression> components = new Vector<ObjectPropertyExpression>();
		components.add(ref);
		return components;
	}

	@Override
	public String toString()
	{
		return stringForm;
	}
}