/******************************************************************************
* Matches individuals in a process (i.e., individuals that are part of a      *
* workflow and thereby organized sequentially).                               *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/

package liseda.matcha.match.ensemble;

import java.util.HashSet;
import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.match.AbstractAlignmentGenerator;
import liseda.matcha.match.Matcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.Settings;
import liseda.matcha.similarity.ISub;
import liseda.matcha.similarity.Similarity;
import liseda.matcha.util.StringParser;


public class ProcessMatcher extends AbstractAlignmentGenerator implements Matcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches individuals in a process (i.e.," +
											  "individuals that are part of a workflow" +
											  "and thereby organized sequentially)" +
											  "through a combination of String matching" +
											  "and similarity flooding";
	protected static final String NAME = "Process Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.INDIVIDUAL};
	private Set<String> stopSet;
	private Ontology source, target;
	
//Constructors
	
	public ProcessMatcher()
	{
		super();
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}
	
//Public Methods
	
	@Override
	public Alignment match(Ontology o1, Ontology o2, EntityType e, double threshold)
	{
		Alignment a = new Alignment(o1,o2);
		source = o1;
		target = o2;
		//For each combination of individuals, do a string and word match
		for(String s : o1.getEntities(e))
		{
			for(String t : o2.getEntities(e))
			{
				double sim = entitySimilarity(s, t, true);
				if(sim > 0)
					a.add(new Mapping(s,t,sim,MappingRelation.EQUIVALENCE));
			}
		}
		a = neighborSimilarity(a);
		double names = o1.getLexicon(e).nameCount()*1.0/o1.count(EntityType.INDIVIDUAL);
		names = Math.min(names, o2.getLexicon(e).nameCount()*1.0/o2.count(EntityType.INDIVIDUAL));
		if(SemanticMap.getInstance().getIndividualConnectivity() < 0.9)
		{
			a = neighborSimilarity(a);
			a = neighborSimilarity(a);
		}
		Alignment b = new Alignment(o1,o2);
		for(Mapping m : a)
		{
			if(Settings.getInstance().isToMatch(m.getEntity1()) && Settings.getInstance().isToMatch(m.getEntity2()) &&
					m.getSimilarity() >= threshold)
				b.add(m);
		}
		return b;
	}

//Private Methods
	
	protected double entitySimilarity(String i1, String i2, boolean useWordNet)
	{
		double sim = 0.0;
		for(String n1 : source.getLexicon(EntityType.INDIVIDUAL).getNames(i1))
			for(String n2 : target.getLexicon(EntityType.INDIVIDUAL).getNames(i2))
				sim = Math.max(sim, nameSimilarity(n1,n2,useWordNet));
		return sim;
	}
	
	protected double nameSimilarity(String n1, String n2, boolean useWordNet)
	{
		//Check if the names are equal
		if(n1.equals(n2))
			return 1.0;
		
		//Since we cannot use string or word similarity on formulas
		//if the names are (non-equal) formulas their similarity is zero
		if(StringParser.isFormula(n1) || StringParser.isFormula(n2))
			return 0.0;

		//Compute the String similarity
		double stringSim = ISub.stringSimilarity(n1,n2);
		//Then the String similarity after removing stop words
		String n1S = n1;
		String n2S = n2;
		for(String s : stopSet)
		{
			n1S = n1S.replace(s, "").trim();
			n2S = n2S.replace(s, "").trim();
		}
		stringSim = Math.max(stringSim, ISub.stringSimilarity(n1S,n2S)*0.95);
		stringSim *= 0.8;
		
		//Compute the Word similarity (ignoring stop words)
		double wordSim = 0.0;
		//Split the source name into words
		String[] sW = n1.split(" ");
		HashSet<String> n1Words = new HashSet<String>();
		for(String s : sW)
			if(!stopSet.contains(s))
				n1Words.add(s);
		String[] tW = n2.split(" ");
		HashSet<String> n2Words = new HashSet<String>();
		for(String s : tW)
			if(!stopSet.contains(s))
				n2Words.add(s);
		wordSim = Similarity.jaccardSimilarity(n1Words, n2Words);
		
		//Return the maximum of the string and word similarity
		return Math.max(stringSim, wordSim);
	}
	
	private Alignment neighborSimilarity(Alignment a)
	{
		Alignment b = new Alignment(a.getSourceOntology(),a.getTargetOntology());
		for(Mapping m : a)
		{
			double maxSim = 0.0;
			HashSet<String> sourceChildren = getChildren((String)m.getEntity1(),false);
			HashSet<String> targetChildren = getChildren((String)m.getEntity2(),false);
			for(String s : sourceChildren)
			{
				for(String t : targetChildren)
				{
					Mapping n = a.get(s, t);
					if(n != null && n.getSimilarity() > maxSim)
						maxSim = n.getSimilarity();
				}
			}
				
			HashSet<String> sourceParents = getParents((String)m.getEntity1(),false);
			HashSet<String> targetParents = getParents((String)m.getEntity2(),false);
			for(String s : sourceParents)
			{
				for(String t : targetParents)
				{
					Mapping n = a.get(s, t);
					if(n != null && n.getSimilarity() > maxSim)
						maxSim = n.getSimilarity();
				}
			}
			b.add(new Mapping(m.getEntity1(),m.getEntity2(),(maxSim * 0.25) + (m.getSimilarity() * 0.75),m.getRelationship()));
		}
		return b;
	}
	
	private HashSet<String> getChildren(String index, boolean recursive)
	{
		HashSet<String> children = new HashSet<String>(SemanticMap.getInstance().getIndividualActiveRelations(index));
		if(recursive)
			for(String c : SemanticMap.getInstance().getIndividualActiveRelations(index))
				for(String rel : SemanticMap.getInstance().getIndividualProperties(index, c))
					for(String cc : SemanticMap.getInstance().getIndividualActiveRelations(c))
						if(SemanticMap.getInstance().getIndividualProperties(c, cc).contains(rel))
							children.add(cc);
		return children;
	}
	
	private HashSet<String> getParents(String index, boolean recursive)
	{
		HashSet<String> parents = new HashSet<String>(SemanticMap.getInstance().getIndividualPassiveRelations(index));
		if(recursive)
			for(String p : SemanticMap.getInstance().getIndividualPassiveRelations(index))
				for(String rel : SemanticMap.getInstance().getIndividualProperties(p, index))
					for(String pp : SemanticMap.getInstance().getIndividualPassiveRelations(p))
						if(SemanticMap.getInstance().getIndividualProperties(pp, p).contains(rel))
							parents.add(pp);
		return parents;
	}
}