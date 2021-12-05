/******************************************************************************
* A table with four columns <A,B,C,D> where D is fixed per <A,B,C>.           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

import java.util.HashMap;
import java.util.Set;

public class Map2Map2Map<A,B,C,D>
{

//Attributes
	
	private HashMap<A,Map2Map<B,C,D>> multimap;
	private int size;
	
//Constructors

	/**
	 * Constructs a new empty Map2Map2Map
	 */
	public Map2Map2Map()
	{
		multimap = new HashMap<A,Map2Map<B,C,D>>();
		size = 0;
	}
	
	/**
	 * Constructs a new Map2Map2Map that is a copy of
	 * the given Map2Map2Map
	 * @param m: the Map2Map2Map to copy
	 */
	public Map2Map2Map(Map2Map2Map<A,B,C,D> m)
	{
		multimap = new HashMap<A,Map2Map<B,C,D>>();
		size = m.size;
		Set<A> keys = m.keySet();
		for(A a : keys)
			multimap.put(a, new Map2Map<B,C,D>(m.get(a)));
	}

//Public Methods
	
	/**
	 * Adds the value for the given keys to the Map2Map2Map
	 * @param keyA: the first level key to add to the Map2Map2Map
	 * @param keyB: the second level key to add to the Map2Map2Map
	 * @param keyC: the third level key to add to the Map2Map2Map
	 * @param valueD: the value for the triple of keys to add to the Map2Map2Map
	 */
	public void add(A keyA, B keyB, C keyC, D valueD)
	{
		Map2Map<B,C,D> mapsA = multimap.get(keyA);
		if(!contains(keyA,keyB,keyC))
			size++;
		if(mapsA == null)
		{
			mapsA = new Map2Map<B,C,D>();
			mapsA.add(keyB, keyC,valueD);
			multimap.put(keyA, mapsA);
		}
		else
			mapsA.add(keyB, keyC, valueD);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @return whether the Map2Map2Map contains the first level keyA
	 */
	public boolean contains(A keyA)
	{
		return multimap.containsKey(keyA);
	}

	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @param keyB: the second level key to search in the Map2Map2Map
	 * @return whether the Map2Map2Map contains an entry with the two keys
	 */
	public boolean contains(A keyA, B keyB)
	{
		return multimap.containsKey(keyA) &&
			multimap.get(keyA).contains(keyB);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @param keyB: the second level key to search in the Map2Map2Map
	 * @param keyC: the third level key to search in the Map2Map2Map
	 * @return whether the Map2Map2Map contains an entry with the three keys
	 * and the given value
	 */
	public boolean contains(A keyA, B keyB, C keyC)
	{
		return multimap.containsKey(keyA) &&
			   multimap.get(keyA).contains(keyB, keyC);
	}

	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @param keyB: the second level key to search in the Map2Map2Map
	 * @param keyC: the third level key to search in the Map2Map2Map
	 * @param valueD: the value to search in the Map2Map2Map
	 * @return whether the Map2Map2Map contains an entry with the three keys
	 * and the given value
	 */
	public boolean contains(A keyA, B keyB, C keyC, D valueD)
	{
		return multimap.containsKey(keyA) &&
			   multimap.get(keyA).contains(keyB, keyC) &&
			   multimap.get(keyA).get(keyB, keyC).equals(valueD);
			
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @return the number of entries with keyA
	 */
	public int entryCount(A keyA)
	{
		Map2Map<B,C,D> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return 0;
		return mapsA.size();
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @param valueD: the value to search in the Map2Map2Map
	 * @return the number of entries with keyA that have valueD
	 */
	public int entryCount(A keyA, D valueD)
	{
		int count = 0;
		Map2Map<B,C,D> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return count;
		Set<B> setB = mapsA.keySet();
		for(B b : setB)
			count += mapsA.entryCount(b, valueD);
		return count;
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @return the HashMap with all entries for keyA
	 */
	public Map2Map<B,C,D> get(A keyA)
	{
		return multimap.get(keyA);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @param keyB: the second level key to search in the Map2Map2Map
	 * @return the values for the entries with the two keys or null
	 * if no such entries exist
	 */	
	public HashMap<C,D> get(A keyA, B keyB)
	{
		Map2Map<B,C,D> mapsA = multimap.get(keyA);
		if(mapsA == null || !mapsA.contains(keyB))
			return null;
		return mapsA.get(keyB);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @param keyB: the second level key to search in the Map2Map2Map
	 * @param keyC: the third level key to search in the Map2Map2Map
	 * @return the values for the entries with the two keys or null
	 * if no such entries exist
	 */	
	public D get(A keyA, B keyB, C keyC)
	{
		Map2Map<B,C,D> mapsA = multimap.get(keyA);
		if(mapsA == null || !mapsA.contains(keyB))
			return null;
		return mapsA.get(keyB, keyC);
	}
	
	/**
	 * @return the set of first level keys in the Map2Map2Map
	 */
	public Set<A> keySet()
	{
		return multimap.keySet();
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @return the set of second level keys in all entries with keyA
	 */
	public Set<B> keySet(A keyA)
	{
		Map2Map<B,C,D> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return null;
		return mapsA.keySet();
	}
	
	/**
	 * @return the number of first level keys in the Map2Map2Map
	 */
	public int keyCount()
	{
		return multimap.size();
	}
	
	
	/**
	 * Removes all entries for the given first level key
	 * @param keyA: the key to remove from the Map2Map2Map
	 */
	public void remove(A keyA)
	{
		if(multimap.get(keyA) != null)
			size -= multimap.get(keyA).size();
		multimap.remove(keyA);
	}
	
	/**
	 * Removes the entry for the given key pair
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @param keyB: the second level key to remove from the Map2Map2Map
	 */
	public void remove(A keyA, B keyB)
	{
		Map2Map<B,C,D> maps = multimap.get(keyA);
		if(maps != null)
		{
			size -= maps.get(keyB).size();
			maps.remove(keyB);
			if(maps.size() == 0)
				multimap.remove(keyA);
		}
	}
	
	/**
	 * Removes the entry for the given key triple
	 * @param keyA: the first level key to search in the Map2Map2Map
	 * @param keyB: the second level key to remove from the Map2Map2Map
	 * @param keyC: the third level key to remove from the Map2Map2Map
	 */
	public void remove(A keyA, B keyB, C keyC)
	{
		Map2Map<B,C,D> maps = multimap.get(keyA);
		if(maps != null) 
		{
			size--;
			maps.remove(keyB, keyC);
			if(maps.size() == 0)
				multimap.remove(keyA);
		}
			
	}
	
	/**
	 * @return the total number of entries in the Map2Map2Map
	 */
	public int size()
	{
		return size;
	}
}
