/******************************************************************************
* Lists the Language Settings.                                                *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.settings;

import java.util.HashMap;

import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;

public enum LanguageSetting
{
    SINGLE ("Single-Language Ontologies"),
    MULTI ("Multi-Language Ontologies"),
    TRANSLATE ("Different-Language Ontologies");
	    
    String label;
	    
    LanguageSetting(String s)
    {
    	label = s;
    }
	
	/**
	 * 	Computes and returns the language setting of the matching problem
	 *  based on the language overlap between the input ontologies
	 */
	public static LanguageSetting getLanguageSetting(Ontology source, Ontology target, EntityType t)
	{
		HashMap<String,Double> sLangs = new HashMap<String,Double>();
		int sTotal = 0;
		double sMax = 0.0;
		String sLang = "";
		for(String l : source.getLexicon(t).getLanguages())
		{
			if(!l.equals("Formula"))
			{
				double count = source.getLexicon(t).getLanguageCount(l);
				sLangs.put(l, count);
				sTotal += count;
				if(count > sMax)
				{
					sMax = count;
					sLang = l;
				}
			}
		}
		sMax /= sTotal;
		for(String l : sLangs.keySet())
			sLangs.put(l, sLangs.get(l)/sTotal);
		//Do the same for the target ontology
		HashMap<String,Double> tLangs = new HashMap<String,Double>();
		int tTotal = 0;
		double tMax = 0.0;
		String tLang = "";
		for(String l : target.getLexicon(t).getLanguages())
		{
			if(!l.equals("Formula"))
			{
				double count = target.getLexicon(t).getLanguageCount(l);
				tLangs.put(l, count);
				tTotal += count;
				if(count > tMax)
				{
					tMax = count;
					tLang = l;
				}
			}
		}
		tMax /= (1.0*tTotal);
		for(String l : tLangs.keySet())
			tLangs.put(l, tLangs.get(l)/tTotal);

		//If both ontologies have the same main language, setting is single language
		if(sLang.equals(tLang) && sMax > 0.8 && tMax > 0.8)
			return SINGLE;
		//If the main language of each ontology is not present in the other in significant
		//amount, setting is translate
		else if((sLangs.get(tLang) == null || sLangs.get(tLang) < 0.2) &&
				(tLangs.get(sLang) == null || tLangs.get(sLang) < 0.2))
			return TRANSLATE;
		//Otherwise, setting is multi-language
		else
			return MULTI;
	}
    
    public String toString()
    {
    	return label;
	}
}