/******************************************************************************
* Lists the categories of the instance matching problem.                      *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.settings;

public enum InstanceMatchingCategory
{
	SAME_CLASSES			("Same Classes"),
	SAME_ONTOLOGY			("Same Ontology"),
	DIFFERENT_ONTOLOGIES	("Different Ontologies");
	
	private String label;
	
	InstanceMatchingCategory(String l)
	{
		label = l;
	}
	
	public static InstanceMatchingCategory parse(String cat)
	{
		for(InstanceMatchingCategory c : InstanceMatchingCategory.values())
			if(cat.equalsIgnoreCase(c.label))
				return c;
		return null;
	}
	    
    public String toString()
    {
    	return label;
    }
}