/******************************************************************************
* An algorithm that removes mappings from an input Alignment according to     *
* predetermined criteria.                                                     *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.filter;

import liseda.matcha.alignment.Alignment;

public interface Filterer
{
	/**
	 * Filters problem mappings from the given Alignment
	 * @param a: the Alignment to filter
	 */
	public Alignment filter(Alignment a);
}