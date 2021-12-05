/******************************************************************************
* Utility class for parsing data values and adding them to the ValueMap.      *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.ontology;

import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import liseda.matcha.ontology.AttributeMap;
import liseda.matcha.util.StringParser;

public class ValueParser
{
	public static void addValue(AttributeMap vMap, String indivUri, String propUri, OWLLiteral val)
	{
		String name = val.getLiteral();
		OWLDatatype d = val.getDatatype();
		if(val.isRDFPlainLiteral() || d.getIRI().equals(OWL2Datatype.XSD_STRING.getIRI()) || d.getIRI().equals(OWL2Datatype.RDF_LANG_STRING.getIRI()))
			name = StringParser.normalizeName(name);
		else if(!d.getIRI().equals(OWL2Datatype.XSD_ANY_URI.getIRI()))
			name = StringParser.normalizeFormula(name);
		vMap.add(indivUri, propUri, name, d.toStringID());
	}
}