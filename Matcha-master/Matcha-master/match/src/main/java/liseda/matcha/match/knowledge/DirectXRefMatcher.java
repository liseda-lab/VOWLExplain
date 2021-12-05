/******************************************************************************
* Matches Ontologies by using cross-references between them.                  *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.knowledge;

import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.match.AbstractHashMatcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.ReferenceMap;
import liseda.matcha.semantics.EntityType;

public class DirectXRefMatcher extends AbstractHashMatcher
{
	
//Attributes

	protected static final String DESCRIPTION = "Matches entities that have the same cross-reference\n" +
											  "or the where one cross-references the other.";
	protected static final String NAME = "Direct Cross-Reference Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS};
	//The weight used for matching and Lexicon extension
	private final double WEIGHT1 = 0.99;
	private final double WEIGHT2 = 0.95;
	
//Constructors

	/**
	 * Constructs a XRefDirectMatcher with the given external Ontology
	 * @param x: the external Ontology
	 */
	public DirectXRefMatcher()
	{
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}

//Protected Methods

	@Override
	protected Alignment hashMatch(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		Alignment maps = new Alignment(o1,o2);
		ReferenceMap sourceRefs = o1.getReferenceMap();
		ReferenceMap targetRefs = o2.getReferenceMap();
				
		//1 - Check for direct references between the ontologies in one direction
		for(String r : sourceRefs.getReferences())
		{
			//Test the reference with no processing
			String t = o2.getURI(r);
			//If not found, test it after truncating the prexif
			if(t == null)
			{
				int index = r.indexOf('_');
				if(index < 0)
					continue;
				t = o2.getURI(r.substring(index+1));
				if(t == null)
					continue;
			}
			for(String s : sourceRefs.getEntities(r))
				maps.add(new Mapping(s,t,WEIGHT1,MappingRelation.EQUIVALENCE));
		}
		//2 - Check for direct references between the ontologies in the opposite direction
		for(String r : targetRefs.getReferences())
		{
			//Test the reference with no processing
			String s = o1.getURI(r);
			//If not found, test it after truncating the prexif
			if(s == null)
			{
				int index = r.indexOf('_');
				if(index < 0)
					continue;
				s = o1.getURI(r.substring(index+1));
				if(s == null)
					continue;
			}
			for(String t : targetRefs.getEntities(r))
				maps.add(new Mapping(s,t,WEIGHT1,MappingRelation.EQUIVALENCE));
		}
		//3 - Check for common references of the ontologies
		//Start by determining the smallest ReferenceMap to minimize computations
		ReferenceMap largest, smallest;
		boolean sourceIsSmallest = true;
		if(sourceRefs.size() < targetRefs.size())
		{
			smallest = sourceRefs;
			largest = targetRefs;
		}
		else
		{
			smallest = targetRefs;
			largest = sourceRefs;
			sourceIsSmallest = false;
		}
		for(String r : smallest.getReferences())
		{
			Set<String> small = smallest.getEntities(r);
			Set<String> large = largest.getEntities(r);
			//We can't match if the reference is not in the other ontology
			//and we don't want to match if the reference is promiscuous
			if(large == null || small.size() > 2 || large.size() > 2)
				continue;
			for(String i : small)
			{
				for(String j : large)
				{
					if(sourceIsSmallest)
						maps.add(new Mapping(i,j,WEIGHT2,MappingRelation.EQUIVALENCE));
					else
						maps.add(new Mapping(j,i,WEIGHT2,MappingRelation.EQUIVALENCE));
				}
			}
		}
		return maps;
	}
}