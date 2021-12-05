/******************************************************************************
* Lists the Match Steps.                                                      *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.settings;

public enum MatchStep
{
 	TRANSLATE	("Translator"),
   	LEXICAL		("Lexical Matcher"),
   	BK			("Background Knowledge Matcher"),
   	WORD		("Word Matcher"),
   	STRING		("String Matcher"),
   	HYBRID		("Hybrid String Matcher"),
   	STRUCT		("Structural Matcher"),
   	OBSOLETE	("Obsolete Filter"),
   	SELECT		("Cardinality Filter"),
   	REPAIR		("Coherence Filter");
	    	
   	final String value;
    	
   	MatchStep(String s)
   	{
   		value = s;
   	}
	    	
   	public String toString()
   	{
   		return value;
   	}
   	
	public static MatchStep parseStep(String step)
	{
		for(MatchStep s : MatchStep.values())
			if(step.equalsIgnoreCase(s.toString()))
				return s;
		return null;
	}
}