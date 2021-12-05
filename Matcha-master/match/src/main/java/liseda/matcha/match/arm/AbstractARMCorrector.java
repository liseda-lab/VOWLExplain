/*******************************************************************************
 * Abstract Corrector based on association rule minings that refines           *
 * subsumption mappings by fining more precise (complex) equivalence mappings  *
 * for the subsumed entity.                                                    *
 *                                                                             *
 * @authors Beatriz Lima, Daniel Faria                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.match.AbstractAlignmentGenerator;
import liseda.matcha.match.Corrector;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.SemanticMap;

public abstract class AbstractARMCorrector extends AbstractAlignmentGenerator implements Corrector
{
	
//Attributes

	protected ARMap rules;

//Constructor
	
	public AbstractARMCorrector(){}	

//Public methods
	
	@Override
	public Alignment correctAlignment(Alignment a, EntityType e)
	{
		if(!checkEntityType(e))
			return a;
		System.out.println("Running " + getName());
		long time = System.currentTimeMillis()/1000;
		Alignment out = new Alignment(a.getSourceOntology(),a.getTargetOntology());
		for(Mapping m: a)
		{
			if(!SemanticMap.getInstance().getTypes(m.getEntity1()).contains(e) || 
					!(m.getRelationship().equals(MappingRelation.SUBSUMED_BY) ||
					m.getRelationship().equals(MappingRelation.SUBSUMES)))
				out.add(m);
			else
			{
				computeSupport(m,a.getSourceOntology(),a.getTargetOntology());
				Alignment candidates = generateRules(m,a.getSourceOntology(),a.getTargetOntology());
				if(candidates.isEmpty())
					out.add(m);
				else
					out.add(filter(candidates));
			}			
		}
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
		return out;
	}
	
//Protected Methods
	
	/**
	 * Populates the EntitySupport and MappingSupport tables for a given mapping
	 */
	protected abstract void computeSupport(Mapping m, Ontology o1, Ontology o2);

	/**
	 * Filter candidate mappings
	 */
	protected abstract Mapping filter(Alignment candidates);

	protected Alignment generateRules(Mapping m, Ontology o1, Ontology o2) 
	{
		Alignment mappings = new Alignment(o1,o2);
		SemanticMap sm = SemanticMap.getInstance();
		for(Expression e1 : rules.getRules().keySet()) 
		{
			for(Expression e2 : rules.getRules().keySet(e1)) 
			{
				double conf = rules.getConfidence(e1, e2);
				double revConf = rules.getConfidence(e1, e2);
				if(conf >= m.getSimilarity() && revConf >= m.getSimilarity())
				{
					sm.addExpression(e1);
					sm.addExpression(e2);
					if(o1.containsAll(e1.getElements()) && o2.containsAll(e2.getElements()))
						mappings.add(new Mapping(e1.toString(), e2.toString(), Math.sqrt(conf*revConf), MappingRelation.EQUIVALENCE));
					else if(o2.containsAll(e1.getElements()) && o1.containsAll(e2.getElements()))
						mappings.add(new Mapping(e2.toString(), e1.toString(), Math.sqrt(conf*revConf), MappingRelation.EQUIVALENCE));
				}	
			}
		}
		return mappings;
	}
}