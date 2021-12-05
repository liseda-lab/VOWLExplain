/******************************************************************************
* Computes the linear weighted combination between two alignments (simple     *
* mappings only).                                                             *
*                                                                             *
* @author Daniel Faria, Catarina Martins                                      *
******************************************************************************/
package liseda.matcha.match;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;

public class LWC
{

//Constructors
	
	private LWC(){}
	
//Public Methods
	
	/**
	 * Computes the linear weighted combination between two alignments.
	 * @param a: the first alignment to combine
	 * @param b: the second alignment to combine
	 * @param weight: the weight to use in combining the alignment
	 * (similarities from a are multiplied by weight and similarities from b are
	 * multiplied by 1-weight)
	 * @return: the combined alignment
	 */
	public static Alignment combine(Alignment a, Alignment b, double weight)
	{
		Alignment combine = new Alignment(a.getSourceOntology(),a.getTargetOntology());
	
		for(Mapping m: a)
		{
			double similarity = m.getSimilarity()*weight;
			if(b.contains(m))
				similarity += b.getSimilarity(m.getEntity1(),m.getEntity2())*(1-weight);
				combine.add(new Mapping(m.getEntity1(),m.getEntity2(),similarity,m.getRelationship(),m.getProvenance(),m.getStatus()));
		}
		for(Mapping m : b)
		{
			if(!a.contains(m))
			{
				double similarity = m.getSimilarity()*(1-weight);
				combine.add(new Mapping(m.getEntity1(),m.getEntity2(),similarity,m.getRelationship(),m.getProvenance(),m.getStatus()));
			}
		}
		return combine;
	}	
}