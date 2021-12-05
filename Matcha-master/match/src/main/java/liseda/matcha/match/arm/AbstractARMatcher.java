/*******************************************************************************
 * Abstract Matcher based on association rule minings that implements methods  *
 * for computing confidence and producing an alignment for a given set of      *
 * association rules.                                                          *
 *                                                                             *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.match.AbstractAlignmentGenerator;
import liseda.matcha.match.Matcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;

public abstract class AbstractARMatcher extends AbstractAlignmentGenerator implements Matcher
{
//Attributes
	
	protected ARMap rules;
	protected final double SUPPORT = 0.01;
	protected int fixedSupport;
	protected Set<String> sharedInd;

//Constructors
	
	protected AbstractARMatcher()
	{
		rules = new ARMap();
	}

//Public methods
	
	@Override
	public Alignment match(Ontology o1, Ontology o2, EntityType e, double thresh) 
	{
		Alignment a = new Alignment(o1,o2);
		if(!checkEntityType(e))
			return a;
		System.out.println("Running " + getName());
		long time = System.currentTimeMillis()/1000;
		computeSupport(o1, o2);
		SemanticMap sm = SemanticMap.getInstance();
		
		for(Expression e1 : rules.getRules().keySet()) 
		{
			for(Expression e2 : rules.getRules().get(e1).keySet()) 
			{
				//Filter by support
				if(rules.getSupport(e1, e2) < fixedSupport)
					continue;
				
				double conf = rules.getConfidence(e1, e2);
				if(conf < thresh)
					continue;
				
				sm.addExpression(e1);
				sm.addExpression(e2);
				
				double reverseConf = rules.getConfidence(e2, e1);
				// If the rule is bidirectional, then it is an equivalence relation
				if(reverseConf >= thresh) 
				{
					conf = Math.sqrt(conf * reverseConf);
					//Ensure the mapping is directional (src->tgt)
					if(o1.containsAll(e1.getElements())) 
						a.add(new Mapping(e1.toString(), e2.toString(), conf, MappingRelation.EQUIVALENCE));
					else
						a.add(new Mapping(e2.toString(), e1.toString(), conf, MappingRelation.EQUIVALENCE));
				}
				// If rule is unidirectional, then it is a subsumption
				else 
				{
					//Ensure the mapping is directional (src->tgt)
					if(o1.containsAll(e1.getElements())) 
						a.add(new Mapping(e1.toString(), e2.toString(), conf, MappingRelation.SUBSUMED_BY));
					else
						a.add(new Mapping(e2.toString(), e1.toString(), conf, MappingRelation.SUBSUMES));
				}
			}
		}
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
		return a;
	}
	
//Protected Methods
	
	/**
	 * Populates the EntitySupport and MappingSupport tables
	 */
	protected abstract void computeSupport(Ontology o1, Ontology o2);
}