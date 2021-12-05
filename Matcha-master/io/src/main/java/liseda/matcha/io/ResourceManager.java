/******************************************************************************
* Utility class for accessing the resources used by AML.                      *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.PropertyConfigurator;

public class ResourceManager
{
	
//Attributes
	
	private static final String KNOWLEDGE = "knowledge/";
	private static final String LOG4J = "log4j.properties";
	private static final String STOP_LIST = "StopList.txt";
	private static final String WORD_NET = "wordnet/";

//Public Methods

	public static void configLog4J()
	{
		URL resource = ResourceManager.class.getClassLoader().getResource(LOG4J);
		if(resource != null)
		PropertyConfigurator.configure(resource.getPath().replace("%20", " "));
	}
	
	public static HashSet<String> getBKLexicons()
	{
		HashSet<String> bkSources = new HashSet<String>();
		String kr = getKnowledgeRoot();
		if(kr == null)
			return bkSources; 
		File ontRoot = new File(getKnowledgeRoot());
		if(ontRoot.exists())
		{
			FileFilter lex = new ExtensionFilter("Lexicon Files (*.lexicon)",
					new String[] { ".lexicon" }, false);
			File[] lexFiles = ontRoot.listFiles(lex);
			for(File f : lexFiles)
				bkSources.add(f.getPath());
		}
		return bkSources;
	}
	
	public static HashMap<String,String> getBKOntologies()
	{
		HashMap<String,String> bkSources = new HashMap<String,String>();
		String kr = getKnowledgeRoot();
		if(kr == null)
			return bkSources; 
		File ontRoot = new File(getKnowledgeRoot());
		if(ontRoot.exists())
		{
			FileFilter ont = new ExtensionFilter("Ontology Files (*.owl, *.rdf, *.rdfs, *.xml)",
					new String[] { ".owl", ".rdf", ".rdfs", ".xml" }, false);
			File[] ontFiles = ontRoot.listFiles(ont);
			for(File f : ontFiles)
			{
				String path = f.getPath().replace("%20", " ");
				String refPath = path.substring(0,path.lastIndexOf(".")) + ".xrefs";
				File f2 = new File(refPath);
				if(f2.exists())
					bkSources.put(path, refPath);
				else
					bkSources.put(path, null);
			}
		}
		return bkSources;
	}
	   
	public static String getKnowledgeRoot()
	{
		URL resource = ResourceManager.class.getClassLoader().getResource(KNOWLEDGE);
		if(resource == null)
		{
			System.err.println("WARNING: knoweledge root folder not found!");
			return null;
		}
		return resource.getPath().replace("%20", " ");
	}
	
	public static Set<String> getStopSet()
	{
		HashSet<String> stopWords = new HashSet<String>();
		URL resource = ResourceManager.class.getClassLoader().getResource(STOP_LIST);
		if(resource == null)
			System.err.println("WARNING: StopList.txt not found!");
		else
		{
			try
			{
				BufferedReader inStream = new BufferedReader(new FileReader(resource.getPath().replace("%20", " ")));
				String line;
				while((line = inStream.readLine()) != null)
					stopWords.add(line);
				inStream.close();
			}
			catch(IOException e)
			{
				System.err.println("WARNING: Could not read StopList.txt - " + e.getMessage());
			}
		}
		return stopWords;
	}

	public static String getWordNetRoot()
	{
		URL resource = ResourceManager.class.getClassLoader().getResource(WORD_NET);
		if(resource == null)
		{
			System.err.println("WARNING: wordnet root folder not found!");
			return null;
		}
		return resource.getPath();
	}
	
	public static boolean readConfig(String path)
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(path));
			//TODO: Define config file options and implement this
			in.close();
			return true;
		}
		catch(Exception e)
		{
			System.err.println("Error: Could not read config file");
			e.printStackTrace();
			System.err.println("Matching will proceed with default configuration");
			return false;
		}
	}
 }