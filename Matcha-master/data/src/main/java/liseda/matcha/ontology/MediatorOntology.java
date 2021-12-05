/******************************************************************************
* An Ontology that is to be used as a mediator in a class matching task.      *
* MediatorOntologies have only a class Lexicon plus a ReferenceMap.           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.ontology;

import liseda.matcha.ontology.lexicon.Lexicon;


public class MediatorOntology
{

//Attributes
	
	//The URI of the ontology
	private String uri;
	//Its lexicon
	private Lexicon lex;
	//Its map of cross-references
	private ReferenceMap refs;
	
	
//Constructors

	/**
	 * Constructs an empty ontology with the given URI
	 */
	public MediatorOntology(String uri)
	{
		this.uri = uri;
		lex = new Lexicon();
		refs = new ReferenceMap();
	}
	
//Public Methods

	/**
	 * Closes the Ontology 
	 */
	public void close()
	{
		uri = null;
		lex = null;
		refs = null;
	}
	
	/**
	 * @return the Lexicon of the Ontology
	 */
	public Lexicon getLexicon()
	{
		return lex;
	}
	
	/**
	 * @return the ReferenceMap of the Ontology
	 */
	public ReferenceMap getReferenceMap()
	{
		return refs;
	}
	
	/**
	 * @return the URI of the Ontology
	 */
	public String getURI()
	{
		return uri;
	}
	
	/**
	 * Sets the URI of the ontology to the given value
	 * @param uri: the URI to set in the ontology
	 */
	public void setURI(String uri)
	{
		this.uri = uri;
	}
}