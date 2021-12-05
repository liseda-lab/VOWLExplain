/******************************************************************************
* Utility class for converting URIs into local names.                         *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.util;

public class LocalNamer
{
	
//Constructors
	
	private LocalNamer() {}
	
//Public Methods
	
	public static String getLocalName(String uri)
	{
		if(uri == null)
			return null;
		int i = uri.indexOf("#") + 1;
		if(i == 0)
			i = uri.lastIndexOf("/") + 1;
		return uri.substring(i);
	}
}