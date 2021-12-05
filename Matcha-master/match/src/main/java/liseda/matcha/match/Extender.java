/******************************************************************************
* A matching algorithm that extends a previous Alignment, returning new       *
* Mappings between the source and target ontologies.                          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.semantics.EntityType;

public interface Extender
{
	/**
	 * Extends the given Alignment between the source and target Ontologies
	 * @param a: the existing alignment to extend
	 * @param e: the EntityType to match
	 * @param thresh: the similarity threshold for the extention
	 * @return the alignment with (only) the new mappings between the Ontologies
	 */
	public Alignment extendAlignment(Alignment a, EntityType e, double thresh);
}
