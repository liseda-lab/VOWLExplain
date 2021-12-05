/******************************************************************************
* The symmetric closure of an Object Property, denoting the disjunction       *
* between the Object Property and its inverse.                                *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.owl.ObjectPropertyExpression;

public class SymmetricProperty extends ObjectPropertyExpression
{

//Attributes
	
	private ObjectPropertyExpression sym;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new SymmetricRelation from the given relation expression
	 * @param sym: the relation expression to symmetrify
	 */
	public SymmetricProperty(ObjectPropertyExpression sym)
	{
		super();
		this.sym = sym;
		elements.addAll(sym.getElements());
		stringForm = "SYMMETRIC[" + sym.toString() + "]";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof SymmetricProperty &&
				((SymmetricProperty)o).sym.equals(this.sym);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ObjectPropertyExpression> getComponents()
	{
		Vector<ObjectPropertyExpression> components = new Vector<ObjectPropertyExpression>();
		components.add(sym);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}