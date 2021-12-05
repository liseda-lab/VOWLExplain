/******************************************************************************
* Lists the Selection Types.                                                  *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.settings;

public enum SelectionType
{
   	STRICT ("Strict"),
   	PERMISSIVE ("Permissive"),
   	HYBRID ("Hybrid");
	    	
   	final String value;
    	
   	SelectionType(String s)
   	{
   		value = s;
   	}
	    	
   	public String toString()
   	{
   		return value;
   	}
   	
	public static SelectionType getSelectionType(SizeCategory size)
	{
		if(size.equals(SizeCategory.SMALL))
			return SelectionType.STRICT;
		else if(size.equals(SizeCategory.MEDIUM))
			return SelectionType.PERMISSIVE;
		else
			return SelectionType.HYBRID;
	}
	    	
	public static SelectionType parseSelector(String selector)
	{
		for(SelectionType s : SelectionType.values())
			if(selector.equalsIgnoreCase(s.toString()))
				return s;
		return null;
	}
}