/******************************************************************************
* A table with three columns <A,B,C> where C is comparable and fixed per      *
* <A,B>; a new C entered for the same <A,B> will replace the previous C if it *
* compares favorably.                                                         *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class Map2MapComparable<A,B,C extends Comparable<C>>
{

//Attributes
	
	private HashMap<A,HashMap<B,C>> multimap;
	private int size;
	
//Constructors

	/**
	 * Constructs a new empty Map2MapComparable
	 */
	public Map2MapComparable()
	{
		multimap = new HashMap<A,HashMap<B,C>>();
		size = 0;
	}
	
	/**
	 * Constructs a new Map2MapComparable that is a copy of
	 * the given Map2MapComparable
	 * @param m: the Map2MapComparable to copy
	 */
	public Map2MapComparable(Map2MapComparable<A,B,C> m)
	{
		multimap = new HashMap<A,HashMap<B,C>>();
		size = m.size;
		Set<A> keys = m.keySet();
		for(A a : keys)
			multimap.put(a, new HashMap<B,C>(m.get(a)));
	}

//Public Methods
	
	/**
	 * Adds the value for the given keys to the Map2MapComparable
	 * If there is already a value for the given keys, the
	 * value will be replaced
	 * @param keyA: the first level key to add to the Map2MapComparable
	 * @param keyB: the second level key to add to the Map2MapComparable
	 * @param valueC: the value for the pair of keys to add to the Map2MapComparable
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
	 * Adds the value for the given keys to the Map2MapComparable
	 * unless there is already a value for the given keys
	 * @param keyA: the first level key to add to the Map2MapComparable
	 * @param keyB: the second level key to add to the Map2MapComparable
	 * @param valueC: the value for the pair of keys to add to the Map2MapComparable
	 */
	public void addIgnore(A keyA, B keyB, C valueC)
	{
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
		{
			mapsA = new HashMap<B,C>();
			mapsA.put(keyB, valueC);
			multimap.put(keyA, mapsA);
			size++;
		}
		else if(!mapsA.containsKey(keyB))
		{
			mapsA.put(keyB, valueC);
			size++;
		}
	}
	
	/**
	 * Adds the value for the given keys to the Map2MapComparable
	 * If there is already a value for the given keys, the
	 * new value will replace the previous value only if it
	 * compares favorably as determined by the compareTo test
	 * @param keyA: the first level key to add to the Map2MapComparable
	 * @param keyB: the second level key to add to the Map2MapComparable
	 * @param valueC: the value for the pair of keys to add to the Map2MapComparable
	 */
	public void addUpgrade(A keyA, B keyB, C valueC)
	{
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
		{
			mapsA = new HashMap<B,C>();
			mapsA.put(keyB, valueC);
			multimap.put(keyA, mapsA);
			size++;
		}
		else if(!mapsA.containsKey(keyB))
		{
			mapsA.put(keyB, valueC);
			size++;
		}
		else if(mapsA.get(keyB).compareTo(valueC) < 0)
			mapsA.put(keyB, valueC);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @return whether the Map2MapComparable contains the first level keyA
	 */
	public boolean contains(A keyA)
	{
		return multimap.containsKey(keyA);
	}

	/**
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @param keyB: the second level key to search in the Map2MapComparable
	 * @return whether the Map2MapComparable contains an entry with the two keys
	 */
	public boolean contains(A keyA, B keyB)
	{
		return multimap.containsKey(keyA) &&
			multimap.get(keyA).containsKey(keyB);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @param keyB: the second level key to search in the Map2MapComparable
	 * @param valueC: the value to search in the Map2MapComparable
	 * @return whether the Map2MapComparable contains an entry with the two keys
	 * and the given value
	 */
	public boolean contains(A keyA, B keyB, C valueC)
	{
		return multimap.containsKey(keyA) &&
			multimap.get(keyA).containsKey(keyB) &&
			multimap.get(keyA).get(keyB).equals(valueC);
	}

	/**
	 * @param keyA: the first level key to search in the Map2MapComparable
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
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @param valueC: the value to search in the Map2MapComparable
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
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @return the HashMap with all entries for keyA
	 */
	public HashMap<B,C> get(A keyA)
	{
		return multimap.get(keyA);
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @param keyB: the second level key to search in the Map2MapComparable
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
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @return the maximum value in entries with keyA
	 */
	public B getKeyMaximum(A keyA)
	{
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return null;
		Vector<B> setA = new Vector<B>(mapsA.keySet());
		B max = setA.get(0);
		C maxVal = mapsA.get(max);
		for(B b : setA)
		{
			C value = mapsA.get(b);
			if(value.compareTo(maxVal) > 0)
			{
				maxVal = value;
				max = b;
			}
		}
		return max;
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @param valueC: the value to search in the Map2MapComparable
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
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @return the maximum value in entries with keyA
	 */
	public C getMaximumValue(A keyA)
	{
		HashMap<B,C> mapsA = multimap.get(keyA);
		if(mapsA == null)
			return null;
		Vector<B> setA = new Vector<B>(mapsA.keySet());
		C max = mapsA.get(setA.get(0));
		for(B b : setA)
		{
			C value = mapsA.get(b);
			if(value.compareTo(max) > 0)
				max = value;
		}
		return max;
	}
	
	/**
	 * @return the set of first level keys in the Map2MapComparable
	 */
	public Set<A> keySet()
	{
		return multimap.keySet();
	}
	
	/**
	 * @param keyA: the first level key to search in the Map2MapComparable
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
	 * @return the number of first level keys in the Map2MapComparable
	 */
	public int keyCount()
	{
		return multimap.size();
	}
	
	
	/**
	 * Removes all entries for the given first level key
	 * @param keyA: the key to remove from the Map2MapComparable
	 */
	public void remove(A keyA)
	{
		if(multimap.get(keyA) != null)
			size -= multimap.get(keyA).size();
		multimap.remove(keyA);
	}
	
	/**
	 * Removes the entry for the given key pair
	 * @param keyA: the first level key to search in the Map2MapComparable
	 * @param keyB: the second level key to remove from the Map2MapComparable
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
	 * @return the total number of entries in the Map2MapComparable
	 */
	public int size()
	{
		return size;
	}
}
