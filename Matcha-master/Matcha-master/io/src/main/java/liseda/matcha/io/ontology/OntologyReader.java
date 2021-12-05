/******************************************************************************
* Utility class that centralizes Ontology file parsing.                       *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.ontology;

import java.net.URI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import liseda.matcha.ontology.MediatorOntology;
import liseda.matcha.ontology.Ontology;

public class OntologyReader
{

//Public Methods

	/**
	 * Creates an Ontology object from an OWL ontology or SKOS thesaurus local file by using the OWL API to interpret it
	 * @param path: the path to the file to parse
	 * @return the Ontology encoding the ontology/thesaurus in the file
	 * @throws OWLOntologyCreationException
	 */
	public static Ontology parseInputOntology(String path) throws OWLOntologyCreationException
	{
		OWLAPIConnector con = new OWLAPIConnector();
		con.openOntology(path);
		Ontology l = new Ontology(path);
		l.setLocation(path);
		parse(con,l);
		return l;
	}

	/**
	 * Creates an Ontology object from an OWL ontology or SKOS thesaurus specified via an URI by using the OWL API to interpret it
	 * @param uri: the URI to the ontology/thesaurus to parse
	 * @param isBK: whether the ontology is to be used strictly as background knowledge
	 * or is to be matched (BK ontologies are not registered in the global SemanticMap or
	 * SemanticMap)
	 * @return the Ontology encoding the ontology/thesaurus in the file
	 * @throws OWLOntologyCreationException
	 */
	public static Ontology parseInputOntology(URI uri) throws OWLOntologyCreationException
	{
		OWLAPIConnector con = new OWLAPIConnector();
		con.openOntology(uri);
		Ontology l = new Ontology(uri.toString());
		l.setLocation(uri.toString());
		parse(con,l);
		return l;
	}
	
	/**
	 * Creates a MediatorOntology object from an OWL ontology or SKOS thesaurus local file by using the OWL API to interpret it
	 * @param path: the path to the file to parse
	 * @return the Ontology encoding the ontology/thesaurus in the file
	 * @throws OWLOntologyCreationException
	 */
	public static MediatorOntology parseMediatorOntology(String path) throws OWLOntologyCreationException
	{
		OWLAPIConnector con = new OWLAPIConnector();
		con.openOntology(path);
		MediatorOntology l = new MediatorOntology(path);
		parse(con,l);
		return l;
	}
	
	/**
	 * Creates a MediatorOntology object from an OWL ontology or SKOS thesaurus local file by using the OWL API to interpret it
	 * and then extends the ReferenceMap using a local reference file
	 * @param path: the path to the file to parse
	 * @param ref: the path to the reference file to extend the MediatorOntology
	 * @return the Ontology encoding the ontology/thesaurus in the file
	 * @throws OWLOntologyCreationException
	 */
	public static MediatorOntology parseMediatorOntology(String path, String ref) throws OWLOntologyCreationException
	{
		OWLAPIConnector con = new OWLAPIConnector();
		con.openOntology(path);
		MediatorOntology l = new MediatorOntology(path);
		parse(con,l);
		l.getReferenceMap().extend(ref);
		return l;
	}

	/**
	 * Creates an Ontology object from an OWL ontology or SKOS thesaurus specified via an URI by using the OWL API to interpret it
	 * @param uri: the URI to the ontology/thesaurus to parse
	 * @return the Ontology encoding the ontology/thesaurus in the file
	 * @throws OWLOntologyCreationException
	 */
	public static MediatorOntology parseMediatorOntology(URI uri) throws OWLOntologyCreationException
	{
		OWLAPIConnector con = new OWLAPIConnector();
		con.openOntology(uri);
		MediatorOntology l = new MediatorOntology(uri.toString());
		parse(con,l);
		return l;
	}


//Private Methods	

	private static void parse(OWLAPIConnector con, Ontology l)
	{
		if(con.isSKOS())
		{
			SKOSParser p = new SKOSParser(con);
			p.parse(l);
		}
		else
		{
			OWLParser p = new OWLParser(con);
			p.parse(l);
		}
	}
	
	private static void parse(OWLAPIConnector con, MediatorOntology l)
	{
		if(con.isSKOS())
		{
			SKOSParser p = new SKOSParser(con);
			p.parse(l);
		}
		else
		{
			OWLParser p = new OWLParser(con);
			p.parse(l);
		}
	}
}