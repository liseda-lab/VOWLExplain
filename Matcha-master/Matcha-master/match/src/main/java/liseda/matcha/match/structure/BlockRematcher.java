/******************************************************************************
* Rematches Ontologies by computing the high-level structural similarity      *
* between their classes.                                                      *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.structure;

import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.match.AbstractAlignmentGenerator;
import liseda.matcha.match.Rematcher;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;

public class BlockRematcher extends AbstractAlignmentGenerator implements Rematcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Rematches classes by computing the fraction\n" +
											  "of mappings that fall within the blocks of the\n" +
											  "ontologies (i.e., have the same high-level\n" +
											  "classes.";
	protected static final String NAME = "Block Rematcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS};

//Constructors
	
	public BlockRematcher()
	{
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}
	
//Public Methods
	
	@Override
	public Alignment rematch(Alignment a, EntityType e)
	{
		Alignment maps = new Alignment(a.getSourceOntology(),a.getTargetOntology());
		if(!checkEntityType(e))
			return maps;
		System.out.println("Computing High-Level Structure Overlap");
		long time = System.currentTimeMillis()/1000;
		Alignment high = a.getHighLevelAlignment();
		SemanticMap rMap = SemanticMap.getInstance();
		for(Mapping m : a)
		{
			String sId = m.getEntity1();
			String tId = m.getEntity2();
			if(!rMap.isClass(sId))
			{
				maps.add(m);
				continue;
			}
			Set<String> sourceAncestors = rMap.getHighLevelAncestors(sId);
			Set<String> targetAncestors = rMap.getHighLevelAncestors(tId);
			double maxSim = 0;
			for(String i : sourceAncestors)
			{
				for(String j : targetAncestors)
				{
					double sim = high.getSimilarity(i, j);
					if(sim > maxSim)
						maxSim = sim;
				}
			}
			maps.add(new Mapping(sId,tId,maxSim,m.getRelationship(),NAME,m.getStatus()));
		}
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
		return maps;
	}
}