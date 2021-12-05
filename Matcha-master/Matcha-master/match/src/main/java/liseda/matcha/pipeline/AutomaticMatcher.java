/******************************************************************************
* Automatic matching pipeline for use in OAEI.                                *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.filter.cardinality.CardinalityFilterer;
import liseda.matcha.filter.cardinality.CoCardinalityFilterer;
import liseda.matcha.filter.logic.Repairer;
import liseda.matcha.filter.quality.ObsoleteFilterer;
import liseda.matcha.filter.semantic.DomainAndRangeFilterer;
import liseda.matcha.io.ResourceManager;
import liseda.matcha.io.LexiconFileIO;
import liseda.matcha.io.ontology.OntologyReader;
import liseda.matcha.match.LWC;
import liseda.matcha.match.attribute.Attribute2LexiconMatcher;
import liseda.matcha.match.attribute.AttributeMatcher;
import liseda.matcha.match.attribute.AttributeStringMatcher;
import liseda.matcha.match.ensemble.ProcessMatcher;
import liseda.matcha.match.knowledge.DirectXRefMatcher;
import liseda.matcha.match.knowledge.MediatingMatcher;
import liseda.matcha.match.knowledge.MediatingXRefMatcher;
import liseda.matcha.match.knowledge.MultiWordMatcher;
import liseda.matcha.match.knowledge.WordNetMatcher;
import liseda.matcha.match.lexical.AcronymMatcher;
import liseda.matcha.match.lexical.HybridStringMatcher;
import liseda.matcha.match.lexical.LexicalMatcher;
import liseda.matcha.match.lexical.SpacelessLexicalMatcher;
import liseda.matcha.match.lexical.StringMatcher;
import liseda.matcha.match.lexical.ThesaurusMatcher;
import liseda.matcha.match.lexical.WordMatcher;
import liseda.matcha.match.structure.BlockRematcher;
import liseda.matcha.match.structure.DifferentClassPenalizer;
import liseda.matcha.match.structure.NeighborSimilarityMatcher;
import liseda.matcha.ontology.MediatorOntology;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.InstanceMatchingCategory;
import liseda.matcha.settings.LanguageSetting;
import liseda.matcha.settings.NeighborSimilarityStrategy;
import liseda.matcha.settings.SelectionType;
import liseda.matcha.settings.Settings;
import liseda.matcha.settings.SizeCategory;

public class AutomaticMatcher implements MatchingPipeline
{
	
//Attributes

	//Interaction manager
	//private static InteractionManager im; //TODO: incorporate interactive matching
	private static final double HIGH_GAIN_THRESH = 0.25;
	private static final double MIN_GAIN_THRESH = 0.02;
	private static final double WN_THRESH = 0.1;
	//And their modifiers
//	private static final double INTERACTIVE_MOD = -0.3;
	private static final double PSM_MOD = 0.1;
	//Alignments
	private static Alignment a;
	private static Alignment lex;
	//The Settings
	private static Settings set;
	
//Constructors	
	
	public AutomaticMatcher()
	{
		set = Settings.getInstance();
	}
	
//Public Methods

	public Alignment match(Ontology source, Ontology target)
	{
		//Initialize the alignment
		a = new Alignment(source,target);
		//Match by entity type
		if(set.matchClasses())
			matchClasses(source, target);
		else if(set.matchProperties())
			matchProperties(source, target);
		if(set.matchIndividuals())
			matchIndividuals(source, target);
		return a;
	}
		
//Private Methods

	//Matching procedure for classes (or classes+properties)
	private void matchClasses(Ontology source, Ontology target)
	{
		//If translation is necessary, translate
		LanguageSetting lang = set.getLanguageSetting();
		if(lang.equals(LanguageSetting.TRANSLATE)) //TODO: incorporate translator
		{
			//set.translateOntologies();
    		//lang = LanguageSetting.getLanguageSetting();
		}
		
		if(source.getReferenceMap().size() > 0 || target.getReferenceMap().size() > 0)
		{
			DirectXRefMatcher dx = new DirectXRefMatcher();
			a.addAll(dx.match(source,target,EntityType.CLASS, set.getThreshold()));
		}
		
		LexicalMatcher lm = new LexicalMatcher();
		lex = lm.match(source,target,EntityType.CLASS, set.getThreshold());
		a.addAll(lex);
		SizeCategory size = set.getSizeCategory();
		if(lang.equals(LanguageSetting.SINGLE))
		{
			if(size.equals(SizeCategory.SMALL))
			{
				WordNetMatcher wn = new WordNetMatcher();
				Alignment wordNet = wn.match(source,target,EntityType.CLASS, set.getThreshold());
				//Deciding whether to use it based on its coverage of the input ontologies
				//(as we expect a high gain if the coverage is high given that WordNet will
				//generate numerous synonyms)
				double coverage = Math.min(wordNet.sourceCoverage(),
						wordNet.targetCoverage());
				
				if(coverage >= WN_THRESH)
				{
					System.out.println("WordNet selected");
					a.addAllOneToOne(wordNet);
				}
				else
					System.out.println("WordNet discarded");
			}
			else
			{
				HashMap<String,String> bkOntologies = ResourceManager.getBKOntologies();
				for(String bk : bkOntologies.keySet())
				{
					MediatorOntology mo;
					try
					{
						if(bkOntologies.get(bk) == null)
							mo = OntologyReader.parseMediatorOntology(bk);
						else
							mo = OntologyReader.parseMediatorOntology(bk, bkOntologies.get(bk));
					}
					catch(OWLOntologyCreationException e)
					{
						System.out.println("WARNING: Could not open ontology " + bk);
						System.out.println(e.getMessage());
						continue;
					}
					
					MediatingXRefMatcher xr = new MediatingXRefMatcher(mo);
					Alignment ref = xr.match(source,target,EntityType.CLASS, set.getThreshold());
					double gain = ref.gain(lex);
					//In the case of Ontologies, if the mapping gain is very high, we can
					//use them for Lexical Extension, which will effectively enable Word-
					//and String-Matching with the BK Ontologies' names
					if(gain >= HIGH_GAIN_THRESH)
					{
						System.out.println(bk + " selected for lexical extension");
						xr.extendLexicons(source);
						xr.extendLexicons(target);

						//If that is the case, we must compute a new Lexical alignment
						//after the extension
						a.addAll(lm.match(source,target,EntityType.CLASS, set.getThreshold()));
					}
					//Otherwise, we add the BK alignment as normal
					else if(gain >= MIN_GAIN_THRESH)
					{
						System.out.println(bk + " selected as a mediator");
						a.addAll(ref);
					}
					else
						System.out.println(bk + " discarded");
				}
			}
		}
		Set<String> bkLexicons = ResourceManager.getBKLexicons();
		for(String bk : bkLexicons)
		{
			try
			{
				Lexicon l = LexiconFileIO.readLexicon(bk);
				MediatingMatcher mm = new MediatingMatcher(l, bk);
				Alignment med = mm.match(source,target,EntityType.CLASS, set.getThreshold());
				double gain = med.gain(lex);
				if(gain >= MIN_GAIN_THRESH)
				{
					System.out.println(bk + " selected");
					a.addAll(med);
				}
				else
					System.out.println(bk + " discarded");
			}
			catch(IOException e)
			{
				System.out.println("WARNING: Could not open lexicon " + bk);
				e.printStackTrace();
				continue;						
			}
		}

		if(!size.equals(SizeCategory.HUGE))
		{
			Alignment word = new Alignment(source,target);
			Set<String> languages = source.getLexicon(EntityType.CLASS).getLanguages();
			languages.retainAll(target.getLexicon(EntityType.CLASS).getLanguages());
			for(String l : languages)
			{
				WordMatcher wm = new WordMatcher(l);
				word.addAll(wm.match(source,target,EntityType.CLASS, set.getThreshold()));
			}
			a.addAllOneToOne(word);
		}
		StringMatcher psm = new StringMatcher();
		//If the task is small, we can use the PSM in match mode
		if(size.equals(SizeCategory.SMALL))
		{
			if(lang.equals(LanguageSetting.SINGLE))
			{
				a.addAll(psm.match(source,target,EntityType.CLASS, set.getThreshold() + PSM_MOD));
				MultiWordMatcher mwm = new MultiWordMatcher();
				a.addAllOneToOne(mwm.match(source,target,EntityType.CLASS, set.getThreshold()));
				AcronymMatcher am = new AcronymMatcher();
				a.addAllOneToOne(am.match(source,target,EntityType.CLASS, set.getThreshold()));
			}
			else
				a.addAll(psm.match(source,target,EntityType.CLASS, set.getThreshold()));
		}
		//Otherwise we use it in extendAlignment mode
		else
			a.addAllOneToOne(psm.extendAlignment(a,EntityType.CLASS,set.getThreshold()));

		if(!size.equals(SizeCategory.HUGE))
		{
			SpacelessLexicalMatcher sl = new SpacelessLexicalMatcher();
			a.addAllNonConflicting(sl.match(source,target,EntityType.CLASS, set.getThreshold()));
			double nameRatio = Math.max(1.0*source.getLexicon(EntityType.CLASS).nameCount()/source.count(EntityType.CLASS),
					1.0*target.getLexicon(EntityType.CLASS).nameCount()/target.count(EntityType.CLASS));
			if(nameRatio >= 1.2)
			{
				ThesaurusMatcher tm = new ThesaurusMatcher();
				a.addAllOneToOne(tm.match(source,target,EntityType.CLASS, set.getThreshold()));
			}
		}
		if(size.equals(SizeCategory.SMALL) || size.equals(SizeCategory.MEDIUM))
		{
			NeighborSimilarityMatcher nsm = new NeighborSimilarityMatcher(
					set.getNeighborSimilarityStrategy(),set.directNeighbors());
			a.addAllOneToOne(nsm.extendAlignment(a,EntityType.CLASS,set.getThreshold()));
		}
		if(set.matchProperties())
		{
			HybridStringMatcher pm = new HybridStringMatcher(true);
			a.addAll(pm.match(source,target,EntityType.DATA_PROP, set.getThreshold()));
			a.addAll(pm.match(source,target,EntityType.OBJECT_PROP, set.getThreshold()));
			DomainAndRangeFilterer dr = new DomainAndRangeFilterer();
			a = dr.filter(a);
		}
		SelectionType sType = set.getSelectionType();
		if(size.equals(SizeCategory.HUGE))
		{
			ObsoleteFilterer or = new ObsoleteFilterer();
			a = or.filter(a);
				
			BlockRematcher hl = new BlockRematcher();
			Alignment b = hl.rematch(a,EntityType.CLASS);
			NeighborSimilarityMatcher nb = new NeighborSimilarityMatcher(
					NeighborSimilarityStrategy.MAXIMUM,true);
			Alignment c = nb.rematch(a,EntityType.CLASS);
			b = LWC.combine(b, c, 0.75);
			b = LWC.combine(a, b, 0.8);
			CardinalityFilterer s = new CardinalityFilterer(sType,set.getThreshold()-0.05);
			b = s.filter(b);
			CoCardinalityFilterer cs = new CoCardinalityFilterer(sType,set.getThreshold(),b);
			a = cs.filter(a);
		}
		else
		{
			CardinalityFilterer s = new CardinalityFilterer(sType,set.getThreshold());
			a = s.filter(a);
		}
		if(!size.equals(SizeCategory.HUGE) || a.cardinality() < 1.5)
		{
			Repairer r = new Repairer();
			a = r.filter(a);
		}
	}
	
	//Matching procedure for individuals
	private void matchIndividuals(Ontology source, Ontology target)
	{
		LanguageSetting lang = set.getLanguageSetting();
		SizeCategory size = set.getSizeCategory();
		double connectivity = SemanticMap.getInstance().getIndividualConnectivity();
		double valueCoverage = Math.min(source.getAttributeMap().size()*1.0/source.count(EntityType.INDIVIDUAL),
				target.getAttributeMap().size()*1.0/target.count(EntityType.INDIVIDUAL));
		double nameCoverage = Math.min(source.getLexicon(EntityType.INDIVIDUAL).nameCount()*1.0/source.count(EntityType.INDIVIDUAL),target.getLexicon(EntityType.INDIVIDUAL).nameCount()*1.0/target.count(EntityType.INDIVIDUAL));
		//Lexical problem
		if(nameCoverage > 0.8)
		{
			LexicalMatcher lm = new LexicalMatcher();
			Alignment b = lm.hashMatch(source, target, EntityType.INDIVIDUAL, set.getThreshold());
			a.addAll(b);
			StringMatcher sm = new StringMatcher();
			if(size.equals(SizeCategory.HUGE))
				a.addAll(sm.extendAlignment(a, EntityType.INDIVIDUAL, set.getThreshold()));
			else
			{
				a.addAll(sm.match(source, target, EntityType.INDIVIDUAL, set.getThreshold()));
			}
			for(String l : source.getLexicon(EntityType.INDIVIDUAL).getLanguages())
			{
				WordMatcher wm = new WordMatcher(l);
				if(size.equals(SizeCategory.HUGE))
					a.addAll(wm.extendAlignment(a, EntityType.INDIVIDUAL, set.getThreshold()));
				else
					a.addAll(wm.match(source, target, EntityType.INDIVIDUAL,set.getThreshold()));
			}
			AttributeMatcher vm = new AttributeMatcher();
			b = vm.match(source, target, EntityType.INDIVIDUAL, set.getThreshold());
			if(b.sourceCoverage() > 0.3 && b.targetCoverage() > 0.3)
			{
				a.addAllNonConflicting(b);
				AttributeStringMatcher vsm = new AttributeStringMatcher();
				if(size.equals(SizeCategory.HUGE))
					a.addAll(vsm.extendAlignment(a, EntityType.INDIVIDUAL, set.getThreshold()));
				else
					a.addAll(vsm.match(source, target, EntityType.INDIVIDUAL,set.getThreshold()));				
			}
			if(set.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_ONTOLOGY))
			{
				DifferentClassPenalizer dcp = new DifferentClassPenalizer();
				dcp.rematch(a, EntityType.INDIVIDUAL);
			}
			CardinalityFilterer s = new CardinalityFilterer(SelectionType.PERMISSIVE, set.getThreshold());
			s.filter(a);
		}
		//Process matching problem
		else if(connectivity >= 0.9 || (connectivity >= 0.4 && valueCoverage < 0.2))
		{
			ProcessMatcher pm = new ProcessMatcher();
			a = pm.match(source,target,EntityType.INDIVIDUAL, set.getThreshold());
			if(set.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_ONTOLOGY))
			{	
				DifferentClassPenalizer dcp = new DifferentClassPenalizer();
				a = dcp.rematch(a, EntityType.INDIVIDUAL);
			}
			
			CardinalityFilterer s;
			if(a.cardinality() >= 2.0)
				s = new CardinalityFilterer(SelectionType.HYBRID,set.getThreshold());
			else
				s = new CardinalityFilterer(SelectionType.PERMISSIVE,set.getThreshold());
			a = s.filter(a);
		}
		else
		{
			AttributeMatcher vm = new AttributeMatcher();
			Alignment b = vm.match(source,target,EntityType.INDIVIDUAL, set.getThreshold());
			double cov = Math.min(b.sourceCoverage(),
					b.targetCoverage());
			System.out.println("ValueMatcher coverage : " + cov);
			//ValueMatcher based strategy
			if(cov >= 0.5)
			{
				HybridStringMatcher sm = new HybridStringMatcher(set.getSizeCategory().equals(SizeCategory.SMALL));
				a = sm.match(source,target,EntityType.INDIVIDUAL, set.getThreshold());
				a.addAll(b);
				if(set.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_ONTOLOGY))
				{	
					DifferentClassPenalizer dcp = new DifferentClassPenalizer();
					a = dcp.rematch(a, EntityType.INDIVIDUAL);
				}
				CardinalityFilterer s = new CardinalityFilterer(SelectionType.PERMISSIVE,set.getThreshold());
				a = s.filter(a);
			}
			//Default strategy
			else
			{
				double thresh = 0.2;
				b = vm.match(source,target,EntityType.INDIVIDUAL, thresh);
				HybridStringMatcher sm = new HybridStringMatcher(set.getSizeCategory().equals(SizeCategory.SMALL));
				a = sm.match(source,target,EntityType.INDIVIDUAL, thresh);
				AttributeStringMatcher vsm = new AttributeStringMatcher();
				a.addAll(vsm.match(source,target,EntityType.INDIVIDUAL, thresh));
				Attribute2LexiconMatcher vlm = new Attribute2LexiconMatcher(set.getSizeCategory().equals(SizeCategory.SMALL)); 
				a.addAll(vlm.match(source,target,EntityType.INDIVIDUAL, thresh));
				if(set.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_ONTOLOGY))
				{	
					DifferentClassPenalizer dcp = new DifferentClassPenalizer();
					a = dcp.rematch(a, EntityType.INDIVIDUAL);
				}
				Alignment c = vsm.rematch(a,EntityType.INDIVIDUAL);
				Alignment d = vlm.rematch(a,EntityType.INDIVIDUAL);
				Alignment aux = LWC.combine(c, d, 0.75);
				aux = LWC.combine(aux, b, 0.65);
				aux = LWC.combine(aux, a, 0.8);
				
				CoCardinalityFilterer s = new CoCardinalityFilterer(SelectionType.PERMISSIVE,set.getThreshold(),aux);
				a = s.filter(a);
			}
		}
	}
	
	//Matching procedure for properties only
	private void matchProperties(Ontology source, Ontology target)
	{
		HybridStringMatcher pm = new HybridStringMatcher(true);
		a.addAll(pm.match(source,target,EntityType.DATA_PROP, set.getThreshold()));
		a.addAll(pm.match(source,target,EntityType.OBJECT_PROP, set.getThreshold()));
		DomainAndRangeFilterer f = new DomainAndRangeFilterer();
		a = f.filter(a);
	}
}