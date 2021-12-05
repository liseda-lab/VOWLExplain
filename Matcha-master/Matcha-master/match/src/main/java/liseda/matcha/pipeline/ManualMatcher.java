/******************************************************************************
* Customizable ensemble of matching and filtering algorithms.                 *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.pipeline;

import java.util.Set;
import liseda.matcha.alignment.Alignment;
import liseda.matcha.filter.cardinality.CardinalityFilterer;
import liseda.matcha.filter.cardinality.CoCardinalityFilterer;
import liseda.matcha.filter.logic.Repairer;
import liseda.matcha.filter.quality.ObsoleteFilterer;
import liseda.matcha.match.LWC;
import liseda.matcha.match.ensemble.BackgroundKnowledgeMatcher;
import liseda.matcha.match.lexical.HybridStringMatcher;
import liseda.matcha.match.lexical.LexicalMatcher;
import liseda.matcha.match.lexical.StringMatcher;
import liseda.matcha.match.lexical.WordMatcher;
import liseda.matcha.match.structure.BlockRematcher;
import liseda.matcha.match.structure.NeighborSimilarityMatcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.settings.MatchStep;
import liseda.matcha.settings.NeighborSimilarityStrategy;
import liseda.matcha.settings.SelectionType;
import liseda.matcha.settings.Settings;

public class ManualMatcher implements MatchingPipeline
{

//Constructors	
	
	public ManualMatcher(){}
	
//Public Methods

	public Alignment match(Ontology source, Ontology target)
	{
		//Get the settings
		Settings set = Settings.getInstance();
		double thresh = set.getThreshold();
		boolean hierarchic = set.isHierarchic();
		
		//Initialize the alignment
		Alignment a = new Alignment(source,target);
		Alignment aux;
		for(MatchStep step : set.getMatchSteps())
		{
			if(step.equals(MatchStep.TRANSLATE))
			{
				//TODO: Implement translator
				//aml.translateOntologies();
			}
			else if(step.equals(MatchStep.LEXICAL))
			{
				LexicalMatcher lm = new LexicalMatcher();
				a.addAll(lm.match(source,target,EntityType.CLASS,thresh));
			}
			else if(step.equals(MatchStep.BK))
			{
				BackgroundKnowledgeMatcher bk = new BackgroundKnowledgeMatcher();
				a.addAll(bk.match(source,target,EntityType.CLASS,thresh));
			}
			else if(step.equals(MatchStep.WORD))
			{
				aux = new Alignment(source,target);
				Set<String> lang = source.getLexicon(EntityType.CLASS).getLanguages();
				lang.retainAll(target.getLexicon(EntityType.CLASS).getLanguages());
				for(String l : lang)
				{
					WordMatcher wm = new WordMatcher(l,set.getWordMatchStrategy());
					aux.addAll(wm.match(source,target,EntityType.CLASS,thresh));
				}
				if(hierarchic)
					a.addAllOneToOne(aux);
				else
					a.addAll(aux);
			}
			else if(step.equals(MatchStep.STRING))
			{
				if(set.matchClasses())
				{
					StringMatcher sm = new StringMatcher(set.getStringSimMeasure());
					if(set.primaryStringMatcher())
						aux = sm.match(source,target,EntityType.CLASS,thresh);
					else
						aux = sm.extendAlignment(a,EntityType.CLASS,thresh);
					if(hierarchic)
						a.addAllOneToOne(aux);
					else
						a.addAll(aux);
				}
				if(set.matchProperties())
				{
					HybridStringMatcher pm = new HybridStringMatcher(true);
					aux = pm.match(source,target,EntityType.DATA_PROP,thresh);
					aux.addAll(pm.match(source,target,EntityType.OBJECT_PROP,thresh));
					if(hierarchic)
						a.addAllOneToOne(aux);
					else
						a.addAll(aux);
				}
			}
			else if(step.equals(MatchStep.STRUCT))
			{
				NeighborSimilarityMatcher nsm = new NeighborSimilarityMatcher(
						set.getNeighborSimilarityStrategy(),set.directNeighbors());
				aux = nsm.extendAlignment(a,EntityType.CLASS,thresh);
				if(hierarchic)
					a.addAllOneToOne(aux);
				else
					a.addAll(aux);
			}
			else if(step.equals(MatchStep.OBSOLETE))
			{
				ObsoleteFilterer or = new ObsoleteFilterer();
				a = or.filter(a);
			}
			else if(step.equals(MatchStep.SELECT))
			{
				SelectionType sType = set.getSelectionType();
				if(set.structuralSelection())
				{
					BlockRematcher br = new BlockRematcher();
					Alignment b = br.rematch(a,EntityType.CLASS);
					NeighborSimilarityMatcher nb = new NeighborSimilarityMatcher(
							NeighborSimilarityStrategy.MAXIMUM,true);
					Alignment c = nb.rematch(a,EntityType.CLASS);
					b = LWC.combine(b, c, 0.75);
					b = LWC.combine(a, b, 0.8);
					CardinalityFilterer s = new CardinalityFilterer(sType,thresh-0.05);
					b = s.filter(b);
					CoCardinalityFilterer cs = new CoCardinalityFilterer(sType,thresh,b);
					a = cs.filter(a);
					
				}
				else
				{
					CardinalityFilterer s = new CardinalityFilterer(sType,thresh);
					a = s.filter(a);
				}
			}
			if(step.equals(MatchStep.REPAIR))
			{
				Repairer r = new Repairer();
				a = r.filter(a);
			}
		}
		return a;
	}
}