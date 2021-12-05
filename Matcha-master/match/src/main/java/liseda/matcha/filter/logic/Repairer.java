/******************************************************************************
* A filtering algorithm based on logical coherence.                           *
*                                                                             *
* @author Daniel Faria & Emanuel Santos                                       *
******************************************************************************/
package liseda.matcha.filter.logic;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.filter.Filterer;
import liseda.matcha.filter.Flagger;
import liseda.matcha.logic.RepairMap;

public class Repairer implements Filterer, Flagger
{

//Constructors
	
	/**
	 * Constructs a Repairer for automatic repair
	 */
	public Repairer(){}

//Public Methods
	
	@Override
	public Alignment filter(Alignment a)
	{
		RepairMap rMap = new RepairMap(a);
		if(rMap.isCoherent())
		{
			System.out.println("Alignment is coherent");
			return a;
		}
		System.out.println("Repairing Alignment");
		long time = System.currentTimeMillis()/1000;
		int repairCount = a.size();
		//Loop until no more mappings can be removed
		boolean check = true;
		while(check)
			check = rMap.removeWorstMapping();
		a.removeAllIncorrect();
		System.out.println("Finished Repair in " + 
				(System.currentTimeMillis()/1000-time) + " seconds");
		repairCount -= a.size();
		System.out.println("Removed " + repairCount + " mappings");
		return a;
	}
	
	@Override
	public void flag(Alignment a)
	{
		System.out.println("Running Coherence Flagger");
		long time = System.currentTimeMillis()/1000;
		RepairMap rMap = new RepairMap(a);
		for(Integer i : rMap)
			if(rMap.getMapping(i).getStatus().equals(MappingStatus.UNREVISED))
				rMap.getMapping(i).setStatus(MappingStatus.FLAGGED);
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
	}
}