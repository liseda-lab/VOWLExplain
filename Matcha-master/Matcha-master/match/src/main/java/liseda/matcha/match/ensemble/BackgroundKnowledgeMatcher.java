/******************************************************************************
* Matching algorithm that tests all available background knowledge sources,   *
* using either the MediatingMatcher, the WordNetMatcher, or the XRefMatcher,  *
* as appropriate. It combines the alignment obtained with the suitable        *
* background knowledge sources with the direct Lexical alignment.             *
* NOTE: Running this matcher makes running the LexicalMatcher or any of the   *
* Matchers mentioned above redundant.                                         *      
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.ensemble;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.io.ResourceManager;
import liseda.matcha.io.LexiconFileIO;
import liseda.matcha.io.ontology.OntologyReader;
import liseda.matcha.match.AbstractAlignmentGenerator;
import liseda.matcha.match.Matcher;
import liseda.matcha.match.knowledge.MediatingMatcher;
import liseda.matcha.match.knowledge.MediatingXRefMatcher;
import liseda.matcha.match.knowledge.WordNetMatcher;
import liseda.matcha.match.lexical.LexicalMatcher;
import liseda.matcha.ontology.MediatorOntology;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.settings.SelectionType;
import liseda.matcha.settings.Settings;
import liseda.matcha.settings.SizeCategory;
import liseda.matcha.util.MapSorter;

public class BackgroundKnowledgeMatcher extends AbstractAlignmentGenerator implements Matcher
{
	
//Attributes

	protected static final String DESCRIPTION = "Matches classes by testing all available\n" +
											  "sources of background knowledge, and using\n" +
											  "those that have a significant mapping gain\n" +
											  "(with Cross-Reference Matcher, Mediating\n" +
											  "Matcher, and/or WordNet Matcher).";
	protected static final String NAME = "Background Knowledge Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS};
	//The minimum gain threshold
	private final double GAIN_THRESH = 0.02;
	//Whether to use 1-to-1 gain or global gain
	private boolean oneToOne;
	//The list of ontologies available as background knowledge
	private HashSet<String> lexicons;
	private HashMap<String,String> ontologies;
	
//Constructor
	
	public BackgroundKnowledgeMatcher()
	{
		lexicons = ResourceManager.getBKLexicons();
		ontologies = ResourceManager.getBKOntologies();
		oneToOne = !Settings.getInstance().getSelectionType().equals(SelectionType.HYBRID);
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}

//Public Methods
	
	@Override
	public Alignment match(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		if(!checkEntityType(e))
			return new Alignment(o1,o2);
		System.out.println("Running " + NAME);
		long time = System.currentTimeMillis()/1000;
		LexicalMatcher lm = new LexicalMatcher();
		//The baseline alignment
		Alignment base = lm.match(o1, o2, e,thresh);
		//The alignment to return
		//(note that if no background knowledge sources are selected
		//this matcher will return the baseline Lexical alignment)
		Alignment a = new Alignment(o1,o2);
		a.addAll(base);
		//The map of pre-selected lexical alignments and their gains
		HashMap<Alignment,Double> selected = new HashMap<Alignment,Double>();
		//Auxiliary variables
		Alignment temp;
		Double gain;
		
		//First do the WordNet
		SizeCategory sc = Settings.getInstance().getSizeCategory();
		if(sc.equals(SizeCategory.SMALL) || sc.equals(SizeCategory.MEDIUM))
		{
			WordNetMatcher wn = new WordNetMatcher();
			temp = wn.match(o1,o2,e,thresh);
			if(oneToOne)
				gain = temp.gainOneToOne(base);
			else
				gain = temp.gain(base);
			if(gain >= GAIN_THRESH)
				selected.put(temp,gain);
		}		
		for(String s : lexicons)
		{
			System.out.println("Testing " + s);
			try
			{
				Lexicon ml = LexiconFileIO.readLexicon(s);
				MediatingMatcher mm = new MediatingMatcher(ml, (new File(s)).toURI().toString());
				temp = mm.match(o1,o2,e,thresh);
			}
			catch(IOException io)
			{
				System.out.println("Could not open lexicon file: " + s);
				io.printStackTrace();
				continue;
			}
			if(oneToOne)
				gain = temp.gainOneToOne(base);
			else
				gain = temp.gain(base);
			if(gain >= GAIN_THRESH)
				selected.put(temp,gain);

		}
		for(String s : ontologies.keySet())
		{
			try
			{
				long ontoTime = System.currentTimeMillis()/1000;
				MediatorOntology mo;
				if(ontologies.get(s) == null)
					mo = OntologyReader.parseMediatorOntology(s);
				else
					mo = OntologyReader.parseMediatorOntology(s,ontologies.get(s));
				ontoTime = System.currentTimeMillis()/1000 - time;
				System.out.println(mo.getURI() + " loaded in " + ontoTime + " seconds");
				MediatingXRefMatcher x = new MediatingXRefMatcher(mo);
				temp = x.match(o1,o2,e,thresh);
			}
			catch(OWLOntologyCreationException o)
			{
				System.out.println("WARNING: Could not open ontology " + s);
				o.printStackTrace();
				continue;
			}
			if(oneToOne)
				gain = temp.gainOneToOne(base);
			else
				gain = temp.gain(base);
			if(gain >= GAIN_THRESH)
				selected.put(temp,gain);
		}
			
		System.out.println("Sorting and selecting background knowledge sources");
		//Get the set of background knowledge alignments sorted by gain
		Set<Alignment> orderedSelection = MapSorter.sortDescending(selected).keySet();
		//And reevaluate them
		for(Alignment s : orderedSelection)
		{
			if(oneToOne)
				gain = s.gainOneToOne(a);
			else
				gain = s.gain(a);
			if(gain >= GAIN_THRESH)
				a.addAll(s);
		}
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
		return a;
	}
}