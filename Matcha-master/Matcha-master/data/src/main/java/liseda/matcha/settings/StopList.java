/******************************************************************************
* Utility class for accessing the list of stop words.                         *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.settings;

import java.util.HashSet;
import java.util.Set;

public class StopList
{
	
//Attributes
	
	private static HashSet<String> stopWords;
	
//Constructors
	
	private StopList(){}
	
//Public Methods
	
	public static boolean contains(String s)
	{
		if(stopWords == null)
		{
			System.err.println("Warning: StopList not initialized!");
			return false;
		}
		return stopWords.contains(s);
	}
	
	public static void init(Set<String> stop)
	{
		stopWords = new HashSet<String>(stop);
	}
	
	public Set<String> get()
	{
		return stopWords;
	}
}