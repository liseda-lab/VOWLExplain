/******************************************************************************
* Lists OBO-specific vocabulary used in OWL ontologies.                       *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.vocabulary;

public enum OBO
{
    BROAD_SYN		(Namespace.OBO_IN_OWL, "hasBroadSynonym"),
    EXACT_SYN		(Namespace.OBO_IN_OWL, "hasExactSynonym"),
    HAS_PART		(Namespace.OBO, "BFO_0000051"),
    NARROW_SYN		(Namespace.OBO_IN_OWL, "hasNarrowSynonym"),
    PART_OF 		(Namespace.OBO, "BFO_0000050"),
    RELATED_SYN		(Namespace.OBO_IN_OWL, "hasRelatedSynonym"),
    XREF            (Namespace.OBO_IN_OWL, "hasDbXref");
	
    private Namespace prefix;
    private String uri;
    
    OBO(Namespace n, String s)
    {
    	prefix = n;
    	uri = s;
    }
    
    /**
     * @return the OBO URI
     */
    public String toURI()
    {
    	return prefix.uri + uri;
    }
    
    public String toString()
    {
    	return uri;
    }
}