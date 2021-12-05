/******************************************************************************
* Utility class for reading and saving Ontology Alignments from/to an OWL     *
* file.                                                                       *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.alignment.owl;

import java.io.File;
import java.io.FileNotFoundException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.io.EncodingException;
import liseda.matcha.io.ontology.OWLAPIConnector;
import liseda.matcha.vocabulary.RDFElement;
import liseda.matcha.vocabulary.SKOS;

public class AlignmentIOOWL
{
	
//Attributes
	
	//Annotation properties for mappings
	private static OWLAnnotationProperty measure;
	private static OWLAnnotationProperty provenance;
	//Object property for 'related' mappings
	private static OWLObjectProperty related;
	//The types of axioms that can encode mappings
	private static final AxiomType<?>[] TYPES = {
			AxiomType.EQUIVALENT_CLASSES,
			AxiomType.SUBCLASS_OF,
			AxiomType.DISJOINT_CLASSES,
			AxiomType.EQUIVALENT_DATA_PROPERTIES,
			AxiomType.SUB_DATA_PROPERTY,
			AxiomType.DISJOINT_DATA_PROPERTIES,
			AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
			AxiomType.SUB_OBJECT_PROPERTY,
			AxiomType.DISJOINT_OBJECT_PROPERTIES,
			AxiomType.SAME_INDIVIDUAL,
			AxiomType.DIFFERENT_INDIVIDUALS,
			AxiomType.CLASS_ASSERTION
		};
	
//Public Methods
	
	/**
	 * Reads an Alignment from an OWL file
	 * @param a: the Alignment to save
	 * @param file: the input file
	 * @throws OWLOntologyCreationException 
	 */
	public static void read(Alignment a, String file) throws OWLOntologyCreationException
	{
		//Start the connector
		OWLAPIConnector con = new OWLAPIConnector();
		//Setup the annotation properties
		measure = con.getAnnotationProperty(RDFElement.MEASURE.toRDFExtended());
		provenance = con.getAnnotationProperty(RDFElement.PROVENANCE.toRDFExtended());
		//Read the OWL alignment file
		OWLOntology o = con.openOntology(file);
		//Initialize the mapping parser
		OWLMappingReader mp = new OWLMappingReader(a);
		//Parse each type of axiom in the OWLOntology
		for(AxiomType<?> t : TYPES)
			o.axioms(t, Imports.INCLUDED).forEach(t1 -> {
				try
				{
					mp.readMapping(t1);
				}
				catch(EncodingException e)
				{
					System.err.println("WARNING: Skipping unparsable axiom " + t1.toString());
				}
			});
	}

	/**
	 * Saves an Alignment into an xml file as a list of doubles
	 * @param a: the Alignment to save
	 * @param file: the output file
	 * @throws OWLOntologyCreationException 
	 * @throws FileNotFoundException 
	 * @throws OWLOntologyStorageException 
	 */
	public static void save(Alignment a, String file) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException
	{
		OWLAPIConnector con = new OWLAPIConnector();
		con.createOntology(IRI.create(new File(file)));
		con.addImports(a);
		measure = con.getAnnotationProperty(RDFElement.MEASURE.toRDFExtended());
		con.addEntity(measure);
		provenance = con.getAnnotationProperty(RDFElement.PROVENANCE.toRDFExtended());
		con.addEntity(provenance);
		related = con.getObjectProperty(SKOS.RELATED.toURI());
		con.addEntity(related);
		con.addReflexive(related);
		OWLMappingWriter w = new OWLMappingWriter(con);
		
		for(Mapping m : a)
		{
			try
			{
				w.writeMapping(m);
			}
			catch(EncodingException e)
			{
				System.err.println("WARNING: Skipping mapping " + m.toString() + "\n" + e.getMessage());
			}
		}
		con.saveOntology(file);
	}
}