/******************************************************************************
* A table with three columns <A,B,C> where C is fixed per <A,B>.              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class Map2Map<A,B,C>
{

//Attributes
	
	private HashMap<A,HashMap<B,C>> multimap;
	private int size;
	
//Constructors

	/**
	 * Constructs a new empty Map2Map
	 */
	public Map2Map()
	{
		multimap = new HashMap<A,HashMap<B,C>>();
		size = 0;
	}
	
	/**
	 * Constructs a new Map2Map that is a copy of
	 * the given Map2Map
	 * @param m: the Map2Map to copy
	 */
	public Map2Map(Map2Map<A,B,C> m)
	{
		multimap = new HashMap<A,HashMap<B,C>>();
		size = m.size;
		Set<A> keys = m.keySet();
		for(A a : keys)
			multimap.put(a, new HashMap<B,C>(m.get(a)));
	}

//Public Methods
	
	/**
	 * Adds the value for the given keys to the Map2Map
	 * @param keyA: the first level key to add to the Map2Map
	 * @param keyB: the second level key to add to the Map2Map
	 * @param valueC: the value for the pair of keys to add to the Map2Map
	 */
	public void add(A keyA, B keyB, C valueC)
	{
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(!contains(keyA,keyB))
			size++;
		if(mapsA == null)
		{
			mapsA = new HashMap<B,C>();
			mapsA.put(keyB, valueC);
			multimap.put(keyA, mapsA);
		}
		else
			mapsA.put(keyB, valueC);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map
	 * @return whether the Map2Map contains the first level keyA
	 */
	public boolean contains(A keyA)
	{
		return multimap.containsKey(keyA);
	}

	/**
	 * @param keyA: the first level key to search in the Map2Map
	 * @param keyB: the second level key to search in the Map2Map
	 * @return whether the Map2Map contains an entry with the two keys
	 */
	public boolean contains(A keyA, B keyB)
	{
		return multimap.containsKey(keyA) &&
			multimap.get(keyA).containsKey(keyB);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map
	 * @param keyB: the second level key to search in the Map2Map
	 * @param valueC: the value to search in the Map2Map
	 * @return whether the Map2Map contains an entry with the two keys
	 * and the given value
	 */
	public boolean contains(A keyA, B keyB, C valueC)
	{
		return multimap.containsKey(keyA) &&
			multimap.get(keyA).containsKey(keyB) &&
			multimap.get(keyA).get(keyB).equals(valueC);
	}

	/**
	 * @param keyA: the first level key to search in the Map2Map
	 * @return the number of entries with keyA
	 */
	public int entryCount(A keyA)
	{
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return 0;
		return mapsA.size();
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map
	 * @param valueC: the value to search in the Map2Map
	 * @return the number of entries with keyA that have valueC
	 */
	public int entryCount(A keyA, C valueC)
	{
		int count = 0;
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return count;
		Set<B> setA = mapsA.keySet();
		for(B b : setA)
			if(mapsA.get(b).equals(valueC))
				count++;
		return count;
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map
	 * @return the HashMap with all entries for keyA
	 */
	public HashMap<B,C> get(A keyA)
	{
		return multimap.get(keyA);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map
	 * @param keyB: the second level key to search in the Map2Map
	 * @return the value for the entry with the two keys or null
	 * if no such entry exists
	 */	
	public C get(A keyA, B keyB)
	{
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null || !mapsA.containsKey(keyB))
			return null;
		return mapsA.get(keyB);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map
	 * @param valueC: the value to search in the Map2Map
	 * @return the list of second level keys in entries with keyA and valueC
	 */	
	public Vector<B> getMatchingKeys(A keyA, C valueC)
	{
		Vector<B> keysB = new Vector<B>(0,1);
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return keysB;
		Set<B> setA = mapsA.keySet();
		for(B b : setA)
			if(mapsA.get(b).equals(valueC))
				keysB.add(b);
		return keysB;
	}
	
	/**
	 * @return the set of first level keys in the Map2Map
	 */
	public Set<A> keySet()
	{
		return multimap.keySet();
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map
	 * @return the set of second level keys in all entries with keyA
	 */
	public Set<B> keySet(A keyA)
	{
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return null;
		return mapsA.keySet();
	}
	
	/**
	 * @return the number of first level keys in the Map2Map
	 */
	public int keyCount()
	{
		return multimap.size();
	}
	
	
	/**
	 * Removes all entries for the given first level key
	 * @param keyA: the key to remove from the Map2Map
	 */
	public void remove(A keyA)
	{
		if(multimap.get(keyA) != null)
			size -= multimap.get(keyA).size();
		multimap.remove(keyA);
	}
	
	/**
	 * Removes the entry for the given key pair
	 * @param keyA: the first level key to search in the Map2Map
	 * @param keyB: the second level key to remove from the Map2Map
	 */
	public void remove(A keyA, B keyB)
	{
		HashMap<B,C> maps = multimap.get(keyA);
		if(maps != null)
		{
			maps.remove(keyB);
			if(maps.isEmpty())
				multimap.remove(keyA);
			size--;
		}
	}
	
	/**
	 * @return the total number of entries in the Map2Map
	 */
	public int size()
	{
		return size;
	}
}
