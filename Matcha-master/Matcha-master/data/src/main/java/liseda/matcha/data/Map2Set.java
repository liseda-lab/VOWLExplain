/******************************************************************************
* A table with two columns <A,B> with hash indexing on both.                  *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Map2Set<A,B>
{

//Attributes
	
	private HashMap<A,HashSet<B>> multimap;
	private int size;
	
//Constructors

	/**
	 * Constructs a new empty Map2Set
	 */
	public Map2Set()
	{
		multimap = new HashMap<A,HashSet<B>>();
		size = 0;
	}
	
	/**
	 * Constructs a new Map2Set that is a copy of
	 * the given Map2Set
	 * @param m: the Map2Set to copy
	 */
	public Map2Set(Map2Set<A,B> m)
	{
		multimap = new HashMap<A,HashSet<B>>();
		size = m.size;
		Set<A> keys = m.keySet();
		for(A a : keys)
			multimap.put(a, new HashSet<B>(m.get(a)));
	}

//Public Methods
	
	/**
	 * Adds the value for the given key to the Map2Set, or
	 * updates the value if an equal value already exists
	 * @param key: the key to add to the Map2Set
	 * @param value: the value to add to the Map2Set
	 */
	public void add(A key, B value)
	{
		HashSet<B> set = multimap.get(key);
		if(!contains(key,value))
			size++;
		if(set == null)
		{
			set = new HashSet<B>();
			set.add(value);
			multimap.put(key, set);
		}
		else 
			set.add(value);	
	}
	
	/**
	 * Adds the value for the given key to the Map2Set
	 * @param key: the key to add to the Map2Set
	 * @param values: the value to add to the Map2Set
	 */
	public void addAll(A key, Collection<B> values)
	{
		for(B val : values)
			add(key, val);
	}

	/**
	 * @param key: the key to search in the Map2Set
	 * @return whether the Map2Set contains the key
	 */
	public boolean contains(A key)
	{
		return multimap.containsKey(key);
	}

	/**
	 * @param key: the key to search in the Map2Set
	 * @param value: the value to search in the Map2Set
	 * @return whether the Map2Set contains an entry with the key and value
	 */
	public boolean contains(A key, B value)
	{
		return multimap.containsKey(key) &&
			multimap.get(key).contains(value);
	}
	
	/**
	 * @param key: the key to search in the Map2Set
	 * @return the number of entries with key
	 */
	public int entryCount(A key)
	{
		Set<B> set = multimap.get(key);
		if(set == null)
			return 0;
		return set.size();
	}
	
	/**
	 * @param key: the key to search in the Map2Set
	 * @return the set of all entries for key
	 */
	public Set<B> get(A key)
	{
		return multimap.get(key);
	}
	
	/**
	 * @return the set of keys in the Map2Set
	 */
	public Set<A> keySet()
	{
		return multimap.keySet();
	}
	
	/**
	 * @return the number of keys in the Map2Set
	 */
	public int keyCount()
	{
		return multimap.size();
	}
	
	/**
	 * Removes all values for the given key
	 * @param key: the key to remove from the Map2Set
	 */
	public void remove(A key)
	{
		if(multimap.get(key) != null)
			size -= multimap.get(key).size();
		multimap.remove(key);
	}
	
	/**
	 * Removes the given value for the given key
	 * @param key: the key to search in the Map2Set
	 * @param value: the value to remove from the Map2Set
	 */
	public void remove(A key, B value)
	{
		Set<B> values = multimap.get(key);
		if(values != null)
		{
			values.remove(value);
			if(values.isEmpty())
				multimap.remove(key);
			size--;
		}
	}
	
	/**
	 * @return the total number of entries in the Map2Set
	 */
	public int size()
	{
		return size;
	}
}
