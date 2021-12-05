/******************************************************************************
* A Data Property Range Type (PropertyTypeRestriction in EDOAL) represents    *
* the set of properties whose range is of the given Datatype.                 *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.owl.DataPropertyExpression;
import liseda.matcha.semantics.owl.Datatype;

public class DataPropertyRangeType extends DataPropertyExpression
{

//Attributes
	
	private Datatype type;
	private String stringForm;
	
//Constructor
	
	/**
	 * Constructs a new PropertyTypeRestriction with the range
	 * @param type: the datatype to restrict the range of the property
	 */
	public DataPropertyRangeType(Datatype type)
	{
		super();
		this.type = type;
		stringForm = "Range(" + type.toString() + ")";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof DataPropertyRangeType &&
				((DataPropertyRangeType)o).type.equals(this.type);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Datatype> getComponents()
	{
		Vector<Datatype> components = new Vector<Datatype>();
		components.add(type);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}