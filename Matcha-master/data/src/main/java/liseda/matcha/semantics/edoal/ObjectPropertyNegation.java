/******************************************************************************
* A negation of an Object Property.                                           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.owl.Construction;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;

public class ObjectPropertyNegation extends ObjectPropertyExpression implements Construction
{

//Attributes
	
	private ObjectPropertyExpression neg;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new RelationNegation from the given relation expression
	 * @param neg: the relation expression in the negation
	 */
	public ObjectPropertyNegation(ObjectPropertyExpression neg)
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
		return o instanceof ObjectPropertyNegation &&
				((ObjectPropertyNegation)o).neg.equals(this.neg);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ObjectPropertyExpression> getComponents()
	{
		Vector<ObjectPropertyExpression> components = new Vector<ObjectPropertyExpression>();
		components.add(neg);
		return components;
	}

	@Override
	public String toString()
	{
		return stringForm;
	}
}