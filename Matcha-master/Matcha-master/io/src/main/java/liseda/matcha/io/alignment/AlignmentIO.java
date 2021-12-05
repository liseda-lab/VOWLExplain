/******************************************************************************
* Utility class for reading and saving Ontology Alignments that automatically *
* chooses the file format based on the extension.                             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.alignment;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.io.EncodingException;
import liseda.matcha.io.alignment.owl.AlignmentIOOWL;
import liseda.matcha.io.alignment.rdf.AlignmentIORDF;
import liseda.matcha.io.alignment.tsv.AlignmentIOTSV;

public class AlignmentIO
{
	/**
	 * Reads an Alignment from a file
	 * @param a: the Alignment to save
	 * @param file: the input file
	 */
	public static void read(Alignment a, String file) throws Exception
	{
		if(file.endsWith(".rdf") || file.endsWith(".edoal"))
			AlignmentIORDF.read(a, file);
		else if(file.endsWith(".owl"))
			AlignmentIOOWL.read(a, file);
		else if(file.endsWith(".tsv"))
			AlignmentIOTSV.read(a, file);
		else
			throw new EncodingException("ERROR: Unknown alignment file extension: file");
	}

	/**
	 * Saves an Alignment into a file
	 * @param a: the Alignment to save
	 * @param file: the output file
	 */
	public static void save(Alignment a, String file) throws Exception
	{
		if(file.endsWith(".rdf") || file.endsWith(".edoal"))
			AlignmentIORDF.save(a, file);
		else if(file.endsWith(".owl"))
			AlignmentIOOWL.save(a, file);
		else if(file.endsWith(".tsv"))
			AlignmentIOTSV.save(a, file);
		else
			throw new EncodingException("ERROR: Unknown alignment file extension: file");
	}
}
