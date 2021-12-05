/******************************************************************************
* A table with two columns <A,B> with Bs ordered ascendingly per A.           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class Map2List<A,B extends Comparable<B>>
{

//Attributes
	
	private HashMap<A,Vector<B>> multimap;
	private int size;
	
//Constructors

	/**
	 * Constructs a new empty Map2List
	 */
	public Map2List()
	{
		multimap = new HashMap<A,Vector<B>>();
		size = 0;
	}
	
	/**
	 * Constructs a new Map2List that is a copy of
	 * the given Map2List
	 * @param m: the Map2List to copy
	 */
	public Map2List(Map2List<A,B> m)
	{
		multimap = new HashMap<A,Vector<B>>();
		size = m.size;
		Set<A> keys = m.keySet();
		for(A a : keys)
			multimap.put(a, new Vector<B>(m.get(a)));
	}

//Public Methods
	
	/**
	 * Adds the value for the given key to the Map2List, or
	 * upgrades the value if an equal value already exists
	 * (and if compareTo and equals differ)
	 * @param key: the key to add to the Map2List
	 * @param value: the value to add to the Map2List
	 */
	public void add(A key, B value)
	{
		Vector<B> list = multimap.get(key);
		if(!contains(key,value))
			size++;
		if(list == null)
		{
			list = new Vector<B>(0,1);
			list.add(value);
			multimap.put(key, list);
		}
		else
		{
			int index = list.indexOf(value);
			if(index == -1)
				list.add(value);
			else if(value.compareTo(list.get(index)) > 0)
			{
				list.remove(index);
				list.add(value);
			}
		}
	}
	
	/**
	 * Adds the value for the given key to the Map2List
	 * @param key: the key to add to the Map2List
	 * @param values: the value to add to the Map2List
	 */
	public void addAll(A key, Collection<B> values)
	{
		for(B val : values)
			add(key, val);
	}

	/**
	 * @param key: the key to search in the Map2List
	 * @return whether the Map2List contains the key
	 */
	public boolean contains(A key)
	{
		return multimap.containsKey(key);
	}

	/**
	 * @param key: the key to search in the Map2List
	 * @param value: the value to search in the Map2List
	 * @return whether the Map2List contains an entry with the key and value
	 */
	public boolean contains(A key, B value)
	{
		return multimap.containsKey(key) &&
			multimap.get(key).contains(value);
	}
	
	/**
	 * @param key: the key to search in the Map2List
	 * @return the number of entries with key
	 */
	public int entryCount(A key)
	{
		Vector<B> list = multimap.get(key);
		if(list == null)
			return 0;
		return list.size();
	}
	
	/**
	 * @param key: the key to search in the Map2List
	 * @return the Vector with all entries for key
	 */
	public Vector<B> get(A key)
	{
		return multimap.get(key);
	}
	
	/**
	 * @return the set of keys in the Map2List
	 */
	public Set<A> keySet()
	{
		return multimap.keySet();
	}
	
	/**
	 * @return the number of keys in the Map2List
	 */
	public int keyCount()
	{
		return multimap.size();
	}
	
	/**
	 * Removes all values for the given key
	 * @param key: the key to remove from the Map2List
	 */
	public void remove(A key)
	{
		if(multimap.get(key) != null)
			size -= multimap.get(key).size();
		multimap.remove(key);
	}
	
	/**
	 * Removes the given value for the given key
	 * @param key: the key to search in the Map2List
	 * @param value: the value to remove from the Map2List
	 */
	public void remove(A key, B value)
	{
		Vector<B> values = multimap.get(key);
		if(values != null)
		{
			values.remove(value);
			if(values.isEmpty())
				multimap.remove(key);
			size--;
		}
	}
	
	/**
	 * @return the total number of entries in the Map2List
	 */
	public int size()
	{
		return size;
	}
}
