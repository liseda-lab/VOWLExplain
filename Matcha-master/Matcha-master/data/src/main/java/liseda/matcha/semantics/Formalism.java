/******************************************************************************
* Lists the ontology formalisms.                                              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics;

public enum Formalism
{
    OWL		("OWL", "http://www.w3.org/2002/07/owl"),
	RDFS	("RDFS", "http://www.w3.org/2000/01/rdf-schema"),
	SKOS    ("SKOS", "http://www.w3.org/2004/02/skos/core");
    
    String label;
    String uri;
    
    Formalism(String l, String u)
    {
    	label = l;
    	uri = u;
    }
    
    public String getName()
    {
    	return label;
    }
    
    public String getURI()
    {
    	return uri;
    }
    
    public Formalism parse(String form)
    {
    	for(Formalism f : Formalism.values())
    		if(form.equals(f.label) || form.equals(f.uri))
    			return f;
    	return null;
    }
}