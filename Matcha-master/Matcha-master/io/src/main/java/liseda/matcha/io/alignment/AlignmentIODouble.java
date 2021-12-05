/******************************************************************************
* Utility class for reading and saving Ontology Alignments from/to a doubles  *
* xml file.                                                                   *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.alignment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.io.EncodingException;

public class AlignmentIODouble
{
	/**
	 * Reads an Alignment from an xml doubles file
	 * @param a: the Alignment to save
	 * @param file: the input file
	 */
	public static void read(Alignment a, String file) throws FileNotFoundException, EncodingException
	{
		//TODO: Implement this
	}

	/**
	 * Saves an Alignment into an xml file as a list of doubles
	 * @param a: the Alignment to save
	 * @param file: the output file
	 */
	public static void save(Alignment a, String file) throws FileNotFoundException, EncodingException
	{
		if(a.getLevel() > 0)
			throw new EncodingException("ERROR: Complex mappings cannot be encoded in doubles format");
		PrintWriter outStream = new PrintWriter(new FileOutputStream(file));
		for(Mapping m : a)
			outStream.println("<" + m.getEntity1() + "> <" + m.getEntity2() + ">");
		outStream.close();
	}
}
