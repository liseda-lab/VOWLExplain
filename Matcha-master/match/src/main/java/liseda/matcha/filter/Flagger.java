/******************************************************************************
* An algorithm that identifies problem mappings in an input Alignment.        *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.filter;

import liseda.matcha.alignment.Alignment;

public interface Flagger
{
	/**
	 * Flags problem mappings in the given Alignment
	 * @param a: the Alignment to flag
	 */
	public void flag(Alignment a);
}