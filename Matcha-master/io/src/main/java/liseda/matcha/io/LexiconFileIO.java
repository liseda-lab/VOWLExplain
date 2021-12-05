/******************************************************************************
* A Mediator between the source and target Ontologies.                        *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import liseda.matcha.ontology.lexicon.LexicalType;
import liseda.matcha.ontology.lexicon.Lexicon;


public class LexiconFileIO
{

	/**
	 * Reads a Lexicon from a given lexicon file
	 * @param file: the lexicon file
	 */
	public static Lexicon readLexicon(String file) throws IOException
	{
		Lexicon l = new Lexicon();
		BufferedReader inStream = new BufferedReader(new FileReader(file));
		String line;
		while((line = inStream.readLine()) != null)
		{
			String[] lex = line.split("\t");
			String uri;
			try
			{
				int id = Integer.parseInt(lex[0]);
				uri = (new File(file)).toURI() + "#" + id;
			}
			catch(NumberFormatException e)
			{
				uri = lex[0];
			}
			String name = lex[1];
			double weight = Double.parseDouble(lex[2]);
			l.add(uri,name,"en",LexicalType.LABEL,"",weight);
		}
		inStream.close();
		return l;
	}

	/**
	 * Saves a Lexicon to a specified file
	 * @param file: the file on which to save the Lexicon
	 */
	public void save(Lexicon l, String file) throws Exception
	{
		PrintWriter outStream = new PrintWriter(new FileOutputStream(file));
		for(String i : l.getEntities())
			for(String n : l.getNames(i))
				outStream.println(i + "\t" + n + "\t" + l.getWeight(n,i));
		outStream.close();
	}
}