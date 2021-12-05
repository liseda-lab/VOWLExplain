/******************************************************************************
* An algorithm that extends a Lexicon by removing stop words.                 *
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
import liseda.matcha.settings.SizeCategory;
import liseda.matcha.settings.StopList;

public class StopWordExtender implements LexiconExtender
{

//Attributes
	
	private final double WEIGHT = 0.98;
	
//Constructors
	
	/**
	 * Constructs a new StopWordExtender for the given EntityType and with
	 * the option to remove all or just leading and trailing stop-words
	 * @param removeAll: whether to remove all or just leading and trailing stop-words
	 */
	public StopWordExtender(){}
	
//Public Methods
	
	@Override
	public void extendLexicons(Ontology o)
	{
		Settings s = Settings.getInstance();
		if(s.matchClasses())
		{
			extendLexicon(o.getLexicon(EntityType.CLASS),false);
			SizeCategory sc = s.getSizeCategory();
			//TODO: test if this is really useful
			if(sc.equals(SizeCategory.LARGE) || sc.equals(SizeCategory.HUGE))
				extendLexicon(o.getLexicon(EntityType.CLASS),true);
		}
		if(s.matchIndividuals())
			extendLexicon(o.getLexicon(EntityType.INDIVIDUAL),true);
		if(s.matchProperties())
		{
			extendLexicon(o.getLexicon(EntityType.DATA_PROP),true);
			extendLexicon(o.getLexicon(EntityType.OBJECT_PROP),true);
		}
	}
	
//Private Methods
	
	private void extendLexicon(Lexicon l, boolean removeAll)
	{
		Vector<String> nm = new Vector<String>(l.getNames());
		for(String n: nm)
		{
			if(l.isFormula(n))
				continue;
			String[] nameWords = n.split(" ");
			String newName = "";
			if(removeAll)
			{
				for(int i = 0; i < nameWords.length; i++)
					if(!StopList.contains(nameWords[i]))
						newName += nameWords[i] + " ";
				newName = newName.trim();
			}
			//Build a synonym by removing all leading and trailing stopWords
			else
			{
				//First find the first word in the name that is not a stopWord
				int start = 0;
				for(int i = 0; i < nameWords.length; i++)
				{
					if(!StopList.contains(nameWords[i]))
					{
						start = i;
						break;
					}
				}
				//Then find the last word in the name that is not a stopWord
				int end = nameWords.length;
				for(int i = nameWords.length - 1; i > 0; i--)
				{
					if(!StopList.contains(nameWords[i]))
					{
						end = i+1;
						break;
					}
				}
				//If the name contains no leading or trailing stopWords proceed to next name
				if(start == 0 && end == nameWords.length)
					continue;
				//Otherwise build the synonym
				for(int i = start; i < end; i++)
					newName += nameWords[i] + " ";
				newName = newName.trim();
			}
			//If the name is empty or unchanged, skip to next name
			if(newName.equals("") || newName.equals(n))
				continue;
			//Otherwise, gGet the entities with the name
			HashSet<String> tr = new HashSet<String>(l.getInternalEntities(n));
			for(String i : tr)
			{
				for(LexicalMetadata p : l.get(n, i))
				{
					double weight = p.getWeight() * WEIGHT;
					l.add(i, newName, p.getLanguage(),
							LexicalType.INTERNAL_SYNONYM, p.getSource(), weight);
				}
			}
		}
	}
}