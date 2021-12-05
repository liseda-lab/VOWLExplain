/******************************************************************************
* An Object Property Range (RelationCoDomainRestriction in EDOAL) represents  *
* the set of Object Property whose range is the given class expression.       *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;

public class ObjectPropertyRange extends ObjectPropertyExpression
{

//Propertys
	
	private ClassExpression rest;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new ObjectPropertyRange with the class expression as CoDomain
	 * @param rest: the class expression defining the domain
	 */
	public ObjectPropertyRange(ClassExpression rest)
	{
		super();
		this.rest = rest;
		elements.addAll(rest.getElements());
		stringForm = "Range(" + rest.toString() + ")";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ObjectPropertyRange &&
				((ObjectPropertyRange)o).rest.equals(this.rest);
	}
	
	public ClassExpression getClassRestriction() 
	{
		return rest;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ClassExpression> getComponents()
	{
		Vector<ClassExpression> components = new Vector<ClassExpression>();
		components.add(rest);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}