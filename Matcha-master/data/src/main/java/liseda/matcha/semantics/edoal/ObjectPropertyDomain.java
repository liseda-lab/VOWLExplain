/******************************************************************************
* An Object Property Domain (RelationDomainRestriction in EDOAL) represents   *
* the set of Object Properties whose domain is the given class expression.    *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;

public class ObjectPropertyDomain extends ObjectPropertyExpression
{

//Attributes
	
	private ClassExpression rest;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new ObjectPropertyDomain with the class expression as domain
	 * @param rest: the class expression defining the domain
	 */
	public ObjectPropertyDomain(ClassExpression rest)
	{
		super();
		this.rest = rest;
		elements.addAll(rest.getElements());
		stringForm = "Domain(" + rest.toString() + ")";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ObjectPropertyDomain &&
				((ObjectPropertyDomain)o).rest.equals(this.rest);
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