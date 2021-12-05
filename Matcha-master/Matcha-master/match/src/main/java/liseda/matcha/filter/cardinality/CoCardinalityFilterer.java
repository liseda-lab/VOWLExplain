/******************************************************************************
* A filtering algorithm based on cardinality that uses an auxiliary alignment *
* to rank mappings.                                                           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.filter.cardinality;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.filter.Filterer;
import liseda.matcha.settings.SelectionType;

public class CoCardinalityFilterer implements Filterer
{
	
//Attributes
	
	private double thresh;
	private SelectionType type;
	private Alignment aux;
	
//Constructors
	
	/**
	 * Constructs a CoSelector with the given similarity threshold
	 * and SelectionType, and using the given auxiliary Alignment
	 * as the basis for selection
	 * @param thresh: the similarity threshold
	 * @param type: the SelectionType
	 * @param aux: the auxiliary Alignment
	 */
	public CoCardinalityFilterer(SelectionType type, double thresh, Alignment aux)
	{
		this.thresh = thresh;
		this.type = type;
		this.aux = aux;
	}

//Public Methods
	
	@Override
	public Alignment filter(Alignment a)
	{
		System.out.println("Performing Selection");
		long time = System.currentTimeMillis()/1000;
		Alignment out = new Alignment(a.getSourceOntology(),a.getTargetOntology());
		aux.sortDescending();
		//Then perform selection based on it
		for(Mapping m : aux)
		{
			Mapping n = a.get(m.getEntity1(), m.getEntity2());
			if(n == null)
				continue;
			if(n.getStatus().equals(MappingStatus.CORRECT))
				out.add(n);
			else if(n.getSimilarity() < thresh || n.getStatus().equals(MappingStatus.INCORRECT))
				continue;
			if((type.equals(SelectionType.STRICT) && !out.containsConflict(n)) ||
					(type.equals(SelectionType.PERMISSIVE) && !aux.containsBetterMapping(m)) ||
					(type.equals(SelectionType.HYBRID) && ((n.getSimilarity() > 0.75 && 
					out.cardinality(n.getEntity1()) < 2 && out.cardinality(n.getEntity2()) < 2) ||
					!out.containsBetterMapping(n))))
				out.add(n);
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