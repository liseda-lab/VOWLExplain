/******************************************************************************
* An algorithm that extends a Lexicon by removing name sections between       *
* parenthesis.                                                                *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.ontology.lexicon;

import java.util.HashSet;
import java.util.Vector;

import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.settings.Settings;

public class ParenthesisExtender implements LexiconExtender
{

//Public Methods
	
	@Override
	public void extendLexicons(Ontology o)
	{
		Settings s = Settings.getInstance();
		if(s.matchClasses())
			extendLexicon(o.getLexicon(EntityType.CLASS));
		if(s.matchIndividuals())
			extendLexicon(o.getLexicon(EntityType.INDIVIDUAL));
		if(s.matchProperties())
		{
			extendLexicon(o.getLexicon(EntityType.DATA_PROP));
			extendLexicon(o.getLexicon(EntityType.OBJECT_PROP));
		}		
	}
	
//Private Methods
	
	private void extendLexicon(Lexicon l)
	{
		Vector<String> nm = new Vector<String>(l.getNames());
		for(String n: nm)
		{
			if(l.isFormula(n) || !n.contains("(") || !n.contains(")"))
				continue;
			String newName;
			double weight = 0.0;
			if(n.matches("\\([^()]+\\)") || n.contains(") or ("))
			{
				newName = n.replaceAll("[()]", "");
				weight = 1.0;
			}
			else if(n.contains(")("))
				continue;
			else
			{
				newName = "";
				char[] chars = n.toCharArray();
				boolean copy = true;
				for(char c : chars)
				{
					if(c == '(')
						copy = false;
					if(copy)
						newName += c;
					if(c == ')')
						copy = true;					
				}
				newName = newName.trim();
				weight = Math.sqrt(newName.length() * 1.0 / n.length());
			}
			if(newName.equals(""))
				continue;
			//Get the classes with the name
			HashSet<String> tr = new HashSet<String>(l.getInternalEntities(n));
			for(String j : tr)
				for(LexicalMetadata p : l.get(n, j))
					l.add(j, newName, p.getLanguage(),
							LexicalType.INTERNAL_SYNONYM, p.getSource(), weight*p.getWeight());
		}
	}
}