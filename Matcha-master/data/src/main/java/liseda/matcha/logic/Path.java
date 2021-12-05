/******************************************************************************
* A path between two ontology entities, represented as a set of Mapping       *
* indeces.                                                                    *
*                                                                             *
* @authors Daniel Faria & Emanuel Santos                                      *
******************************************************************************/
package liseda.matcha.logic;

import java.util.Collection;
import java.util.HashSet;

public class Path<X> extends HashSet<X> implements Comparable<Path<X>>
{

//Attributes
	
	private static final long serialVersionUID = 1L;
	
//Constructors
	
	public Path()
	{
		super();
	}
	
	public Path(X i)
	{
		super();
		add(i);
	}
	
	public Path(Collection<X> p)
	{
		super(p);
	}
	
//Public Methods
	
	@Override
	public int compareTo(Path<X> p)
	{
		return this.size()-p.size();
	}

	/**
	 * Tests if this path contains all elements of another path.
	 * @param p: the path to test for containment by this path
	 * @return whether this path contains all elements of p
	 */
	public boolean contains(Path<X> p)
	{
		return this.containsAll(p);
	}
		
	/**
	 * Merges this path with another given path by adding all distinct
	 * entries and removing all shared entries, so as to obtain a minimal path
	 * (i.e., this _UNION_ p - this _INTERSECTION_ p)
	 * @param p: the path to merge with this path
	 */
	public void merge(Path<X> p)
	{
		Path<X> intersection = new Path<X>(p);
		intersection.retainAll(this);
		addAll(p);
		removeAll(intersection);
	}
	
	/**
	 * @return this path in String form
	 */
	public String toString()
	{
		String a = "[";
		for(X i: this)
			a += " " + i.toString();
		a += "]";
		return a;
	}	
}
