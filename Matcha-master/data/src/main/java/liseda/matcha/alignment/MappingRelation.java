/******************************************************************************
* Lists the Mapping relationships.                                            *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             * 
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.alignment;

public enum MappingRelation
{
	EQUIVALENCE	 ("=","Equivalence"),
	HAS_INSTANCE ("HasInstance","HasInstance"),
	INCOMPATIBLE ("%","Incompatible"),
	INSTANCE_OF	 ("InstanceOf","InstanceOf"),
	RELATED		 ("?","Related"),
	SUBSUMED_BY	 ("<","SubsumedBy","&lt;"),
	SUBSUMES	 (">","Subsumes","&gt;");
	
	private String symbol;
	private String label;
	private String safe;
	
	private MappingRelation(String rep, String l)
	{
		symbol = rep;
		label = l;
	}

	private MappingRelation(String rep, String l, String s)
	{
		symbol = rep;
		label = l;
		safe = s;
	}

	public String getLabel()
	{
		return label;
	}
    	
	public MappingRelation inverse()
	{
		if(this.equals(SUBSUMES))
			return SUBSUMED_BY;
		else if(this.equals(SUBSUMED_BY))
			return SUBSUMES;
		else if(this.equals(HAS_INSTANCE))
			return INSTANCE_OF;
		else if(this.equals(INSTANCE_OF))
			return HAS_INSTANCE;
		else
			return this;
	}
    	
	public String toString()
	{
		return symbol;
	}
	
	public String toStringSafe()
	{
		if(safe != null)
			return safe;
		return symbol;
	}
	
	public static MappingRelation parseRelation(String relation)
	{
		for(MappingRelation rel : MappingRelation.values())
			if(relation.equals(rel.symbol) || relation.equals(rel.safe) || relation.equalsIgnoreCase(rel.getLabel()))
				return rel;
		return null;
	}
}