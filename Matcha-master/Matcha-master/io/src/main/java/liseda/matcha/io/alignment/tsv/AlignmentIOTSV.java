/******************************************************************************
* Utility class for saving Ontology Alignments into an RDF file.              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.alignment.tsv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.io.EncodingException;

public class AlignmentIOTSV
{

	/**
	 * Reads an ontology alignment from a TSV file into an Alignment object
	 * @param a: the Alignment object to populate from the file
	 * @param file: the ontology alignment file to read
	 * @throws IOException
	 */
	public static void read(Alignment a, String file) throws IOException 
	{
		//TODO: Extend to process complex mappings
		BufferedReader inStream = new BufferedReader(new FileReader(file));
		//First line contains the reference to AML Alignment Format
		String format = inStream.readLine();
		if(!format.equals("#Matcha Alignment File"))
		{
			inStream.close();
			throw new EncodingException("ERROR: Alignment file " + file + " not in TSV format");
		}
		//Second line contains the source ontology
		String onto1 = inStream.readLine();
		onto1.substring(onto1.indexOf("\t")+1);
		//Third line contains the target ontology
		String onto2 = inStream.readLine();
		onto2.substring(onto2.indexOf("\t")+1);
		boolean reverse = false;
		if(a.getSourceOntology().getURI() != null && a.getTargetOntology().getURI() != null)
		{
			if(onto1.equals(a.getTargetOntology().getURI()) && onto2.equals(a.getSourceOntology().getURI()))
				reverse = true;
			else if(!onto1.equals(a.getSourceOntology().getURI()) || !onto2.equals(a.getTargetOntology().getURI()))
			{
				inStream.close();
				throw new EncodingException("ERROR: Ontologies in alignment file " + file + " do not match open ontologies");
			}
		}
		//Fourth line contains the headers
		inStream.readLine();
		//And from the fifth line forward we have mappings
		String line;
		while((line = inStream.readLine()) != null)
		{
			String[] col = line.split("\t");
			if(col.length < 5)
			{
				inStream.close();
				throw new EncodingException("ERROR: Alignment file " + file + " has missing columns in \"" + line + "\"");
			}
			//First column contains the entity1 uri
			String sourceURI = col[0];
			//Third contains the entity2 uri
			String targetURI = col[2];
			//Fifth contains the similarity
			String measure = col[4];
			//Parse it, assuming 1 if a valid measure is not found
			double similarity = 1;
			if(measure != null)
			{
				try
				{
					similarity = Double.parseDouble(measure);
		            if(similarity < 0 || similarity > 1)
		            	similarity = 1;
				}
            	catch(Exception ex){/*Do nothing - use the default value*/};
            }
			//The sixth column, if it exists, contains the type of relation
			MappingRelation rel;
			if(col.length > 5)
				rel = MappingRelation.parseRelation(col[5]);
			else
				rel = MappingRelation.EQUIVALENCE;
			//The seventh column, if it exists, contains the provenance of the Mapping
			String prov = null;
			if(col.length > 6)
				prov = col[6];
			//The eight column, if it exists, contains the status of the Mapping
			MappingStatus st;
			if(col.length > 7)
				st = MappingStatus.parseStatus(col[7]);
			else
				st = MappingStatus.UNREVISED;
			if(reverse)
				a.add(new Mapping(targetURI, sourceURI, similarity, rel.inverse(), prov, st));
			else
				a.add(new Mapping(sourceURI, targetURI, similarity, rel, prov, st));
		}
		inStream.close();
	}
	
	/**
	 * Saves the Alignment into a .tsv file in AML format
	 * @param a: the Alignment to save
	 * @param file: the output file
	 */
	public static void save(Alignment a, String file) throws FileNotFoundException
	{
		PrintWriter outStream = new PrintWriter(new FileOutputStream(file));
		outStream.println("#AgreementMakerLight Alignment File");
		outStream.println("#Source ontology:\t" + a.getSourceURI());
		outStream.println("#Target ontology:\t" + a.getTargetURI());
		outStream.println("Source URI\tSource Label\tTarget URI\tTarget Label\tSimilarity\tRelationship\tProvenace\tStatus");
		for(Mapping m : a)
			outStream.println(m.getEntity1() + "\t" + a.getSourceOntology().getName(m.getEntity1()) + 
					m.getEntity2() + "\t" + a.getTargetOntology().getName(m.getEntity2()) + 
					m.getSimilarity() + "\t" + m.getProvenance() + "\t" + m.getStatus().toString());
		outStream.close();
	}
}
