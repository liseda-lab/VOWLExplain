/******************************************************************************
* Filtering algorithm for mappings between properties that checks if their    *
* domains and ranges are compatible.                                          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.filter.semantic;

import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.filter.Filterer;
import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.ClassUnion;

public class DomainAndRangeFilterer implements Filterer
{
	
//Constructors
	
	public DomainAndRangeFilterer(){}
	
//Public Methods
	
	@Override
	public Alignment filter(Alignment a)
	{
		System.out.println("Running Domain & Range Filter");
		long time = System.currentTimeMillis()/1000;
		Alignment out = new Alignment(a.getSourceOntology(),a.getTargetOntology());
		SemanticMap sm = SemanticMap.getInstance();
		for(Mapping m : a)
		{
			if(m.getStatus().equals(MappingStatus.CORRECT))
				continue;
			if(sm.isDataProperty(m.getEntity1()))
			{
				if(classesMatch(a,sm.getDomain(m.getEntity1()),sm.getDomain(m.getEntity2())) && 
						datatypesMatch(sm.getRange(m.getEntity1()),sm.getRange(m.getEntity2())))
					out.add(m);
			}
			else if(sm.isObjectProperty(m.getEntity1()))
			{
				if(classesMatch(a,sm.getDomain(m.getEntity1()),sm.getDomain(m.getEntity2())) || 
						classesMatch(a, sm.getRange(m.getEntity1()),sm.getRange(m.getEntity2())))
					out.add(m);
			}
		}
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
		return out;
	}
	
//Private Methods
	
	//Checks if two ids match (i.e., are either equal, aligned
	//or one is aligned to the parent of the other)
	private boolean classesMatch(Alignment a, String src, String tgt)
	{
		if(src == null || tgt == null || src.equals(tgt) || a.contains(src, tgt))
	    	return true;
		
		SemanticMap sm = SemanticMap.getInstance();
		if(sm.isClass(src) && sm.isClass(tgt))
		{
			Set<String> sParent = SemanticMap.getInstance().getSuperclasses(src,1,false);
			if(sParent.size() == 1)
			{
				String spId = sParent.iterator().next();
				if(a.contains(spId, tgt))
					return true;
			}
			Set<String> tParent= SemanticMap.getInstance().getSuperclasses(tgt,1,false);
			if(tParent.size() == 1)
			{
				String tpId = tParent.iterator().next();
				if(a.contains(src, tpId))
					return true;
			}
		}
		else
		{
			Expression s = sm.getExpression(src);
			Expression t = sm.getExpression(tgt);
			if(s instanceof ClassUnion && t instanceof ClassUnion)
			{
				return classSetsMatch(a, s.getElements(), t.getElements());
			}
		}
		return false;
	}

	//Checks if two sets of classes match (i.e., have Jaccard similarity above 50%)
	private boolean classSetsMatch(Alignment a, Set<String> sIds, Set<String> tIds)
	{
		if(sIds.size() == 0 && tIds.size() == 0)
			return true;
		if(sIds.size() == 0 || tIds.size() == 0)
			return false;
		double matches = 0.0;
		for(String i : sIds)
		{
			for(String j : tIds)
			{
				if(i.equals(j) || a.contains(i, j))
				{
					matches++;
					break;
				}
			}
		}
		matches /= sIds.size()+tIds.size()-matches;
		return (matches > 0.5);
	}

	//Checks if two data property ranges match
	private boolean datatypesMatch(String sRange, String tRange)
	{
		//TODO: Update this when we handle complex data ranges
		return sRange == null || tRange == null || sRange.equals("") || tRange.equals("") || sRange.equals(tRange);
	}	
}