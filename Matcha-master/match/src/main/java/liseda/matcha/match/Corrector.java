/******************************************************************************
* A matching algorithm that corrects a previous Alignment, returning refined  *
* Mappings for the same source or target entity as input Mappings.            *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.semantics.EntityType;

public interface Corrector
{
	/**
	 * Corrects the given Alignment between the source and target Ontologies
	 * @param a: the existing alignment to correct
	 * @param e: the EntityType to correct
	 * @return the alignment with the corrected mappings between the Ontologies
	 * and all non-corrected input mappings
	 */
	public Alignment correctAlignment(Alignment a, EntityType e);
}
