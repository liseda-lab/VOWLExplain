/******************************************************************************
* An algorithm that extends a Lexicon by adding new synonyms.                 *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.ontology.lexicon;

import liseda.matcha.ontology.Ontology;

public interface LexiconExtender
{
	/**
	 * Extends the Lexicons of the given Ontology
	 */
	public void extendLexicons(Ontology o);
}
