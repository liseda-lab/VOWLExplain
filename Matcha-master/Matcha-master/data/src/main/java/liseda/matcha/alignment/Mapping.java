/******************************************************************************
* A Mapping represents an element in an Alignment.                            *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.alignment;

public class Mapping implements Comparable<Mapping>
{
	
//Attributes

	//The source ontology entity
	protected String entity1;
	//The target ontology entity
	protected String entity2;
	//The similarity between the terms
	protected double similarity;
	//The relationship between the terms
	protected MappingRelation rel;
	//The status of the Mapping
	protected MappingStatus status;
	//The provenance of the Mapping
	protected String provenance;
	
//Constructors
	
	/**
	 * Creates a mapping between two entities with the given similarity and relation
	 * @param entity1: the source ontology entity
	 * @param entity2: the target ontology entity
	 * @param sim: the similarity between the entities
	 * @param r: the mapping relationship between the entities
	 */
	public Mapping(String entity1, String entity2, double sim, MappingRelation r)
	{
		this(entity1,entity2,sim,r,null,MappingStatus.UNREVISED);
	}
	
	/**
	 * Creates a mapping between two entities with the given similarity, relation and provenance
	 * @param entity1: the source ontology entity
	 * @param entity2: the target ontology entity
	 * @param sim: the similarity between the entities
	 * @param r: the mapping relationship between the entities
	 * @param prov: the provenance of the mapping
	 */
	public Mapping(String entity1, String entity2, double sim, MappingRelation r, String prov)
	{
		this(entity1,entity2,sim,r,prov,MappingStatus.UNREVISED);
	}
	
	/**
	 * Creates a mapping between two entities with the given similarity, relation, provenance and status
	 * @param entity1: the source ontology entity
	 * @param entity2: the target ontology entity
	 * @param sim: the similarity between the entities
	 * @param r: the mapping relationship between the entities
	 * @param s: the status of the mapping
	 * @param prov: the provenance of the mapping
	 */
	public Mapping(String entity1, String entity2, double sim, MappingRelation r, String prov, MappingStatus s)
	{
		this.entity1 = entity1;
		this.entity2 = entity2;
		if(sim < 0)
			this.similarity = 0;
		else if(sim > 1)
			this.similarity = 1;
		else
			this.similarity = sim;
		this.similarity = Math.round(this.similarity*1000)/1000.0;
		this.rel = r;
		this.status = s;
		this.provenance = prov;
	}
	
	/**
	 * Creates a new mapping that is a copy of m
	 * @param m: the mapping to copy
	 */
	public Mapping(Mapping m)
	{
		this(m.entity1,m.entity2,m.similarity,m.rel,m.provenance,m.status);
	}

//Public Methods	
	
	/**
	 * Mappings are compared first based on their status, then based on their similarity.
	 * This enables both sorting by status for the GUI and sorting by similarity during
	 * the matching procedure, as all mappings will have UNKNOWN status at that stage.
	 */
	@Override
	public int compareTo(Mapping o)
	{
		if(this.getStatus().equals(o.getStatus()))
		{
			double diff = this.getSimilarity() - o.getSimilarity();
			if(diff < 0)
				return -1;
			if(diff > 0)
				return 1;
			return 0;
		}
		else return this.getStatus().compareTo(o.getStatus());
	}
	
	/**
	 * Two Mappings are equal if they map the same two entities irrespective of the
	 * the similarity or relationship (this enables finding redundant Mappings)
	 */
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Mapping))
			return false;
		Mapping m = (Mapping)o;
		return (this.entity1.equals(m.entity1) && this.entity2.equals(m.entity2));
	}

	/**
	 * @return the source entity in the Mapping
	 */
	public String getEntity1()
	{
		return entity1;
	}

	/**
	 * @return the target entity in the Mapping
	 */
	public String getEntity2()
	{
		return entity2;
	}
	
	/**
	 * @return the provenance of the Mapping
	 */
	public String getProvenance()
	{
		return provenance;
	}
	
	/**
	 * @return the MappingRelation of the Mapping
	 */
	public MappingRelation getRelationship()
	{
		return rel;
	}
	
	/**
	 * @return the similarity of the Mapping
	 */
	public double getSimilarity()
	{
		return similarity;
	}
	
	/**
	 * @return the similarity of the Mapping formatted as percentage
	 */
	public String getSimilarityPercent()
	{
		return (Math.round(similarity*10000) * 1.0 / 100) + "%";
	}
	
	/**
	 * @return the MappingStatus of the Mapping
	 */
	public MappingStatus getStatus()
	{
		return status;
	}
	
	/**
	 * Creates a copy of this mapping with the source and target
	 * entities swapped and with the inverse relationship.
	 * @return the new mapping
	 */
	public Mapping reverse()
	{
		return new Mapping(entity2, entity1, similarity, rel.inverse(), provenance, status);
	}

	/**
	 * Sets the MappingRelation of the Mapping
	 * @param r: the MappingRelation to set
	 */
	public void setRelationship(MappingRelation r)
	{
		this.rel = r;
	}
	
	/**
	 * Sets the similarity of the Mapping
	 * @param sim: the similarity to set
	 */
	public void setSimilarity(double sim)
	{
		if(sim < 0)
			this.similarity = 0;
		else if(sim > 1)
			this.similarity = 1;
		else
			this.similarity = sim;
	}
	
	/**
	 * Sets the MappingStatus of the Mapping
	 * @param s: the MappingStatus to set
	 */
	public void setStatus(MappingStatus s)
	{
		this.status = s;
	}
}