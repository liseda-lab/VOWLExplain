/******************************************************************************
* A filtering algorithm based on cardinality.                                 *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.filter.cardinality;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.filter.Filterer;
import liseda.matcha.filter.Flagger;
import liseda.matcha.settings.SelectionType;

public class CardinalityFilterer implements Filterer, Flagger
{
	
//Attributes
	
	private double thresh;
	private SelectionType type;
	
//Constructors
	
	/**
	 * Constructs a Selector with the given similarity threshold and SelectionType
	 * @param thresh: the similarity threshold
	 * @param type: the SelectionType
	 */
	public CardinalityFilterer(SelectionType type, double thresh)
	{
		this.type = type;
		this.thresh = thresh;
	}

//Public Methods
	
	@Override
	public Alignment filter(Alignment a)
	{
		System.out.println("Performing Selection");
		long time = System.currentTimeMillis()/1000;
		//The alignment to store selected mappings
		Alignment out = new Alignment(a.getSourceOntology(),a.getTargetOntology());
		//Sort the active alignment
		a.sortDescending();
		//Then select Mappings in ranking order (by similarity)
		for(Mapping m : a)
		{
			//If the Mapping is CORRECT, select it, regardless of anything else
			if(m.getStatus().equals(MappingStatus.CORRECT))
				out.add(m);
			//If it is INCORRECT or below the similarity threshold, discard it
			else if(m.getSimilarity() < thresh || m.getStatus().equals(MappingStatus.INCORRECT))
				continue;
			//Otherwise, add it if it obeys the rules for the chosen SelectionType:
					//In STRICT selection no conflicts are allowed
			else if((type.equals(SelectionType.STRICT) && !out.containsConflict(m)) ||
					//In PERMISSIVE selection only conflicts of equal similarity are allowed
					(type.equals(SelectionType.PERMISSIVE) && !out.containsBetterMapping(m)) ||
					//And in HYBRID selection a cardinality of 2 is allowed above 0.75 similarity
					(type.equals(SelectionType.HYBRID) && ((m.getSimilarity() > 0.75 &&
					out.cardinality(m.getEntity1()) < 2 && out.cardinality(m.getEntity2()) < 2) ||
					//And PERMISSIVE selection is employed below this limit
					!out.containsBetterMapping(m))))
				out.add(m);
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
	
	@Override
	public void flag(Alignment a)
	{
		System.out.println("Running Cardinality Flagger");
		long time = System.currentTimeMillis()/1000;
		Alignment b = (Alignment)a;
		for(Mapping m : b)
			if(b.containsConflict(m) && m.getStatus().equals(MappingStatus.UNREVISED))
				m.setStatus(MappingStatus.FLAGGED);
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
	}
}