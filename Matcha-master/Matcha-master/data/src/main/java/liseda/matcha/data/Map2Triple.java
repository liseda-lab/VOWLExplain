/******************************************************************************
* A table with four columns <A,B,C,D> where <B,C,D> are unique per A.         *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

import java.util.HashMap;
import java.util.Set;

public class Map2Triple<A,B,C,D>
{

//Attributes
	
	private HashMap<A,Triple<B,C,D>> multimap;
	
//Constructors

	/**
	 * Constructs a new empty Map2Triple
	 */
	public Map2Triple()
	{
		multimap = new HashMap<A,Triple<B,C,D>>();
	}
	
	/**
	 * Constructs a new Map2Triple that is a copy of
	 * the given Map2Triple
	 * @param m: the Map2Triple to copy
	 */
	public Map2Triple(Map2Triple<A,B,C,D> m)
	{
		multimap = new HashMap<A,Triple<B,C,D>>(m.multimap);
	}

//Public Methods
	
	/**
	 * Adds the value for the given keys to the Map2Triple
	 * @param keyA: the key to add to the Map2Triple
	 * @param elB: the first element in the Triple
	 * @param elC: the second element in the Triple
	 * @param elD: the third  element in the Triple
	 */
	public void add(A keyA, B elB, C elC, D elD)
	{
		Triple<B,C,D> t = new Triple<B,C,D>(elB,elC,elD);
		multimap.put(keyA,t);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Triple
	 * @return whether the Map2Triple contains the first level keyA
	 */
	public boolean contains(A keyA)
	{
		return multimap.containsKey(keyA);
	}

	/**
	 * @param keyA: the first level key to search in the Map2Triple
	 * @return the Triple corresponding to keyA
	 */
	public Triple<B,C,D> get(A keyA)
	{
		return multimap.get(keyA);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Triple
	 * @return the element1 in the Triple corresponding to keyA
	 */
	public B get1(A keyA)
	{
		return multimap.get(keyA).get1();
	}

	/**
	 * @param keyA: the first level key to search in the Map2Triple
	 * @return the element2 in the Triple corresponding to keyA
	 */
	public C get2(A keyA)
	{
		return multimap.get(keyA).get2();
	}

	/**
	 * @param keyA: the first level key to search in the Map2Triple
	 * @return the element3 in the Triple corresponding to keyA
	 */
	public D get3(A keyA)
	{
		return multimap.get(keyA).get3();
	}

	/**
	 * @return the set of first level keys in the Map2Triple
	 */
	public Set<A> keySet()
	{
		return multimap.keySet();
	}
	
	/**
	 * @return the total number of entries in the Map2Triple
	 */
	public int size()
	{
		return multimap.size();
	}
}
