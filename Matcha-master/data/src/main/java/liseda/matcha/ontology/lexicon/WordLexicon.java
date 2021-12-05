/******************************************************************************
* A Word Lexicon, derived from a Lexicon, listing the words in the names and  *
* synonyms of entities.                                                       *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.ontology.lexicon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import liseda.matcha.data.Map2MapComparable;
import liseda.matcha.data.Map2Set;
import liseda.matcha.settings.StopList;

public class WordLexicon
{

//Attributes

	//The Lexicon from which this WordLexicon was derived
	private Lexicon lex;
	//The maximum size of class blocks
	private final int MAX_BLOCK_SIZE = 10000;
	private String language;
	//The map of words to entities divided in blocks
	private HashMap<Integer,Map2Set<String,String>> wordEntities;
	//The map of entities to words with weights
	private Map2MapComparable<String,String,Double> entityWords;
	//The map of names to words
	private Map2Set<String,String> nameWords;
	//The map of word evidence contents
	private HashMap<String,Double> wordECs;
	//The map of entities to total evidence contents, which are the sum
	//of evidence contents of all their words (multiplied by frequency)
	private HashMap<String,Double> entityECs;
	//The map of name evidence contents, which is the sum of evidence
	//contents of all its words (multiplied by frequency)
	private HashMap<String,Double> nameECs;
	//Auxiliary count of words entered into the WordLexicon
	private int total;
	
//Constructors

	/**
	 * Constructs a new WordLexicon from the given Ontology
	 * @param o: the Ontology from which the WordLexicon is derived
	 * @param e: the EntityType for which to construct the WordLexicon
	 * @param lang: the language to use for this WordLexicon
	 */
	public WordLexicon(Lexicon l, String lang)
	{
		lex = l;
		language = lang;
		int size = (int)Math.ceil(1.0*l.entityCount()/MAX_BLOCK_SIZE);
		wordEntities = new HashMap<Integer,Map2Set<String,String>>();
		for(int i = 0; i < size; i++)
			wordEntities.put(i,new Map2Set<String,String>());
		entityWords = new Map2MapComparable<String,String,Double>();
		nameWords = new Map2Set<String,String>();
		wordECs = new HashMap<String,Double>();
		entityECs = new HashMap<String,Double>();
		nameECs = new HashMap<String,Double>();
		total = 0;
		
		init();
	}
	
//Public Methods

	/**
	 * @return the number of blocks in the WordLexicon
	 */
	public int blockCount()
	{
		return wordEntities.size();
	}
	
	/**
	 * @param uri: the uri of the entity to search in the WordLexicon
	 * @return the EC of the given entity
	 */
	public double getEntityEC(String uri)
	{
		if(entityECs.containsKey(uri))
			return entityECs.get(uri);
		return -1.0;
	}
	
	/**
	 * @return the set of entities in the WordLexicon
	 */
	public Set<String> getEntities()
	{
		return entityWords.keySet();
	}
	
	/**
	 * @return the language used to build this WordLexicon
	 */
	public String getLanguage()
	{
		return language;
	}
	
	/**
	 * @param n: the name to search in the WordLexicon
	 * @return the EC of the given name
	 */
	public double getNameEC(String n)
	{
		if(nameECs.containsKey(n))
			return nameECs.get(n);
		return -1.0;
	}
	
	/**
	 * @return the set of names in the WordLexicon
	 */
	public Set<String> getNames()
	{
		return nameWords.keySet();
	}

	/**
	 * @param uri: the uri of the entity to search in the WordLexicon
	 * @return the set of names for the given entity
	 */
	public Set<String> getNames(String uri)
	{
		HashSet<String> names = new HashSet<String>();
		for(String n : lex.getNames(uri))
			if(nameWords.contains(n))
				names.add(n);
		return names;
	}
	
	/**
	 * @param name: the name to search in the WordLexicon
	 * @param uri: the uri of the entity to search in the WordLexicon
	 * @return the weight of the name for the class in the WordLexicon
	 */
	public double getNameWeight(String name, String uri)
	{
		return lex.getCorrectedWeight(name,uri);
	}
	
	/**
	 * @param w: the word to search in the WordLexicon
	 * @return the EC of the given word
	 */
	public double getWordEC(String w)
	{
		if(wordECs.containsKey(w))
			return wordECs.get(w);
		return -1.0;
	}

	/**
	 * @param uri: the uri of the entity to search in the WordLexicon
	 * @return the set of words for the given entity
	 */
	public Set<String> getWordsByEntity(String uri)
	{
		if(!entityWords.contains(uri))
			return new HashSet<String>();
		return entityWords.keySet(uri);
	}
	
	/**
	 * @param name: the name to search in the WordLexicon
	 * @return the set of words for the given classId
	 */
	public Set<String> getWordsByName(String name)
	{
		if(!nameWords.contains(name))
			return new HashSet<String>();
		return new HashSet<String>(nameWords.get(name));
	}
	
	/**
	 * @return the table of words for a given block of classes
	 */
	public Map2Set<String,String> getWordTable(int block)
	{
		return wordEntities.get(block);
	}
	
	/**
	 * @param word: the word to search in the WordLexicon
	 * @param uri: the uri of the entity to search in the WordLexicon
	 * @return the weight of the word for the class in the WordLexicon
	 */
	public double getWordWeight(String word, String uri)
	{
		if(!entityWords.contains(uri, word))
			return -1.0;
		return entityWords.get(uri, word);
	}
	
