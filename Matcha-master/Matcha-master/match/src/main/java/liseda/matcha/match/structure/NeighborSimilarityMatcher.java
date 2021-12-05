/******************************************************************************
* Matches Ontologies by computing the neighbor structural similarity between  *
* their classes.                                                              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.structure;

import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.match.AbstractParallelMatcher;
import liseda.matcha.match.lexical.LexicalMatcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.NeighborSimilarityStrategy;

public class NeighborSimilarityMatcher extends AbstractParallelMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches classes that have matching neighbor\n" +
											  "classes (ancestors and/or descendants) by\n" +
											  "propagating neighbor similarity.";
	protected static final String NAME = "Neighbor Similarity Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS};
	//Links to ontology data structures
	private NeighborSimilarityStrategy strat;
	private boolean direct;
	private Alignment a;
	
//Constructors
	
	public NeighborSimilarityMatcher(NeighborSimilarityStrategy s, boolean direct)
	{
		super();
		threads = Runtime.getRuntime().availableProcessors();
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
		strat = s;
		this.direct = direct;
	}
	
//Public Methods
	
	@Override
	public Alignment extendAlignment(Alignment a, EntityType e, double thresh)
	{
		this.a = a;
		return super.extendAlignment(a, e, thresh);
	}
	
	@Override
	//Note that the NeighborSimilarityMatcher cannot be used on its own in match mode,
	//so it is used to extend a LexicalMatcher alignment with the same settings
	public Alignment match(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		LexicalMatcher lm = new LexicalMatcher();
		this.a = lm.match(o1, o2, e, thresh);;
		a.addAll(this.extendAlignment(a, e, thresh));
		return a;
	}
	
	@Override
	public Alignment rematch(Alignment a, EntityType e)
	{
		this.a = a;
		return super.rematch(a, e);
	}
	
//Protected Methods
	
	//Computes the neighbor structural similarity between two terms by
	//checking for mappings between all their ancestors and descendants
	protected Mapping mapTwoEntities(String sId, String tId)
	{
		double parentSim = 0.0;
		double childrenSim = 0.0;
		SemanticMap sm = SemanticMap.getInstance();
		if(!strat.equals(NeighborSimilarityStrategy.DESCENDANTS))
		{
			double parentTotal = 0.0;
			Set<String> sourceParents, targetParents;
			if(direct)
			{
				sourceParents = sm.getSuperclasses(sId,1,false);
				targetParents = sm.getSuperclasses(tId,1,false);
			}
			else
			{
				sourceParents = sm.getSuperclasses(sId,false);
				targetParents = sm.getSuperclasses(tId,false);
			}
			for(String i : sourceParents)
			{
				parentTotal += 0.5 / sm.getDistance(sId,i);
				for(String j : targetParents)
					parentSim += a.getSimilarity(i,j) /
						Math.sqrt(sm.getDistance(sId,i) * sm.getDistance(tId, j));
			}
			for(String i : targetParents)
				parentTotal += 0.5 / sm.getDistance(tId,i);
			parentSim /= parentTotal;
		}
		if(!strat.equals(NeighborSimilarityStrategy.ANCESTORS))
		{
			double childrenTotal = 0.0;
			Set<String> sourceChildren, targetChildren;
			if(direct)
			{
				sourceChildren = sm.getSubclasses(sId,1,false);
				targetChildren = sm.getSubclasses(tId,1,false);
			}
			else
			{
				sourceChildren = sm.getSubclasses(sId,false);
				targetChildren = sm.getSubclasses(tId,false);
			}			
			for(String i : sourceChildren)
			{
				childrenTotal += 0.5 / sm.getDistance(i,sId);
				for(String j : targetChildren)
					childrenSim += a.getSimilarity(i,j) /
						Math.sqrt(sm.getDistance(i,sId) * sm.getDistance(j,tId));
			}
			for(String i : targetChildren)
				childrenTotal += 0.5 / sm.getDistance(i,tId);
			childrenSim /= childrenTotal;
		}
		double sim;
		if(strat.equals(NeighborSimilarityStrategy.ANCESTORS))
			sim = parentSim;
		else if(strat.equals(NeighborSimilarityStrategy.DESCENDANTS))
			sim = childrenSim;
		else if(strat.equals(NeighborSimilarityStrategy.MINIMUM))
			sim = Math.min(parentSim,childrenSim);
		else if(strat.equals(NeighborSimilarityStrategy.MAXIMUM))
			sim = Math.max(parentSim,childrenSim);
		else
			sim = (parentSim + childrenSim)*0.5;
		complete++;
		if(showProgress)
			printProgress();
		return new Mapping(sId, tId, sim, MappingRelation.EQUIVALENCE);
	}
}