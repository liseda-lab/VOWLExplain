/******************************************************************************
* A cardinality specification, which must be a non-negative integer.          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.Expression;

public class Cardinality extends AbstractExpression
{

//Attributes
	
	private int cardinality;
	
//Constructor

	/**
	 * Constructs a new non-negative int Literal
	 * @param cardinality: the cardinality specification, which must be a non-negative int
	 */
	public Cardinality(int cardinality)
	{
		super();
		if(cardinality < 0)
			throw new IllegalArgumentException("Negative integer received but non-negative integer expected");
		this.cardinality = cardinality;
		elements.add("" + cardinality);
	}

//Public Methods
	
	public void decrement()
	{
		cardinality--;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof Cardinality &&
			((Cardinality)o).cardinality == this.cardinality;
	}

	public int getCardinality()
	{
		return cardinality;
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return null;
	}

	public void increment()
	{
		cardinality++;
	}
	
	@Override
	public String toString()
	{
		return "" + cardinality;
	}
}