//Private methods
	
	//Builds the WordLexicon from the Ontology and its Lexicon
	private void init()
	{
		//Get the entities from the Ontology
		Set<String> entities = lex.getEntities();
		//For each entity
		for(String e: entities)
		{
			//Get all names 
			Set<String> names = lex.getNamesWithLanguage(e, language);
			if(names == null || names.isEmpty())
				continue;
			//And add the words for each name 
			for(String n: names)
				if(!lex.isFormula(n))
					addWords(n, e);
		}
		//Compute the maximum EC
		double max = Math.log(total);
		//Compute and store the normalized EC for
		//each word in the WordLexicon
		for(String w : wordECs.keySet())
		{
			double ec = 1 - (Math.log(wordECs.get(w)) / max);
			wordECs.put(w, ec);
		}
		//The total EC for each class
		for(String i : entityWords.keySet())
		{
			double ec = 0.0;
			for(String w : entityWords.keySet(i))
				ec += wordECs.get(w) * getWordWeight(w, i);
			entityECs.put(i, ec);
		}
		//And the total EC for each name
		for(String n : nameWords.keySet())
		{
			double ec = 0.0;
			for(String w : nameWords.get(n))
				ec += wordECs.get(w);
			nameECs.put(n, ec);
		}
	}
			
	//Adds all words for a given name and uri
	private void addWords(String name, String uri)
	{
		String[] words = name.split(" ");
		for(String w : words)
		{
			String word = w.replaceAll("[()]", "");
			if(StopList.contains(word) || word.length() < 2 || !word.matches(".*[a-zA-Z].*"))
				continue;
			//Get the current block number (as determined by the number of classes already loaded)
			int block = entityWords.keySet().size()/MAX_BLOCK_SIZE;
			//Add the block-word-class triple
			wordEntities.get(block).add(word,uri);
			//Update the current weight of the word for the classId
			Double weight = entityWords.get(uri,word);
			if(weight == null)
				weight = lex.getCorrectedWeight(name, uri);
			else
				weight += lex.getCorrectedWeight(name, uri);
			//Add the class-word-weight triple
			entityWords.add(uri, word, weight);
			//Add the name-word pair
			nameWords.add(name, word);
			//Update the word frequency
			Double freq = wordECs.get(word);
			if(freq == null)
				freq = 1.0;
			else
				freq++;
			wordECs.put(word,freq);
			//Update the total;
			total++;
		}
	}
}