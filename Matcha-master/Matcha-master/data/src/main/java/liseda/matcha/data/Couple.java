/******************************************************************************
* A tuple with 2 elements.                                                    *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

public class Couple<A,B>
{

//Attributes
	
	private A element1;
	private B element2;
	
//Constructors

	/**
	 * Constructs a new Couple with the given elements
	 * @param elA: the first element
	 * @param elB: the second element
	 */
	public Couple(A elA, B elB)
	{
		element1 = elA;
		element2 = elB;
	}
	
//Public Methods
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o)
	{
		return o instanceof Couple && element1.equals(((Couple)o).element1) &&
				element2.equals(((Couple)o).element2);
	}
	
	/**
	 * @return the first element in the Triple
	 */
	public A get1()
	{
		return element1;
	}
	
	/**
	 * @return the second element in the Triple
	 */
	public B get2()
	{
		return element2;
	}
}