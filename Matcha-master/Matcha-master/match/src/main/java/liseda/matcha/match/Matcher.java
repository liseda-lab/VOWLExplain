/******************************************************************************
* A matching algorithm that maps the source and target Ontologies globally.   *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;

public interface Matcher
{
	/**
	 * Matches the source and target Ontologies, returning an Alignment between them
	 * @param o1: the source Ontology to match
	 * @param o2: the target Ontology to match
	 * @param e: the EntityType to match
	 * @param thresh: the similarity threshold for the alignment
	 * @return the alignment between the source and target ontologies
	 */
	public Alignment match(Ontology o1, Ontology o2, EntityType e, double thresh);
}
