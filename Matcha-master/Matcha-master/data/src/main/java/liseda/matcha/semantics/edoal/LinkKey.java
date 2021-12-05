/******************************************************************************
* Linkkey is a generalisation of the concept of foreign key from relational   *
* databases. A Linkkey object defines the conditions under which two          *
* individuals should be considered equal, by declaring linked pairs of        *
* properties for which they must have equal values, as well as those for      *
* which there must be intersecting values.                                    *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.owl.PropertyExpression;

public class LinkKey extends AbstractExpression
{

//Attributes
	
	private String type;
	private HashMap<PropertyExpression,PropertyExpression> equals;
	private HashMap<PropertyExpression,PropertyExpression> intersects;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new LinkKey from the given maps of attribute expressions
	 * @param equals: the map of attribute expressions whose values must be equal
	 * @param intersects: the map of attribute expressions whose values must intersect
	 */
	public LinkKey(String type, HashMap<PropertyExpression,PropertyExpression> equals, HashMap<PropertyExpression,PropertyExpression> intersects)
	{
		super();
		this.type = type;
		this.equals = equals;
		this.intersects = intersects;
		stringForm = "LinkKey";
		if(type != null)
			stringForm += "(" + type + ")";
		if(!equals.isEmpty())
		{
			stringForm += " EQUALS:";
			for(PropertyExpression e : equals.keySet())
			{
				elements.addAll(e.getElements());
				elements.addAll(equals.get(e).getElements());
				stringForm += "<" + e.toString() + "," + equals.get(e).toString() + ">";
			}
		}
		if(!intersects.isEmpty())
		{
			stringForm += " INTERSECTS:";
			for(PropertyExpression e : intersects.keySet())
			{
				elements.addAll(e.getElements());
				elements.addAll(intersects.get(e).getElements());
				stringForm += "<" + e.toString() + "," + intersects.get(e).toString() + ">";
			}
		}
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof LinkKey &&
				((LinkKey)o).equals.equals(this.equals) &&
				((LinkKey)o).intersects.equals(this.intersects);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<PropertyExpression> getComponents()
	{
		Vector<PropertyExpression> components = new Vector<PropertyExpression>();
		for(PropertyExpression e : equals.keySet())
		{
			components.add(e);
			components.add(equals.get(e));
		}
		for(PropertyExpression e : intersects.keySet())
		{
			components.add(e);
			components.add(intersects.get(e));
		}
		return components;
	}
	
	/**
	 * @return the type of this LinkKey
	 */
	public String getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return stringForm;
	}
}