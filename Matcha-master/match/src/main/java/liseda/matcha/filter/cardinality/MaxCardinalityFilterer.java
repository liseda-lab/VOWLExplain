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

public class MaxCardinalityFilterer implements Filterer, Flagger
{

//Attributes

	private double thresh;
	private SelectionType type;
	private int card;

//Constructors

	/**
	 * Constructs a Selector with the given similarity threshold
	 * and automatic SelectionType
	 * @param t: the SelectionType
	 * @param thresh: the similarity threshold
	 * @param c: the desired max cardinality
	 */
	public MaxCardinalityFilterer(SelectionType t, double thresh, int c)
	{
		this.type = t;
		this.thresh = thresh;
		this.card = c;
	}

//Public Methods

	@Override
	public Alignment filter(Alignment a)
	{
		System.out.println("Performing Selection");
		long time = System.currentTimeMillis()/1000;
		Alignment out = new Alignment(a.getSourceOntology(),a.getTargetOntology());
		a.sortDescending();
		for(Mapping m : a)
		{
			if(m.getStatus().equals(MappingStatus.CORRECT))
				out.add(m);
			else if(m.getSimilarity() >= thresh && !m.getStatus().equals(MappingStatus.INCORRECT))
			{
				int sourceCard = out.getSourceMappings(m.getEntity1()).size();
				int targetCard = out.getTargetMappings(m.getEntity2()).size();
				if((sourceCard < card && targetCard < card) ||
						(!type.equals(SelectionType.STRICT) && !out.containsBetterMapping(m)) ||
						(type.equals(SelectionType.HYBRID) && m.getSimilarity() > 0.75 && sourceCard <= card && targetCard <= card))
					out.add(m);
			}
		}
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
		return out;
	}

	@Override
	public void flag(Alignment a)
	{
		System.out.println("Running Cardinality Flagger");
		long time = System.currentTimeMillis()/1000;
		for(Mapping m : a)
		{
			if(m.getStatus().equals(MappingStatus.UNREVISED))
			{		
				if(a.getSourceMappings(m.getEntity1()).size() > card ||
						a.getTargetMappings(m.getEntity2()).size() > card)
					m.setStatus(MappingStatus.FLAGGED);
			}
		}
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
	}
}