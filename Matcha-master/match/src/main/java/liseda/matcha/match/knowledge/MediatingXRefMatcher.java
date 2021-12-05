/******************************************************************************
* Matches Ontologies by using cross-references between them and a third       *
* mediating Ontology, or using Lexical matches when there are few or no       *
* cross-references.                                                           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.knowledge;

import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.data.Map2MapComparable;
import liseda.matcha.ontology.MediatorOntology;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.ReferenceMap;
import liseda.matcha.semantics.EntityType;

public class MediatingXRefMatcher extends MediatingMatcher
{
	
//Attributes

	protected static final String DESCRIPTION = "Matches entities that are cross-referenced by\n" +
											  "the same entity of a background knowledge\n" +
											  "source, and/or using the Mediating Matcher.";
	protected static final String NAME = "Mediating Cross-Reference Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS};
	//The external ontology's ReferenceMap
	private ReferenceMap rm;
	//The weight used for matching and Lexicon extension
	private final double WEIGHT = 0.95;
	
//Constructors

	/**
	 * Constructs a MediatingXRefMatcher with the given external Ontology
	 * @param x: the external Ontology
	 */
	public MediatingXRefMatcher(MediatorOntology x)
	{
		super(x);
		rm = x.getReferenceMap();
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}

//Public Methods

	@Override
	public void extendLexicons(Ontology o)
	{
		System.out.println("Extending Lexicon with " + NAME + " using " + uri);
		long time = System.currentTimeMillis()/1000;
		Map2MapComparable<String,String,Double> a = match(o,0.0);
		for(String s : a.keySet())
		{
			for(String hit : a.keySet(s))
			{
				Set<String> names = ext.getNames(hit);
				for(String n : names)
				{
					double sim = a.get(s,hit) * ext.getWeight(n, hit);
					o.getLexicon(EntityType.CLASS).add(s, n, "en", TYPE, uri, sim);
				}
			}
		}
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
	}

//Protected Methods
	
	@Override
	protected Alignment hashMatch(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		Alignment maps = new Alignment(o1,o2);
		System.out.println("Using " + uri);
		Map2MapComparable<String,String,Double> a = match(o1,thresh);
		Map2MapComparable<String,String,Double> b = match(o2,thresh);
		//Reverse the target alignment table
		Map2MapComparable<String,String,Double> rev = new Map2MapComparable<String,String,Double>();
		for(String s : b.keySet())
			for(String t : b.keySet(s))
				rev.add(t, s, b.get(s, t));
		for(String s : a.keySet())
		{
			for(String med : a.keySet(s))
			{
				if(!rev.contains(med))
					continue;
				for(String t : rev.keySet(med))
				{
					double similarity = Math.min(a.get(s, med), rev.get(med, t));
					similarity = Math.min(similarity,WEIGHT);
					maps.add(new Mapping(s,t,similarity,MappingRelation.EQUIVALENCE));
				}
			}
		}
		return maps;
	}
	
//Private Methods
	
	private Map2MapComparable<String,String,Double> match(Ontology o, double thresh)
	{
		Map2MapComparable<String,String,Double> maps = new Map2MapComparable<String,String,Double>();
		if(rm != null)
		{
			Set<String> refs = rm.getReferences();
			Set<String> names = o.getLocalNames();
			for(String r : refs)
			{
				if(names.contains(r))
				{
					Set<String> terms = rm.getEntities(r);
					//Penalize cases where multiple terms have the same xref
					//(note that sim = 1 when the xref is unique) 
					double sim = 1.3 - (terms.size() * 0.3);
					if(sim < thresh)
						continue;
					for(String i : terms)
						if(!maps.contains(o.getURI(r), i) || maps.get(o.getURI(r), i) > sim)
							maps.add(o.getURI(r), i, sim);
				}
			}
		}
		//Step 2 - Do a lexical match
		Map2MapComparable<String,String,Double> lex = match(o.getLexicon(EntityType.CLASS),thresh);
		
		//Step 3 - Compare the two
		//If the coverage of the lexical match is at least double
		//the coverage of the xref match (such as when there are
		//few or no xrefs) merge the two
		if(lex.keySet().size() > maps.keySet().size() * 2)
		{
			for(String s : lex.keySet())
			{
				if(maps.contains(s))
					continue;
				for(String t : lex.keySet(s))
					maps.add(s, t, lex.get(s, t));
			}
		}
		return maps;
	}
}