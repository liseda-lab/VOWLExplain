/******************************************************************************
* An Ontology object.                                                         *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*																			  *
* @author Daniel Faria														  *
******************************************************************************/
package liseda.matcha.ontology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import liseda.matcha.data.Map2Map;
import liseda.matcha.data.Map2Set;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.ontology.lexicon.WordLexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Formalism;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.util.LocalNamer;

public class Ontology
{

//Attributes
	
	//The URI of the ontology
	protected String uri;
	//The location of the ontology
	protected String location;
	//The formalism of the ontology
	protected Formalism form;
	//The entities in the ontology and their types
	protected Map2Set<String,EntityType> entities;
	//The entities grouped by type 
	protected Map2Set<EntityType,String> entitiesByType;
	//Its lexicons
	protected HashMap<EntityType,Lexicon> lex;
	//Its word lexicons
	protected Map2Map<EntityType,String,WordLexicon> wLex;
	//Its attribute map
	protected AttributeMap aMap;
	//Its reference map
	protected ReferenceMap refs;
	//Its map of class names -> uris (necessary for cross-reference matching)
	protected HashMap<String,String> classNames;
	//Its set of deprecated entities
	protected HashSet<String> deprecated;
	
//Constructors

	/**
	 * Constructs an empty ontology with the given URI
	 */
	public Ontology(String uri)
	{
		this.uri = uri;
		//Initialize the data structures
		entities = new Map2Set<String,EntityType>();
		entitiesByType = new Map2Set<EntityType,String>();
		lex = new HashMap<EntityType,Lexicon>();
		wLex = new Map2Map<EntityType,String,WordLexicon>();
		aMap = new AttributeMap();
		refs = new ReferenceMap();
		classNames = new HashMap<String,String>();
		deprecated = new HashSet<String>();
	}

//Public Methods

	/**
	 * Adds an entity to the Ontology
	 * @param index: the index of the entity to add
	 * @param e: the type of the entity to add
	 */
	public void add(String uri, EntityType e)
	{
		entities.add(uri,e);
		entitiesByType.add(e, uri);
		if(e.equals(EntityType.CLASS))
		{
			String name = LocalNamer.getLocalName(uri);
			classNames.put(name, uri);
		}
	}

	/**
	 * @param uri: the uri of the entity or expression to search in the Ontology
	 * @return whether the Ontology contains the entity with the given uri, or all of the
	 * entities listed in the expression 
	 */
	public boolean contains(String uri)
	{
		SemanticMap sm = SemanticMap.getInstance();
		if(sm.isExpression(uri))
		{
			for(String s : sm.getExpression(uri).getElements())
				if(!entities.contains(s))
					return false;
			return true;			
		}
		else
			return entities.contains(uri);
	}

	/**
	 * @param elements: the set of entity uris to search in the Ontology
	 * @return whether the Ontology contains all the entities
	 */
	public boolean containsAll(Set<String> uris)
	{
		for(String e : uris)
			if(!contains(e))
				return false;
		return true;
	}
	
	/**
	 * @param uri: the uri of the entity or expression to search in the Ontology
	 * @return whether the Ontology contains the entity with the given uri, or any of the
	 * entities listed in the expression 
	 */
	public boolean containsPartial(String uri)
	{
		SemanticMap sm = SemanticMap.getInstance();
		if(sm.isExpression(uri))
		{
			for(String s : sm.getExpression(uri).getElements())
				if(entities.contains(s))
					return true;
			return false;			
		}
		else
			return entities.contains(uri);
	}


	/**
	 * @param e: the EntityType to check in the Ontology
	 * @return the number of entities of EntityType e in the Ontology
	 */
	public int count(EntityType e)
	{
		return entitiesByType.entryCount(e);
	}

	/**
	 * @return this Ontology's ValueMap
	 */
	public AttributeMap getAttributeMap()
	{
		return aMap;
	}

