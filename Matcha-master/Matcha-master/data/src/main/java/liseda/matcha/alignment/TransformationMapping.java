/******************************************************************************
* An EDOAL mapping that includes transformations expressing constraints on    *
* instances that should match.                                                *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.alignment;

import java.util.Set;

import liseda.matcha.semantics.edoal.Transformation;

public class TransformationMapping extends Mapping
{

//Attributes

	//The direction of the transformation
	private Set<Transformation> transformations;
	
//Constructors
	
	/**
	 * Creates a transformation mapping between entity1 and entity2 with the given similarity
	 * @param entity1: the source ontology expression
	 * @param entity2: the target ontology expression
	 * @param sim: the similarity between the entities
	 * @param r: the mapping relationship between the entities
	 * @param s: the status of the mapping
	 * @param t: the set of transformations
	 */
	public TransformationMapping(String entity1, String entity2, double similarity, MappingRelation r, String provenance, MappingStatus s, Set<Transformation> t)
	{
		super(entity1,entity2,similarity,r,provenance,s);
		transformations = t;
	}
	
	/**
	 * Creates a new transformation mapping that is a copy of m
	 * @param m: the mapping to copy
	 */
	public TransformationMapping(TransformationMapping m)
	{
		this(m.getEntity1(),m.getEntity2(),m.similarity,m.rel,m.getProvenance(),m.getStatus(),m.transformations);
	}
	
//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof TransformationMapping &&
				((TransformationMapping)o).transformations.equals(this.transformations) &&
				super.equals(o);
	}
	
	/**
	 * @return the set of transformations in this mapping
	 */
	public Set<Transformation> getTransformations()
	{
		return transformations;
	}
}