/******************************************************************************
* Lists the WordMatcher strategy options.                                     *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.settings;

public enum WordMatchStrategy
{
	BY_ENTITY ("By_Entity"),
	BY_NAME ("By_Name"),
	AVERAGE ("Average"),
	MAXIMUM ("Maximum"),
	MINIMUM ("Minimum");
	
	String label;
	
	WordMatchStrategy(String s)
    {
    	label = s;
    }
	
	public static WordMatchStrategy parseStrategy(String strat)
	{
		for(WordMatchStrategy s : WordMatchStrategy.values())
			if(strat.equalsIgnoreCase(s.toString()))
				return s;
		return null;
	}
	
    public String toString()
    {
    	return label;
	}
}