/******************************************************************************
* A filtering algorithm that excludes mappings that are superseded by more    *
* specific mappings, with the same source or the same target class, and a     *
* subclass of the target or of the source class respectively.                 *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.filter.semantic;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.filter.Filterer;
import liseda.matcha.semantics.SemanticMap;

public class ParentMappingFilterer implements Filterer
{
	
//Constructors
	
	/**
	 * Constructs a ParentSelector
	 */
	public ParentMappingFilterer(){}
	

//Public Methods
	
	@Override
	public Alignment filter(Alignment a)
	{
		System.out.println("Performing Selection");

		long time = System.currentTimeMillis()/1000;
		SemanticMap r = SemanticMap.getInstance();
		Alignment out = new Alignment(a.getSourceOntology(),a.getTargetOntology());
		for(Mapping m : a)
		{
			String src = m.getEntity1();
			String tgt = m.getEntity2();
			if(r.isClass(src) && r.isClass(tgt))
			{
				boolean add = true;
				for(Mapping n : a.getSourceMappings(src))
				{
					String t = n.getEntity2();
					if(r.isSubclass(t,tgt) &&
							a.getSimilarity(src, t) >= a.getSimilarity(src, tgt))
					{
						add = false;
						break;
					}
				}
				if(!add)
					continue;
				for(Mapping n : a.getTargetMappings(tgt))
				{
					String s = n.getEntity1();
					if(r.isSubclass(s,src) &&
							a.getSimilarity(s, tgt) >= a.getSimilarity(src, tgt))
					{
						add = false;
						break;
					}
				}
				if(add)
					out.add(m);
			}
		}
		if(out.size() < a.size())
		{
			for(Mapping m : out)
				if(m.getStatus().equals(MappingStatus.FLAGGED))
					m.setStatus(MappingStatus.UNREVISED);
		}
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
		return out;
	}
}