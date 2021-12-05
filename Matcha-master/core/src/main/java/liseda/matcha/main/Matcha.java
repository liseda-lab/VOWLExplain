/******************************************************************************
* Matching task manager.                                                      *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.UIManager;

import org.apache.log4j.PropertyConfigurator;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.io.ResourceManager;
import liseda.matcha.io.ontology.OntologyReader;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.ParenthesisExtender;
import liseda.matcha.ontology.lexicon.StopWordExtender;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Reasoner;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.Settings;
import liseda.matcha.settings.SizeCategory;

public class Matcha
{

//Attributes
	
	//The ontology and alignment data structures
	private SemanticMap map;
	private Settings set;
	private List<Ontology> onts;
	private List<Alignment> aligns;
	
//Constructors
	
	public Matcha()
	{
		map = SemanticMap.getInstance();
		set = Settings.getInstance();
		ResourceManager.configLog4J();
	}

//Public Methods

	
	public void match(String src, String tgt, String conf) throws OWLOntologyCreationException
	{
		SemanticMap.clear();
		long totalTime = System.currentTimeMillis()/1000;
		long time = System.currentTimeMillis()/1000;
		System.out.println("Loading source ontology");
		Ontology source = OntologyReader.parseInputOntology(src);
		time = System.currentTimeMillis()/1000 - time;
		System.out.println(source.getURI() + " loaded in " + time + " seconds");
		System.out.println("Classes: " + source.count(EntityType.CLASS));
		System.out.println("Individuals: " + source.count(EntityType.INDIVIDUAL));
		System.out.println("Properties: " + (source.count(EntityType.DATA_PROP)+source.count(EntityType.OBJECT_PROP)));
		time = System.currentTimeMillis()/1000;
		System.out.println("Loading target ontology");
		Ontology target = OntologyReader.parseInputOntology(src);
		time = System.currentTimeMillis()/1000 - time;
		System.out.println(target.getURI() + " loaded in " + time + " seconds");
		System.out.println("Classes: " + target.count(EntityType.CLASS));
		System.out.println("Individuals: " + target.count(EntityType.INDIVIDUAL));
		System.out.println("Properties: " + (target.count(EntityType.DATA_PROP)+target.count(EntityType.OBJECT_PROP)));
		System.out.println("Direct Relationships: " + map.relationshipCount());
		System.out.println("Disjoints: " + map.disjointCount());
    	set.defaultConfig(source,target);
    	if(conf != null)
    	{
			time = System.currentTimeMillis()/1000;
			System.out.println("Reading config file");
	    	boolean check = ResourceManager.readConfig(conf);
			time = System.currentTimeMillis()/1000 - time;
			if(!check)
				System.out.println("Configurations read in " + time + " seconds");	
    	}
    	if(set.repair())
    	{
    		time = System.currentTimeMillis()/1000;
			System.out.println("Performing transitive closure of class relations");
			Reasoner.transitiveClosure();
			time = System.currentTimeMillis()/1000 - time;
			System.out.println("Transitive closure finished in " + time + " seconds");	
			System.out.println("Extended Relationships: " + map.relationshipCount());
			System.out.println("Disjoints: " + map.disjointCount());
    	}
    	if(set.matchIndividuals() && map.getSameIndividuals().size() > 0)
    	{
    		time = System.currentTimeMillis()/1000;
			System.out.println("Performing transitive closure of sameAs individuals");
    		Reasoner.sameAsExpansion();
			time = System.currentTimeMillis()/1000 - time;
			System.out.println("Transitive closure finished in " + time + " seconds");	
    	}
		time = System.currentTimeMillis()/1000;
		System.out.println("Performing lexical extension");
    	StopWordExtender sw = new StopWordExtender();
    	sw.extendLexicons(source);
    	sw.extendLexicons(target);
    	ParenthesisExtender p = new ParenthesisExtender();
    	p.extendLexicons(source);
    	p.extendLexicons(target);
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Lexical extension finished in " + time + " seconds");
		if(set.matchAuto())
			
    	System.out.println("Finished!");
	}
}