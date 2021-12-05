/******************************************************************************
* A table with three columns <A,B,C> where <B,C> are unique per A.            *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

import java.util.HashMap;
import java.util.Set;

public class Map2Couple<A,B,C>
{

//Attributes
	
	private HashMap<A,Couple<B,C>> multimap;
	
//Constructors

	/**
	 * Constructs a new empty Map2Couple
	 */
	public Map2Couple()
	{
		multimap = new HashMap<A,Couple<B,C>>();
	}
	
	/**
	 * Constructs a new Map2Couple that is a copy of
	 * the given Map2Couple
	 * @param m: the Map2Couple to copy
	 */
	public Map2Couple(Map2Couple<A,B,C> m)
	{
		multimap = new HashMap<A,Couple<B,C>>(m.multimap);
	}

//Public Methods
	
	/**
	 * Adds the value for the given keys to the Map2Couple
	 * @param keyA: the key to add to the Map2Couple
	 * @param elB: the first element in the Couple
	 * @param elC: the second element in the Couple
	 */
	public void add(A keyA, B elB, C elC)
	{
		Couple<B,C> t = new Couple<B,C>(elB,elC);
		multimap.put(keyA,t);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Couple
	 * @return whether the Map2Couple contains the first level keyA
	 */
	public boolean contains(A keyA)
	{
		return multimap.containsKey(keyA);
	}

	/**
	 * @param keyA: the first level key to search in the Map2Couple
	 * @return the Couple corresponding to keyA
	 */
	public Couple<B,C> get(A keyA)
	{
		return multimap.get(keyA);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Couple
	 * @return the element1 in the Couple corresponding to keyA
	 */
	public B get1(A keyA)
	{
		return multimap.get(keyA).get1();
	}

	/**
	 * @param keyA: the first level key to search in the Map2Couple
	 * @return the element2 in the Couple corresponding to keyA
	 */
	public C get2(A keyA)
	{
		return multimap.get(keyA).get2();
	}

	/**
	 * @return the set of first level keys in the Map2Couple
	 */
	public Set<A> keySet()
	{
		return multimap.keySet();
	}
	
	/**
	 * @return the total number of entries in the Map2Couple
	 */
	public int size()
	{
		return multimap.size();
	}
}
