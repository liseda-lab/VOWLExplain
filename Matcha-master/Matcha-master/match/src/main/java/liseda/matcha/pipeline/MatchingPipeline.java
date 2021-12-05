/******************************************************************************
* A pipeline comprising a series of matching and filtering algorithms.        *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.pipeline;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.ontology.Ontology;

public interface MatchingPipeline
{
	/**
	 * Matches the source and target Ontologies, returning an Alignment between them
	 * @param o1: the source Ontology to match
	 * @param o2: the target Ontology to match
	 */
	public Alignment match(Ontology o1, Ontology o2);
}
