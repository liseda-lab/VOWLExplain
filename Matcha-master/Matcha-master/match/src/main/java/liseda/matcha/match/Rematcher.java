/******************************************************************************
* A matching algorithm that rematches the mapped entities in an Alignment,    *
* either computing new similarities for the mappings, or producing new        *
* mappings entirely (e.g. by combining or refining existing mappings).        *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.semantics.EntityType;

public interface Rematcher
{
	/**
	 * Recomputes similarities for mappings of a given Alignment
	 * @param a: the existing alignment
	 * @param e: the EntityType for which to recompute similarities
	 * @return the Alignment with the new similarities
	 */
	public Alignment rematch(Alignment a, EntityType e);
}
