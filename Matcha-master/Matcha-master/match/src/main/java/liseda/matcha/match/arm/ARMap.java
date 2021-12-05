/*******************************************************************************
 * Auxiliary data structure for performing association rule mining.            *
 *                                                                             *
 * @authors Daniel Faria, Beatriz Lima                                         *
 ******************************************************************************/
package liseda.matcha.match.arm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.data.Map2Map2Set;
import liseda.matcha.data.Map2Set;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;

public class ARMap
{
	
//Attributes
	
	private static Map2Set<String,String> sharedInd;
	private static String sourceURI;
	private static String targetURI;
	private static int alignCode;
	private Map2Set<String, String> entityIndividuals;
	private Map2Set<String, String> individualEntities;
	private Map2Map2Set<String, String, String> rules;
	private HashMap<String,Expression> expressions;

//Constructors
	
	public ARMap()
	{
		entityIndividuals = new Map2Set<String, String>();
		individualEntities = new Map2Set<String, String>();
		rules = new Map2Map2Set<String, String, String>();
		expressions = new HashMap<String,Expression>();
	}

//Public methods
	
	/**
	 * Adds an entity - individual pair
	 * @param e: the entity to add
	 * @param i: the individual to add
	 */
	public void addEntity(String e, String i) 
	{
		entityIndividuals.add(e, i);
		individualEntities.add(i, e);
	}
	
	/**
	 * Adds an expression
	 * @param e: the expression to add
	 */
	public void addExpression(Expression e)
	{
		expressions.put(e.toString(), e);
	}
	
	/**
	 * Adds or increments a rule
	 * @param e1: the antecedent expression in the rule
	 * @param e2: the consequent expression in the rule
	 * @param i: the individual with the rule
	 */
	public void addRule(String e1, String e2, String i) 
	{
		rules.add(e1, e2, i);
		rules.add(e2, e1, i);
	}
	
	/**
	 * @param e1: the first entity in a rule
	 * @param e2: the second entity in a rule
	 * @return the confidence of the rule
	 */
	public double getConfidence(String e1, String e2) 
	{
		if(rules.contains(e1, e2))
			return (double)rules.get(e1, e2).size() / (double)entityIndividuals.get(e1).size();
		return 0.0;
	}
	
	/**
	 * @param e: the String form of the Expression to get
	 * @return the Expression corresponding to e
	 */
	public Expression getExpression(String e)
	{
		return expressions.get(e);
	}
	
	/**
	 * @return the rule support table
	 */
	public Map2Map2Set<String, String, String> getRules()
	{
		return rules;
	}
	
	/**
	 * Gets the individuals shared by two ontologies
	 */
	public static Map2Set<String,String> getSharedIndividuals(Ontology o1, Ontology o2) 
	{
		//If we have already computed the shared individuals for the ontology pair, we can just return them
		if(sharedInd != null && sourceURI.equals(o1.getURI()) && targetURI.equals(o2.getURI()))
			return sharedInd;
		//Otherwise, we find shared individuals between the two ontologies
		sharedInd = new Map2Set<String,String>();
		//1 - Individuals that exist in both ontologies
		Set<String> shared = new HashSet<String>();
		shared = o1.getEntities(EntityType.INDIVIDUAL);
		shared.retainAll(o2.getEntities(EntityType.INDIVIDUAL));
		for(String s : shared)
			sharedInd.add(s, s);
		//2 - Individuals that are sameAs between the two ontologies
		SemanticMap sm = SemanticMap.getInstance();
		for(String s : sm.getSameIndividuals())
		{
			if(!sm.belongsToOntology(s, o1))
				continue;
			for(String t : sm.getSameIndividuals())
				if(sm.belongsToOntology(t, o2))
					sharedInd.add(s, t);				
		}
		//Update the URIs to avoid repeating this computation
		sourceURI = o1.getURI();
		targetURI = o2.getURI();
		return sharedInd;
	}
	
	/**
	 * Gets the individuals shared by two ontologies and/or mapped through their alignment
	 */
	public static Map2Set<String,String> getSharedIndividuals(Ontology o1, Ontology o2, Alignment a) 
	{
		//Start by getting their internal shared individuals
		getSharedIndividuals(o1,o2);
		//If we already computed the shared individuals using this alignment, we can just return them
		if(alignCode == a.hashCode())
			return sharedInd;
		//Otherwise, we get the mapped individuals from the alignment
		SemanticMap sm = SemanticMap.getInstance();
		for(Mapping m : a)
		{
			if(sm.isIndividual(m.getEntity1()) && sm.isIndividual(m.getEntity2()) && o1.contains(m.getEntity1()) && o2.contains(m.getEntity2()))
				sharedInd.add(m.getEntity1(), m.getEntity2());	
		}
		//Update the hash code to avoi repeating this computation
		alignCode = a.hashCode();
		return sharedInd;
	}
	
	/**
	 * @param e1: the first entity in a rule
	 * @param e2: the second entity in a rule
	 * @return the support of the rule
	 */
	public double getSupport(String e1, String e2) 
	{
		if(rules.contains(e1, e2))
			return rules.get(e1, e2).size();
		return 0.0;
	}
}