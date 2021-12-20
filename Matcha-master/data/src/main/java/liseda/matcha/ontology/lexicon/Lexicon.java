/******************************************************************************
* A Lexicon, listing the names and synonyms of semantic entities, weighted    *
* according to their provenance.                                              *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.ontology.lexicon;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import liseda.matcha.data.Map2Map2List;
import liseda.matcha.data.Map2Map2Set;
import liseda.matcha.data.Map2Set;
import liseda.matcha.util.MapSorter;


public class Lexicon
{

//Attributes
	
	//The table of entity uris to names and associated metadata, for matching by entity
	private Map2Map2List<String,String,LexicalMetadata> URINames;
	//The table of languages to entity names to uris for hash-based matching 
	private Map2Map2Set<String,String,String> langNameURIs;
	//The table of entity names to uris for quick searching 
	private Map2Set<String,String> nameURIs;
	//The language counts (number of entities covered by that language)
	private Map<String,Integer> langCount;
	
//Constructors

	/**
	 * Creates a new empty Lexicon, initializing the multimaps
	 * and the list of provenances
	 */
	public Lexicon()
	{
		URINames = new Map2Map2List<String,String,LexicalMetadata>();
		langNameURIs = new Map2Map2Set<String,String,String>();
		nameURIs = new Map2Set<String,String>();
		langCount = new LinkedHashMap<String,Integer>();
	}
	
	/**
	 * Creates a new Lexicon that is a copy of the given Lexicon
	 * @param l: the Lexicon to copy
	 */
	public Lexicon(Lexicon l)
	{
		URINames = new Map2Map2List<String,String,LexicalMetadata>(l.URINames);
		langNameURIs = new Map2Map2Set<String,String,String>(l.langNameURIs);
		nameURIs = new Map2Set<String,String>(l.nameURIs);
		langCount = new LinkedHashMap<String,Integer>(l.langCount);
	}
	
