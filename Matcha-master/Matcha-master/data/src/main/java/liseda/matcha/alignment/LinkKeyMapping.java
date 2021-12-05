/******************************************************************************
* An EDOAL mapping that includes linkkeys, and thus declares the conditions   *
* under which instances of the two mapped classes can be considered equal.    *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.alignment;

import liseda.matcha.semantics.edoal.LinkKey;

public class LinkKeyMapping extends Mapping
{

//Attributes
	
	private LinkKey l;
	
//Constructors

	/**
	 * Creates a mapping between entity1 and entity2 with the given similarity
	 * @param entity1: the source ontology expression
	 * @param entity2: the target ontology expression
	 * @param sim: the similarity between the entities
	 * @param r: the mapping relationship between the entities
	 * @param s: the status of the mapping
	 * @param l: the link keys between properties of instances of the class expressions
	 */
	public LinkKeyMapping(String entity1, String entity2, double similarity, MappingRelation r, String provenance, MappingStatus s, LinkKey l)
	{
		super(entity1,entity2,similarity,r);
		this.l = l;
	}
	
	/**
	 * Creates a new mapping that is a copy of m
	 * @param m: the mapping to copy
	 */
	public LinkKeyMapping(LinkKeyMapping m)
	{
		this(m.getEntity1(),m.getEntity2(),m.similarity,m.rel,m.getProvenance(),m.getStatus(),m.l);
		this.status = m.status;
	}

//Public Methods

	@Override
	public boolean equals(Object o)
	{
		return o instanceof LinkKeyMapping &&
				((LinkKeyMapping)o).l.equals(this.l) &&
				super.equals(o);
	}
	
	/**
	 * @return the LinkKey of this mapping
	 */
	public LinkKey getLinkKey()
	{
		return l;
	}
}