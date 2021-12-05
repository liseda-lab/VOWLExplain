/******************************************************************************
* Utility class for parsing lexical annotations and adding them to a Lexicon. *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.ontology;

import liseda.matcha.ontology.lexicon.LexicalType;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.util.StringParser;

public class LexicalParser
{
	public static void addAnnotation(Lexicon l, String uri, LexicalType type, String name, String lang, boolean isProp)
	{
		//If no language is given
		if(lang.equals(""))
		{
			//We add english by default
			if(l.getLanguages().contains("en"))
				lang = "en";
			//Otherwise we add the most popular language listed
			else
				lang = l.getBestLanguage();
		}
		double weight = type.getDefaultWeight();
		if(!lang.equals("en"))
			l.add(uri, StringParser.normalizeFormula(name), lang, type, "", weight);
		else if(name.matches(".*[a-zA-Z].*"))
		{
			if(StringParser.isFormula(name))
				l.add(uri, StringParser.normalizeFormula(name), lang, LexicalType.FORMULA, "", weight);
			else if(isProp)
				l.add(uri, StringParser.normalizeProperty(name), lang, type, "", weight);
			else
				l.add(uri, StringParser.normalizeName(name), lang, type, "", weight);
		}
	}
	
	public static void addLocalName(Lexicon l, String uri, String localName, boolean isProp)
	{
		double weight = LexicalType.LOCAL_NAME.getDefaultWeight();
		//Set the language to English as long as an English lexical annotation exists for the class
		String lang = "en";
		//Otherwise set the language to the most popular language
		if(!l.getEntityLanguages(uri).contains("en"))
			lang = l.getBestLanguage();
		if(!lang.equals("en"))
			l.add(uri, StringParser.normalizeFormula(localName), lang, LexicalType.LOCAL_NAME, "", weight);
		else if(localName.matches(".*[a-zA-Z].*"))
		{
			if(StringParser.isFormula(localName))
				l.add(uri, StringParser.normalizeFormula(localName), lang, LexicalType.FORMULA, "", weight);
			else if(isProp)
				l.add(uri, StringParser.normalizeProperty(localName), lang, LexicalType.LOCAL_NAME, "", weight);
			else
				l.add(uri, StringParser.normalizeName(localName), lang, LexicalType.LOCAL_NAME, "", weight);
		}
	}
}