//Public Methods

	/**
	 * Adds a new entry to the Lexicon
	 * @param uri: the uri of the entity to add
	 * @param name: the name of the entry to add
	 * @param language: the language of the entry to add
	 * @param type: the LexicalType of the entry to add (localName, label, etc)
	 * @param source: the source of the entry (ontology URI, etc)
	 * @param weight: the numeric weight of the entry, in [0.0,1.0]
	 */
	public void add(String uri, String name, String language, LexicalType type, String source, double weight)
	{
		//First ensure that the name is not null or empty
		if(name == null || name.equals(""))
			return;
		if(language.length() > 2)
			language = language.substring(0, 2);

		LexicalMetadata p = new LexicalMetadata(type, source, language, weight);
		//If there is no name with that language for that URI, increment the language count
		if(!URINames.contains(uri, name) || !URINames.get(uri, name).contains(p))
		{
			Integer i = langCount.get(language);
			if(i == null)
				langCount.put(language, 1);
			else
				langCount.put(language, i+1);			
		}
		URINames.add(uri,name,p);
		langNameURIs.add(language, name, uri);
		nameURIs.add(name,uri);
	}
	
	/**
	 * @param name: the name to check in the Lexicon
	 * @return whether an entity of the given EntityType in the Lexicon contains the name
	 */
	public boolean contains(String name)
	{
		return nameURIs.contains(name);
	}
	
	/**
	 * @param uri: the uri of the entity to check in the Lexicon
	 * @param name: the name to check in the Lexicon
	 * @return whether the Lexicon contains the name for the entity
	 */
	public boolean contains(String uri, String name)
	{
		return URINames.contains(uri,name);
	}
	
	/**
	 * @param lang the language to search in the Lexicon
	 * @return whether the Lexicon contains names with the given language
	 */
	public boolean containsLanguage(String lang)
	{
		return langCount.containsKey(lang);
	}
	
	/**
	 * @param uri: the uri of the entity to check in the Lexicon
	 * @return whether the Lexicon contains a name for the entity
	 * other than a small formula (i.e., < 10 characters)
	 */
	public boolean containsNonSmallFormula(String uri)
	{
		if(!URINames.contains(uri))
			return false;
		for(String n : URINames.keySet(uri))
		{
			if(n.length() >= 10)
				return true;
			for(LexicalMetadata p : URINames.get(uri,n))
				if(!p.getType().equals(LexicalType.FORMULA))
					return true;
		}
		return false;
	}
	
	/**
	 * @return the number of entities in the Lexicon
	 */
	public int entityCount()
	{
		return URINames.keyCount();
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the provenances associated with the name,entity pair
	 */	
	public Vector<LexicalMetadata> get(String name, String uri)
	{
		return URINames.get(uri, name);
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param internal: whether to restrict the search to internal Lexicon entries
	 * or consurier extension entries
	 * @return the entity of the given EntityType associated with the name that has the highest
	 * provenance weight, or null if either no entity or two or more such entities are found
	 */
	public String getBestEntity(String name, boolean internal)
	{
		Set<String> hits;
		if(internal)
			hits = getInternalEntities(name);
		else
			hits = getEntities(name);
		if(hits == null)
			return null;
		
		Vector<String> bestEntities = new Vector<String>(1,1);
		double weight;
		double maxWeight = 0.0;
		
		for(String i : hits)
		{
			weight = getWeight(name,i);
			if(weight > maxWeight)
			{
				maxWeight = weight;
				bestEntities = new Vector<String>(1,1);
				bestEntities.add(i);
			}
			else if(weight == maxWeight)
			{
				bestEntities.add(i);
			}
		}
		if(bestEntities.size() != 1)
			return null;
		return bestEntities.get(0);
	}
	
	/**
	 * @return the language that best covers the entities in this Lexicon
	 * or "en" if no language is listed yet
	 */
	public String getBestLanguage()
	{
		if(langCount.isEmpty())
			return "en";
		langCount = MapSorter.sortDescending(langCount);
		return langCount.keySet().iterator().next();
	}
	
	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the name associated with the entity that has the best provenance
	 */
	public String getBestName(String uri)
	{
		Map<String,LexicalMetadata> results = new HashMap<String,LexicalMetadata>();
		if(!URINames.contains(uri))
			return "";
		for(String n : URINames.keySet(uri))
			results.put(n,URINames.get(uri, n).iterator().next());
		results = MapSorter.sortDescending(results);
		return results.keySet().iterator().next();
	}
	
	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @param lang: the language desired
	 * @return the name associated with the entity that has the best provenance
	 */
	public String getBestName(String uri, String lang)
	{
		Map<String,LexicalMetadata> results = new HashMap<String,LexicalMetadata>();
		if(!URINames.contains(uri))
			return "";

		for(String n : URINames.keySet(uri))
		{
			for(LexicalMetadata p : URINames.get(uri, n))
			{
				if(p.getLanguage().equals(lang))
				{
					results.put(n,p);
					break;
				}
			}
		}
		if(results.size() == 0)
			return "";
		results = MapSorter.sortDescending(results);
		return results.keySet().iterator().next();
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the weight corresponding to the provenance of the name for that entity with a
	 * correction factor depending on how many names of that provenance the the entity has
	 */
	public double getCorrectedWeight(String name, String uri)
	{
		Vector<LexicalMetadata> meta = URINames.get(uri, name);
		if(meta.isEmpty())
			return 0.0;
		double weight = 0.0;
		double correction = 0.0;
		for(LexicalMetadata p : meta)
		{
			if(p.getWeight() > weight)
			{
				weight = p.getWeight();
				correction = nameCount(uri,p.getType())/100.0;
			}
		}
		return weight - correction;
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @param lang: the language to search in the Lexicon
	 * @return the weight corresponding to the provenance of the name for that entity with a
	 * correction factor depending on how many names of that provenance the the entity has
	 */
	public double getCorrectedWeight(String name, String uri, String lang)
	{
		Vector<LexicalMetadata> meta = URINames.get(uri, name);
		if(meta.isEmpty())
			return 0.0;
		for(LexicalMetadata p : meta)
		{
			if(p.getLanguage().equals(lang))
			{
				double correction = nameCount(uri,p.getType(),p.getLanguage())/100.0;
				return p.getWeight() - correction;
			}
		}
		return 0.0;
	}

	/**
	 * @return the list of entities in this Lexicon
	 */
	public Set<String> getEntities()
	{
		return URINames.keySet();
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @return the list of entities associated with the name
	 */
	public Set<String> getEntities(String name)
	{
		return nameURIs.get(name);
	}
	
	/**
	 * @param name: the class name to search in the Lexicon
	 * @param lang: the language of the names to get from the Lexicon
	 * @return the list of classes associated with the name with the
	 * given language
	 */
	public Set<String> getEntities(String name, String lang)
	{
		Set<String> hits = nameURIs.get(name);
		HashSet<String> classesLang = new HashSet<String>();
		if(hits == null)
			return classesLang;
		for(String i : hits)
			for(LexicalMetadata p : URINames.get(i, name))
				if(p.getLanguage().equals(lang))
					classesLang.add(i);
		return classesLang;
	}
	
	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the list of languages declared for the entity
	 */
	public Set<String> getEntityLanguages(String uri)
	{
		Set<String> hits = URINames.keySet(uri);
		HashSet<String> langs = new HashSet<String>();
		if(hits == null)
			return langs;
		for(String i : hits)
			for(LexicalMetadata p : URINames.get(uri, i))
				langs.add(p.getLanguage());
		return langs;
	}
	
	/**
	 * @param uri: the index of the entity to search in the Lexicon
	 * @return the list of local names associated with the class
	 */
	public Set<String> getInternalNames(String uri)
	{
		HashSet<String> localHits = new HashSet<String>();
		if(URINames.contains(uri))
		{
			Set<String> hits = URINames.keySet(uri);
			for(String s : hits)
				if(!isExternal(s,uri))
					localHits.add(s);
		}
		return localHits;
	}
	
	/**
	 * @param e: the EntityType to get from the Lexicon
	 * @param name: the name to search in the Lexicon
	 * @return the list of entities associated with the name from
	 * a local source
	 */
	public Set<String> getInternalEntities(String name)
	{
		Set<String> hits = nameURIs.get(name);
		HashSet<String> localHits = new HashSet<String>();
		if(hits == null)
			return localHits;
		for(String i : hits)
			if(!isExternal(name,i))
				localHits.add(i);
		return localHits;
	}
	
	/**
	 * @param lang: the language code to search in the Lexicon
	 * @return the number of Lexical entries with that language
	 */
	public Integer getLanguageCount(String lang)
	{
		return langCount.get(lang);
	}
	
	/**
	 * @return the set of languages in the Lexicon
	 */
	public Set<String> getLanguages()
	{
		return langCount.keySet();
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the index of the entity to search in the Lexicon
	 * @return the list of languages declared for the name,entity pair
	 */
	public Set<String> getLanguages(String name, String uri)
	{
		Vector<LexicalMetadata> meta = URINames.get(uri,name);
		HashSet<String> langs = new HashSet<String>();
		if(meta == null)
			return langs;
		for(LexicalMetadata p : meta)
			langs.add(p.getLanguage());
		return langs;
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the index of the entity to search in the Lexicon
	 * @param t: the LexicalType
	 * @return the list of languages declared for the name,entity pair
	 */
	public Set<String> getLanguages(String name, String uri, LexicalType t)
	{
		Vector<LexicalMetadata> meta = URINames.get(uri,name);
		HashSet<String> langs = new HashSet<String>();
		if(meta == null)
			return langs;
		for(LexicalMetadata p : meta)
			if(p.getType().equals(t))
				langs.add(p.getLanguage());
		return langs;
	}
	
	/**
	 * @param uri: the URI of the entity to search in the Lexicon
	 * @param name: the name of the entity to search in the Lexicon
	 * @return the LexicalMetadata values for that URI, name pair
	 */
	public Vector<LexicalMetadata> getMetadata(String uri, String name)
	{
		return URINames.get(uri,name);
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @return the list of languages declared for the name
	 */
	public Set<String> getNameLanguages(String name)
	{
		Set<String> hits = nameURIs.get(name);
		HashSet<String> langs = new HashSet<String>();
		if(hits == null)
			return langs;
		for(String i : hits)
			for(LexicalMetadata p : URINames.get(i, name))
				langs.add(p.getLanguage());
		return langs;
	}
	
	/**
	 * @return the set of names in the Lexicon
	 */
	public Set<String> getNames()
	{
		return nameURIs.keySet();
	}

	/**
	 * @param uri: the index of the entity to search in the Lexicon
	 * @return the list of names associated with the entity
	 */
	public Set<String> getNames(String uri)
	{
		if(URINames.contains(uri))
			return URINames.keySet(uri);
		return new HashSet<String>();
	}
	
	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @param type: the type to restrict the search
	 * @return the list of names of the given type associated with the entity
	 */
	public Set<String> getNames(String uri, LexicalType type)
	{
		HashSet<String> namesType = new HashSet<String>();
		if(URINames.contains(uri))
			for(String n : URINames.keySet(uri))
				for(LexicalMetadata p : URINames.get(uri,n))
					if(p.getType().equals(type))
						namesType.add(n);
		return namesType;
	}
	
	/**
	 * @param lang: the lang of the names to get from the Lexicon
	 * @return the names with the given language in the Lexicon
	 */
	public Set<String> getNamesWithLanguage(String lang)
	{
		return langNameURIs.keySet(lang);
	}
	
	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @param lang: the lang of the names to get from the Lexicon
	 * @return the names with the given language associated with the entity
	 */
	public Set<String> getNamesWithLanguage(String uri, String lang)
	{
		HashSet<String> namesLang = new HashSet<String>();
		if(URINames.contains(uri))
			for(String n : URINames.keySet(uri))
				for(LexicalMetadata p : URINames.get(uri,n))
					if(p.getLanguage().equals(lang))
						namesLang.add(n);
		return namesLang;
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the sources of the name for that entity
	 */
	public Set<String> getSources(String name, String uri)
	{
		Vector<LexicalMetadata> provs = URINames.get(uri,name);
		HashSet<String> sources = new HashSet<String>();
		if(provs == null)
			return sources;
		for(LexicalMetadata p : provs)
			sources.add(p.getSource());
		return sources;
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the best type of the name for that class
	 */
	public LexicalType getType(String name, String uri)
	{
		LexicalType type = null;
		double weight = 0.0;
		for(LexicalMetadata p : URINames.get(uri, name))
		{
			if(p.getWeight() > weight)
			{
				weight = p.getWeight();
				type = p.getType();
			}
		}
		return type;
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the types of the name for that class
	 */
	public Set<LexicalType> getTypes(String name, String uri)
	{
		Vector<LexicalMetadata> provs = URINames.get(uri, name);
		HashSet<LexicalType> types = new HashSet<LexicalType>();
		if(provs == null)
			return types;
		for(LexicalMetadata p : provs)
			types.add(p.getType());
		return types;
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the best weight of the name for that entity
	 */
	public double getWeight(String name, String uri)
	{
		double weight = 0.0;
		if(URINames.contains(uri,name))
		{
			for(LexicalMetadata p : URINames.get(uri,name))
				if(p.getWeight() > weight)
					weight = p.getWeight();
		}
		return weight;
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @param lang: the language to search in the Lexicon
	 * @return the weight corresponding to the provenance of the name for that class
	 */
	public double getWeight(String name, String uri, String lang)
	{
		if(URINames.contains(uri,name))
		{
			for(LexicalMetadata p : URINames.get(uri,name))
				if(p.getLanguage().equals(lang))
					return p.getWeight();
		}
		return 0.0;
	}

	/**
	 * @param uri: the class to search in the Lexicon
	 * @return whether the class has an external name
	 */
	public boolean hasExternalName(String uri)
	{
		Set<String> entityNames = getNames(uri);
		if(entityNames == null)
			return false;
		for(String n : entityNames)
			if(isExternal(n,uri))
				return true;
		return false;
	}

	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @param source: the source to search in the Lexicon
	 * @return whether the class has an external name
	 */
	public boolean hasNameFromSource(String uri, String source)
	{
		Set<String> entityNames = getNames(uri);
		if(entityNames == null)
			return false;
		for(String n : entityNames)
			if(getSources(n,uri).contains(source))
				return true;
		return false;
	}

	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return whether the type of the name for the entity
	 * is external
	 */
	public boolean isExternal(String name, String uri)
	{
		if(URINames.contains(uri,name))
		{
			Vector<LexicalMetadata> provs = URINames.get(uri, name);
			for(LexicalMetadata p : provs)
				if(!p.isExternal())
					return false;
			return true;
		}
		return false;
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @param lang: the language to search in the Lexicon
	 * @return whether the type of the name for the class
	 * is external
	 */
	public boolean isExternal(String name, String uri, String lang)
	{
		if(URINames.contains(uri,name))
		{
			Vector<LexicalMetadata> provs = URINames.get(uri, name);
			for(LexicalMetadata p : provs)
				if(p.getLanguage().equals(lang) && p.isExternal())
					return true;
		}
		return false;
	}
	
	/**
	 * @param name: the name to search in the Lexicon
	 * @return whether the name is a formula
	 */
	public boolean isFormula(String name)
	{
		//If a name is a formula, it will be recorded as such for all entities, so we can just look at the first
		return URINames.get(nameURIs.get(name).iterator().next(),name).get(0).getType().equals(LexicalType.FORMULA);
	}
	
	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the number of names associated with the entity
	 */
	public int languageNameCount(String lang)
	{
		return langNameURIs.keySet(lang).size();
	}
	
	/**
	 * @return the number of names in the Lexicon
	 */
	public int nameCount()
	{
		return nameURIs.keyCount();
	}
	
	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @return the number of names associated with the entity
	 */
	public int nameCount(String uri)
	{
		return URINames.entryCount(uri);
	}
	
	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @param type: the type to restrict the search
	 * @return the number of names of the given type associated with the entity
	 */
	public int nameCount(String uri, LexicalType type)
	{
		return getNames(uri,type).size();
	}
	
	/**
	 * @param uri: the uri of the entity to search in the Lexicon
	 * @param type: the type to restrict the search
	 * @param lang: the language to restrict the search
	 * @return the number of names with the given type and language
	 * that are associated with the class
	 */
	public int nameCount(String uri, LexicalType type, String lang)
	{
		Set<String> hits = URINames.keySet(uri);
		int count = 0;
		if(hits == null)
			return count;
		for(String n : hits)
			for(LexicalMetadata p : URINames.get(uri,n))
				if(p.getLanguage().equals(lang) && p.getType().equals(type))
					count++;
		return count;
	}
	
	/**
	 * @return the number of entries in the Lexicon
	 */
	public int size()
	{
		return URINames.size();
	}
}