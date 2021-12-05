/******************************************************************************
* Lists the size category of the ontology matching problem.                   *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.settings;

import liseda.matcha.ontology.Ontology;

public enum SizeCategory
{
	SMALL,
	MEDIUM,
	LARGE,
	HUGE;
	
	SizeCategory(){}
		
	/**
	 * Computes the size category of the matching problem
	 * based on the number of classes of the input ontologies
	 */
	public static SizeCategory getSizeCategory(Ontology source, Ontology target)
	{
		int sSize = source.size();
		int tSize = target.size();
		int max = Math.max(sSize, tSize);
		int min = Math.min(sSize, tSize);
		if(max > 60000 || (min > 30000 && sSize*tSize > 1000000000))
			return HUGE;
		else if(max > 10000)
			return LARGE;
		else if(max > 500)
			return MEDIUM;
		else
			return SMALL;
	}
}