	/**
	 * @param e: the EntityType to search in the Ontology
	 * @return the set of entities of the given type in the Ontology
	 */
	public Set<String> getEntities(EntityType e)
	{
		if(entitiesByType.contains(e))
			return entitiesByType.get(e);
		return new HashSet<String>();
	}

	/**
	 * @return the formalism of this Ontology
	 */
	public Formalism getFormalism()
	{
		return form;
	}

	/**
	 * @return the Ontology's Lexicon for a give EntityType (or a new Lexicon if it hadn't been previously instantiated)
	 * @param type: the EntityType of the Lexicon to return
	 */
	public Lexicon getLexicon(EntityType type)
	{
		if(!lex.containsKey(type))
			lex.put(type, new Lexicon());
		return lex.get(type);
	}

	/**
	 * @return the set of class local names in the Ontology
	 */
	public Set<String> getLocalNames()
	{
		return classNames.keySet();
	}

	/**
	 * @return the location of this Ontology
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * @param index: the index of the term/property to get the name
	 * @return the primary name of the term/property with the given index
	 */
	public String getName(String uri)
	{
		if(entities.contains(uri))
		{
			for(EntityType t : entities.get(uri))
				if(lex.containsKey(t))
					return getLexicon(t).getBestName(uri);
		}
		return "";
	}

	/**
	 * @return the Ontology's ReferenceMap
	 */
	public ReferenceMap getReferenceMap()
	{
		return refs;
	}

	/**
	 * @param uri: the uri of the entity in the ontology
	 * @return the entity types of the entity
	 */
	public Set<EntityType> getTypes(String uri)
	{
		if(!entities.contains(uri))
			return new HashSet<EntityType>();
		return entities.get(uri);
	}
	
	/**
	 * @return the Ontology's URI
	 */
	public String getURI()
	{
		return uri;
	}

	/**
	 * @param name: the localName of the class to get from the Ontology
	 * @return the index of the corresponding name in the Ontology
	 */
	public String getURI(String name)
	{
		if(classNames.containsKey(name))
			return classNames.get(name);
		return null;
	}	
	
	/**
	 * Gets the WordLexicon for the given EntityType and language
	 * for this Ontology, or builds one if not yet built.
	 * @param e: the EntityType for which to build the WordLexicon
	 * @param lang: the language of the WordLexicon
	 * @return the WordLexicon of this Ontology
	 */
	public WordLexicon getWordLexicon(EntityType e, String lang)
	{
		if(!wLex.contains(e,lang))
			wLex.add(e, lang, new WordLexicon(lex.get(e),lang));
		return wLex.get(e, lang);
	}

	/**
	 * @param uri: the URI of the entity in the ontology
	 * @return whether the entity is deprecated
	 */
	public boolean isDeprecated(String uri)
	{
		return deprecated.contains(uri);
	}
	
	/**
	 * @param entity: the entity URI to search in the ontology
	 * @return whether the entity is an expression
	 */
	public boolean isExpression(String entity)
	{
		return entities.contains(entity) && (
				entities.get(entity).contains(EntityType.CLASS_EXPRESSION) ||
				entities.get(entity).contains(EntityType.OBJECT_EXPRESSION) ||
				entities.get(entity).contains(EntityType.DATA_EXPRESSION));
	}

	/**
	 * @return the number of Entities in the Ontology
	 */
	public int size()
	{
		return entities.size();
	}
	
	/**
	 * Sets the formalism of the ontology
	 * @param f: the Formalism to set
	 */
	public void setFormalism(Formalism f)
	{
		this.form = f;
	}

	/**
	 * Adds an uri to the list of deprecated entities
	 * @param uri: the uri of the entity to set as deprecated
	 */
	public void setDeprecated(String uri)
	{
		deprecated.add(uri);
	}
	
	/**
	 * Sets the location of the ontology
	 * @param location: the location to set
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}

	/**
	 * Sets the URI of the ontology
	 * @param uri: the URI to set
	 */
	public void setURI(String uri)
	{
		this.uri = uri;
	}
}