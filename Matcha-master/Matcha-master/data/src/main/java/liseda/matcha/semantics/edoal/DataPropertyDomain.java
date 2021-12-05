/******************************************************************************
* A Data Property Domain (PropertyDomainRestriction in EDOAL) represents the  *
* set of properties whose domain falls under the given restriction.           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.DataPropertyExpression;

public class DataPropertyDomain extends DataPropertyExpression
{

//Propertys
	
	private ClassExpression rest;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new PropertyDomainRestriction with the class expression as domain
	 * @param rest: the class expression defining the domain
	 */
	public DataPropertyDomain(ClassExpression rest)
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
		return o instanceof DataPropertyDomain &&
				((DataPropertyDomain)o).rest.equals(this.rest);
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