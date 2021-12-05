/******************************************************************************
* A table with three columns <A,B,C> with hash indexing on all three.         *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Map2Map2Set<A,B,C>
{

//Attributes
	
	private HashMap<A,Map2Set<B,C>> multimap;
	private int size;
	
//Constructors

	/**
	 * Constructs a new empty Map2Map2Set
	 */
	public Map2Map2Set()
	{
		multimap = new HashMap<A,Map2Set<B,C>>();
		size = 0;
	}
	
	/**
	 * Constructs a new Map2Map2Set that is a copy of
	 * the given Map2Map2Set
	 * @param m: the Map2Map2Set to copy
	 */
	public Map2Map2Set(Map2Map2Set<A,B,C> m)
	{
		multimap = new HashMap<A,Map2Set<B,C>>();
		size = m.size;
		Set<A> keys = m.keySet();
		for(A a : keys)
			multimap.put(a, new Map2Set<B,C>(m.get(a)));
	}

//Public Methods
	
	/**
	 * Adds the value for the given keys to the Map2Map2Set
	 * @param keyA: the first level key to add to the Map2Map2Set
	 * @param keyB: the second level key to add to the Map2Map2Set
	 * @param valueC: the value for the pair of keys to add to the Map2Map2Set
	 */
	public void add(A keyA, B keyB, C valueC)
	{
		Map2Set<B,C> mapsA = multimap.get(keyA);
		if(!contains(keyA,keyB,valueC))
			size++;
		if(mapsA == null)
		{
			mapsA = new Map2Set<B,C>();
			mapsA.add(keyB, valueC);
			multimap.put(keyA, mapsA);
		}
		else
			mapsA.add(keyB, valueC);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @return whether the Map2Map2Set contains the first level keyA
	 */
	public boolean contains(A keyA)
	{
		return multimap.containsKey(keyA);
	}

	/**
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @param keyB: the second level key to search in the Map2Map2Set
	 * @return whether the Map2Map2Set contains an entry with the two keys
	 */
	public boolean contains(A keyA, B keyB)
	{
		return multimap.containsKey(keyA) &&
			multimap.get(keyA).contains(keyB);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @param keyB: the second level key to search in the Map2Map2Set
	 * @param valueC: the value to search in the Map2Map2Set
	 * @return whether the Map2Map2Set contains an entry with the two keys
	 * and the given value
	 */
	public boolean contains(A keyA, B keyB, C valueC)
	{
		return multimap.containsKey(keyA) &&
			multimap.get(keyA).contains(keyB) &&
			multimap.get(keyA).get(keyB).contains(valueC);
	}

	/**
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @return the number of entries with keyA
	 */
	public int entryCount(A keyA)
	{
		Map2Set<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return 0;
		return mapsA.size();
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @param valueC: the value to search in the Map2Map2Set
	 * @return the number of entries with keyA that have valueC
	 */
	public int entryCount(A keyA, C valueC)
	{
		int count = 0;
		Map2Set<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return count;
		Set<B> setA = mapsA.keySet();
		for(B b : setA)
			if(mapsA.get(b).equals(valueC))
				count++;
		return count;
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @return the HashMap with all entries for keyA
	 */
	public Map2Set<B,C> get(A keyA)
	{
		return multimap.get(keyA);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @param keyB: the second level key to search in the Map2Map2Set
	 * @return the values for the entries with the two keys or null
	 * if no such entries exist
	 */	
	public Set<C> get(A keyA, B keyB)
	{
		Map2Set<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null || !mapsA.contains(keyB))
			return null;
		return mapsA.get(keyB);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @param valueC: the value to search in the Map2Map2Set
	 * @return the list of second level keys in entries with keyA and valueC
	 */	
	public HashSet<B> getMatchingKeys(A keyA, C valueC)
	{
		HashSet<B> keysB = new HashSet<B>();
		Map2Set<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return keysB;
		Set<B> setA = mapsA.keySet();
		for(B b : setA)
			if(mapsA.get(b).contains(valueC))
				keysB.add(b);
		return keysB;
	}
	
	/**
	 * @return the set of first level keys in the Map2Map2Set
	 */
	public Set<A> keySet()
	{
		return multimap.keySet();
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @return the set of second level keys in all entries with keyA
	 */
	public Set<B> keySet(A keyA)
	{
		Map2Set<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return null;
		return mapsA.keySet();
	}
	
	/**
	 * @return the number of first level keys in the Map2Map2Set
	 */
	public int keyCount()
	{
		return multimap.size();
	}
	
	
	/**
	 * Removes all entries for the given first level key
	 * @param keyA: the key to remove from the Map2Map2Set
	 */
	public void remove(A keyA)
	{
		if(multimap.get(keyA) != null)
			size -= multimap.get(keyA).size();
		multimap.remove(keyA);
	}
	
	/**
	 * Removes the entry for the given key pair
	 * @param keyA: the first level key to search in the Map2Map2Set
	 * @param keyB: the second level key to remove from the Map2Map2Set
	 */
	public void remove(A keyA, B keyB)
	{
		Map2Set<B,C> maps = multimap.get(keyA);
		if(maps != null)
		{
			size -= maps.get(keyB).size();
			maps.remove(keyB);
			if(maps.size() == 0)
				multimap.remove(keyA);
		}
	}
	
	/**
	 * @return the total number of entries in the Map2Map2Set
	 */
	public int size()
	{
		return size;
	}
}
