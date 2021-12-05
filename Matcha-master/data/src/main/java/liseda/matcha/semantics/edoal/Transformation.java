/******************************************************************************
* A transformation expresses constraints on instances that should match.      *
* It lists the direction of the transformation, and two entities, one of      *
* which should be an operation (Aggregate or Apply).                          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.edoal;

import java.util.List;
import java.util.Vector;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.owl.ValueExpression;

public class Transformation extends AbstractExpression
{

//Attributes

	//The direction of the transformation
	private String direction;
	private ValueExpression entity1;
	private ValueExpression entity2;
	private String stringForm;
	
//Constructors
	
	/**
	 * Creates a transformation with the given direction between entity1 and entity2, one of
	 * which should be an operation
	 * @param dir: the direction of the transformation
	 * @param entity1: the EDOAL expression of the source ontology
	 * @param entity2: the EDOAL expression of the target ontology
	 */
	public Transformation(String direction, ValueExpression entity1, ValueExpression entity2)
	{
		this.direction = direction;
		this.entity1 = entity1;
		this.entity2 = entity2;
		elements.addAll(entity1.getElements());
		elements.addAll(entity2.getElements());
		stringForm = "TRANSF[" + entity1.toString() + " " + direction + " " + entity2.toString() + "]";
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Transformation &&
				((Transformation)o).direction.equals(this.direction) &&
				((Transformation)o).entity1.equals(this.entity1) &&
				((Transformation)o).entity2.equals(this.entity2);
	}
	
	/**
	 * @return the direction of this transformation
	 */
	public String getDirection()
	{
		return direction;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ValueExpression> getComponents()
	{
		Vector<ValueExpression> components = new Vector<ValueExpression>(2);
		components.add(entity1);
		components.add(entity2);
		return components;
	}
	
	@Override
	public String toString()
	{
		return stringForm;
	}
}