/******************************************************************************
* A tuple with 3 elements.                                                    *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

public class Triple<A,B,C>
{

//Attributes
	
	private A element1;
	private B element2;
	private C element3;
	
//Constructors

	/**
	 * Constructs a new Triple with the given elements
	 * @param elA: the first element
	 * @param elB: the second element
	 * @param elC: the third element
	 */
	public Triple(A elA, B elB, C elC)
	{
		element1 = elA;
		element2 = elB;
		element3 = elC;
	}
	
//Public Methods
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o)
	{
		return o instanceof Triple && element1.equals(((Triple)o).element1) &&
				element2.equals(((Triple)o).element2) && element3.equals(((Triple)o).element3);
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
	
	/**
	 * @return the third element in the Triple
	 */
	public C get3()
	{
		return element3;
	}
}