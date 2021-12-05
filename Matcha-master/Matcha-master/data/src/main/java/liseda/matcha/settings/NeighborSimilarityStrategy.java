/******************************************************************************
* Lists the NeighborSimilarityMatcher strategy options.                       *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.settings;

public enum NeighborSimilarityStrategy
{
	ANCESTORS ("Ancestors"),
	DESCENDANTS ("Descendants"),
	AVERAGE ("Average"),
	MAXIMUM ("Maximum"),
	MINIMUM ("Minimum");
	
	String label;
	
	NeighborSimilarityStrategy(String s)
    {
    	label = s;
    }
	
	public static NeighborSimilarityStrategy parseStrategy(String strat)
	{
		for(NeighborSimilarityStrategy s : NeighborSimilarityStrategy.values())
			if(strat.equalsIgnoreCase(s.toString()))
				return s;
		return null;
	}
	
    public String toString()
    {
    	return label;
	}
}