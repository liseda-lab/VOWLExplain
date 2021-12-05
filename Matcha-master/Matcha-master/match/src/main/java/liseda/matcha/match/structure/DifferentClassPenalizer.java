/******************************************************************************
* Reduces the score of mappings between individuals that aren't instances of  *
* the same class (if same ontology) or of matching classes (if different      *
* ontologies).                                                                *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/

package liseda.matcha.match.structure;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.match.Rematcher;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.InstanceMatchingCategory;
import liseda.matcha.settings.Settings;

public class DifferentClassPenalizer implements Rematcher
{

//Constructors
	
	public DifferentClassPenalizer(){}
	
	
//Public Methods
	
	@Override
	public Alignment rematch(Alignment a, EntityType e)
	{
		SemanticMap map = SemanticMap.getInstance();
		InstanceMatchingCategory c = Settings.getInstance().getInstanceMatchingCategory();
		Alignment b = new Alignment(a);
		for(Mapping m : b)
		{
			if((c.equals(InstanceMatchingCategory.DIFFERENT_ONTOLOGIES) &&
					!haveMatchingClasses(m.getEntity1(),m.getEntity2(),a)) ||
					!map.shareClass(m.getEntity1(),m.getEntity2()))
				m.setSimilarity(m.getSimilarity() * 0.9);
		}
		return b;
	}
	
//Private Methods
	
	private static boolean haveMatchingClasses(String source, String target, Alignment a)
	{
		SemanticMap map = SemanticMap.getInstance();
		for(String s : map.getIndividualClasses(source))
			for(String t : map.getIndividualClasses(target))
				if(a.contains(s, t))
					return true;
		return false;
	}
}