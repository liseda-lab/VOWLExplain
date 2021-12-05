/******************************************************************************
* Utility class for reading/saving Ontology Alignments from/into an RDF file. *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.alignment.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.io.EncodingException;
import liseda.matcha.vocabulary.Namespace;
import liseda.matcha.vocabulary.RDFElement;

public class AlignmentIORDF
{
	
//Public Methods
	
	/**
	 * Reads an ontology alignment from an RDF file into an Alignment object
	 * @param a: the Alignment object to populate from the file
	 * @param file: the ontology alignment file to read
	 * @throws EncodingException 
	 * @throws IOException
	 */
	public static void read(Alignment a, String file) throws DocumentException, EncodingException
	{
		//Open the Alignment file using SAXReader
		SAXReader reader = new SAXReader();
		File f = new File(file);
		Document doc = reader.read(f);
		//Read the root, then go to the "Alignment" element
		Element root = doc.getRootElement();
		Element align = root.element(RDFElement.ALIGNMENT_.toString());
		//No need to read the alignment level, as it will be automatically detected from the mappings
		//String level = align.elementText(RDFElement.LEVEL.toString());
		//Initialize the Alignment
		//Try to read the ontologies
		Element o1 = align.element(RDFElement.ONTO1.toString());
		String onto1 = parseOntology(o1);
		Element o2 = align.element(RDFElement.ONTO2.toString());
		String onto2 = parseOntology(o2);
		
		boolean reverse = false;
		if(onto1.equals(a.getTargetOntology().getURI()) && onto2.equals(a.getSourceOntology().getURI()))
			reverse = true;
		else if(!(onto1.isBlank() || onto1.equals(a.getSourceOntology().getURI())) || !(onto2.isBlank() || onto2.equals(a.getTargetOntology().getURI())))
			throw new EncodingException("ERROR: Ontologies in alignment file " + file + " do not match open ontologies");
		RDFMappingReader r = new RDFMappingReader(a, reverse);
		for(Element map : align.elements(RDFElement.MAP.toString()))
		{
			for(Element cell : map.elements(RDFElement.CELL_.toString()))
			{
				try
				{
					r.readMapping(cell);
				}
				catch(EncodingException e)
				{
					System.err.println("WARNING: Skipping mapping due to encoding error - " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Saves the Alignment into an .rdf file in Alignment/EDOAL syntax
	 * @param a: the Alignment to save
	 * @param file: the output file
	 */
	public static void save(Alignment a, String file) throws FileNotFoundException
	{
		PrintWriter out = new PrintWriter(new FileOutputStream(file));
		boolean edoal = a.getLevel() == 2;
		writeHeader(a,out,edoal);
		RDFMappingWriter w = new RDFMappingWriter(out,edoal);
		for(Mapping m : a)
		{
			try
			{
				w.writeMapping(m);
			}
			catch(EncodingException e)
			{
				System.err.println("WARNING: Could not save mapping " + m.toString() + "\n" + e.getMessage());
			}
		}
		writeFooter(out);
		out.close();
	}

//Private Methods

	private static String parseOntology(Element e)
	{
		if(e == null)
			return "";
		String uri;
		if(e.isTextOnly())
			uri = e.getText();
		else
		{
			Element ont = e.element(RDFElement.ONTOLOGY_.toString());
			if(ont == null)
				return "";
			uri = ont.attributeValue(RDFElement.RDF_ABOUT.toString());
		}
		if(uri == null)
			uri = "";
		return uri;
	}
	

	
	private static void writeHeader(Alignment a, PrintWriter out, boolean edoal) //TODO: Refactor this to use SAXWriter
	{
		String level = "" + a.getLevel();
		if(edoal)
		{
			level += "EDOAL";
			Namespace[] names = {Namespace.RDF, Namespace.RDFS, Namespace.XSD, Namespace.ALIGNMENT, Namespace.EDOAL, Namespace.ALIGN_EXT};
			out.println("<?xml version='1.0' encoding='utf-8' standalone='no'?>");
			out.println("<!DOCTYPE rdf:RDF [");
			for(Namespace n : names)
				out.println("<!ENTITY " + n.uri +  "\"" + n.uri + "\">");
			out.println("]>");
			out.println();
			out.println("\t<rdf:RDF xmlns='" + Namespace.ALIGNMENT.uri + "'"); 
			for(Namespace n : names)
				out.println("\txmlns:" + n.ns + "='" + n.uri + "'");
			out.println(">");
		}
		else
		{
			Namespace[] names = {Namespace.RDF, Namespace.XSD, Namespace.ALIGN_EXT};
			out.println("<?xml version='1.0' encoding='utf-8'?>");
			out.println("\t<rdf:RDF xmlns='" + Namespace.ALIGNMENT.uri + "'");
			for(Namespace n : names)
				out.println("\txmlns:" + n.ns + "='" + n.uri + "'");
			out.println(">");
		}
	
		out.println();
		out.println("\t<" + RDFElement.ALIGNMENT_ + ">");
		out.println("\t\t<" + RDFElement.XML + ">yes</" + RDFElement.XML +">");
		out.println("\t\t<" + RDFElement.LEVEL + ">" + level + "</" + RDFElement.LEVEL + ">");
		out.println("\t\t<" + RDFElement.TYPE + ">" + a.getType() + "</" + RDFElement.TYPE + ">");
		if(a.getSourceURI() == null || a.getTargetURI() == null)
			return;
		out.println("\t\t<" + RDFElement.ONTO1 + ">");
		out.println("\t\t\t<" +  RDFElement.ONTOLOGY_ + " " + RDFElement.RDF_ABOUT.toRDF() + "=\"" + a.getSourceURI() + "\">");
		if(!a.getSourceLocation().equals(a.getSourceURI()))
			out.println("\t\t\t\t<" +  RDFElement.LOCATION + ">" + a.getSourceLocation() + "</" + RDFElement.LOCATION + ">");
		if(edoal)
		{
			out.println("\t\t\t\t<" +  RDFElement.FORMALISM + ">");
			out.println("\t\t\t\t\t<" + RDFElement.FORMALISM_ + " " + RDFElement.NAME + "=\"" + a.getSourceFormalism().getName() +
					"\" " + RDFElement.URI + "=\"" + a.getSourceFormalism().getURI() + "\"/>");
			out.println("\t\t\t\t</" + RDFElement.FORMALISM + ">");
		}
			out.println("\t\t\t</" + RDFElement.ONTOLOGY_ + ">");
			out.println("\t\t</" + RDFElement.ONTO1 + ">");
			
		out.println("\t\t<" + RDFElement.ONTO2 + ">");
		out.println("\t\t\t<" +  RDFElement.ONTOLOGY_ + " " + RDFElement.RDF_ABOUT.toRDF() + "=\"" + a.getTargetURI() + "\">");
		if(!a.getTargetLocation().equals(a.getTargetURI()))
			out.println("\t\t\t\t<" +  RDFElement.LOCATION + ">" + a.getTargetLocation() + "</" + RDFElement.LOCATION + ">");
		if(edoal)
		{
			out.println("\t\t\t\t<" +  RDFElement.FORMALISM + ">");
			out.println("\t\t\t\t\t<" + RDFElement.FORMALISM_ + " " + RDFElement.NAME + "=\"" + a.getTargetFormalism().getName() +
					"\" " + RDFElement.URI + "=\"" + a.getTargetFormalism().getURI() + "\"/>");
			out.println("\t\t\t\t</" + RDFElement.FORMALISM + ">");
		}
		out.println("\t\t\t</" + RDFElement.ONTOLOGY_ + ">");
		out.println("\t\t</" + RDFElement.ONTO2 + ">");
	}
	
	private static void writeFooter(PrintWriter out)  //TODO: Refactor this to use SAXWriter
	{
		out.println("\t</" + RDFElement.ALIGNMENT_ + ">");
		out.println("</rdf:RDF>");
	}
}