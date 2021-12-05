/******************************************************************************
* Flagger that identifies low quality mappings in the Alignment by computing  *
* auxiliary Alignments.                                                       *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.filter.quality;

import java.util.HashMap;
import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.filter.Flagger;
import liseda.matcha.match.lexical.StringMatcher;
import liseda.matcha.match.lexical.WordMatcher;
import liseda.matcha.match.structure.BlockRematcher;
import liseda.matcha.match.structure.NeighborSimilarityMatcher;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.settings.NeighborSimilarityStrategy;
import liseda.matcha.settings.Settings;
import liseda.matcha.settings.SizeCategory;
import liseda.matcha.settings.WordMatchStrategy;

public class QualityFlagger implements Flagger
{
	
//Attributes
	
	private HashMap<String,Alignment> auxAlignments;
	private final double AVERAGE_THRESH = 0.2;
	
//Constructors
	
	public QualityFlagger(){}
	
//Public Methods
	
	@Override
	public void flag(Alignment a) //TODO: Extend this to handle all entity types
	{
		System.out.println("Running Quality Flagger");
		long time = System.currentTimeMillis()/1000;
		Settings set = Settings.getInstance();
		//Get the languages shared by the ontology
		Set<String> languages = a.getSourceOntology().getLexicon(EntityType.CLASS).getLanguages();
		languages.retainAll(a.getTargetOntology().getLexicon(EntityType.CLASS).getLanguages());
		
		//Construct the list of auxiliary (re)matchers and alignments
		auxAlignments = new HashMap<String,Alignment>();
		for(String lang : languages)
		{
			WordMatcher wm = new WordMatcher(lang, WordMatchStrategy.AVERAGE);
			auxAlignments.put("Word Similarity (" + lang + "): ", 
					wm.rematch(a, EntityType.CLASS));
		}
		StringMatcher sm = new StringMatcher();
		auxAlignments.put("String Similarity: ",
				sm.rematch(a, EntityType.CLASS));
		NeighborSimilarityMatcher nm = new NeighborSimilarityMatcher(NeighborSimilarityStrategy.DESCENDANTS,
				!set.getSizeCategory().equals(SizeCategory.SMALL));
		auxAlignments.put("Descendant Similarity: ", 
				nm.rematch(a, EntityType.CLASS));
		nm = new NeighborSimilarityMatcher(NeighborSimilarityStrategy.ANCESTORS,
				!set.getSizeCategory().equals(SizeCategory.SMALL));
		auxAlignments.put("Ancestor Similarity: ",
				nm.rematch(a, EntityType.CLASS));
		if(set.getSizeCategory().equals(SizeCategory.HUGE))
		{
			BlockRematcher br = new BlockRematcher();
			auxAlignments.put("High-Level Similarity: ",
					br.rematch(a, EntityType.CLASS));
		}
		for(Mapping m : a)
		{
			String source = m.getEntity1();
			String target = m.getEntity2();
			double average = m.getSimilarity();
			int support = 0;
			for(String s : auxAlignments.keySet())
			{
				double sim = auxAlignments.get(s).getSimilarity(source, target);
				//High level similarity doesn't count towards the support
				if(!s.equals("High-Level Similarity: ") && sim > 0)
					support++;
				//Ancestor similarity doesn't count towards the average
				if(!s.equals("Ancestor Similarity: "))
					average += sim;
			}
			average /= auxAlignments.size()-1;
			
			if((support < 2 || average < AVERAGE_THRESH) &&
					m.getStatus().equals(MappingStatus.UNREVISED))
				m.setStatus(MappingStatus.FLAGGED);
		}		
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
	}
	
	/**
	 * @param source: the id of the source Ontology Entity
	 * @param target: the id of the target Ontology Entity
	 * @return the average similarity between the Entities
	 */
	public double getAverageSimilarity(String source, String target)
	{
		double average = 0.0;
		for(String s : auxAlignments.keySet())
		{
			//Ancestor similarity doesn't count towards the average
			if(!s.equals("Ancestor Similarity: "))
				average += auxAlignments.get(s).getSimilarity(source, target);
		}
		average /= auxAlignments.size()-1;
		return average;
	}

	/**
	 * @return the labels of the auxiliary Alignments
	 */
	public Set<String> getLabels()
	{
		return auxAlignments.keySet();
	}
	
	/**
	 * @param source: the id of the source Ontology Entity
	 * @param target: the id of the target Ontology Entity
	 * @return the maximum similarity between the Entities
	 */
	public double getMaxSimilarity(Alignment a, String source, String target)
	{
		double max = a.getSimilarity(source, target);
		for(String s : auxAlignments.keySet())
			max = Math.max(max, auxAlignments.get(s).getSimilarity(source, target));
		return max;
	}
	
	/**
	 * @param source: the id of the source Ontology Entity
	 * @param target: the id of the target Ontology Entity
	 * @param matcher: the label of the auxiliary Alignment
	 * @return the similarity between the Entities in the auxiliary Alignment
	 */
	public double getSimilarity(String source, String target, String matcher)
	{
		return auxAlignments.get(matcher).getSimilarity(source, target);
	}
	
	/**
	 * @param source: the id of the source Ontology Entity
	 * @param target: the id of the target Ontology Entity
	 * @param matcher: the label of the auxiliary Alignment
	 * @return the similarity between the Entities in the auxiliary Alignment in percentage
	 */
	public String getSimilarityPercent(String source, String target, String matcher)
	{
		return auxAlignments.get(matcher).getSimilarityPercent(source, target);
	}
	
	/**
	 * @param source: the id of the source Ontology Entity
	 * @param target: the id of the target Ontology Entity
	 * @return the support for a mapping between the Entities
	 */
	public int getSupport(String source, String target)
	{
		int support = 0;
		for(String s : auxAlignments.keySet())
		{
			double sim = auxAlignments.get(s).getSimilarity(source, target);
			//High level similarity doesn't count towards the support
			if(!s.equals("High-Level Similarity: ") && sim > 0)
				support++;
		}
		return support;
	}
}