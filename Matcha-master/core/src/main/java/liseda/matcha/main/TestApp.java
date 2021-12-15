package liseda.matcha.main;

import liseda.match.evaluate.SimpleEvaluator;
import liseda.matcha.alignment.Alignment;
import liseda.matcha.io.ResourceManager;
import liseda.matcha.io.alignment.owl.AlignmentIOOWL;
import liseda.matcha.io.alignment.rdf.AlignmentIORDF;
import liseda.matcha.io.json.CreatingJSON;
import liseda.matcha.io.ontology.OntologyReader;
import liseda.matcha.match.lexical.LexicalMatcher;
import liseda.matcha.match.lexical.StringMatcher;
import liseda.matcha.match.structure.NeighborSimilarityMatcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.pipeline.AutomaticMatcher;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.Settings;
import liseda.matcha.settings.StopList;

public class TestApp 
{
	
	String o1;
	
    public static void main(String[] args) throws Exception
    {
    	ResourceManager.configLog4J();
        System.out.println("Starting Matcha");
        String o1 = "C:\\Users\\filip\\OneDrive\\Ambiente de Trabalho\\Tese\\Projetos\\WebVOWL_individual\\WebVOWLindividual\\owl_desired_format.owl";
        String o2 = "C:\\Users\\filip\\OneDrive\\Ambiente de Trabalho\\Tese\\Projetos\\WebVOWL_individual\\WebVOWLindividual\\owl_desired_format.owl";
//      String o3 = "D:/data/FMA.owl";
//    	String a1 = "D:/data/alignment1.owl";
//    	String a2 = "D:/data/alignment2.owl";
        String oToJSONpath = "C:\\Users\\filip\\OneDrive\\Ambiente de Trabalho\\Tese\\Projetos\\WebVOWL_individual\\WebVOWLindividual\\owl_desired_format.owl";
    	String ref = "C:\\Users\\filip\\OneDrive\\Ambiente de Trabalho\\Tese\\Projetos\\WebVOWL_individual\\WebVOWLindividual\\owl_desired_format.owl";
    	Ontology source = OntologyReader.parseInputOntology(o1);
    	Ontology target = OntologyReader.parseInputOntology(o2);
    	Ontology ontologyToJSON = OntologyReader.parseInputOntology(oToJSONpath);
    	Settings set = Settings.getInstance();
    	StopList.init(ResourceManager.getStopSet());
    	set.defaultConfig(source, target);
//    	AutomaticMatcher am = new AutomaticMatcher();
//    	Alignment a = am.match(source, target);
    	StringMatcher sm = new StringMatcher();
    	Alignment a = sm.match(source, target, EntityType.CLASS, 0.6);
    	CreatingJSON test = new CreatingJSON(ontologyToJSON);
    	// chama o test.justdoit, dont let your dreams be dreams
    	Alignment r = new Alignment(source,target);
    	AlignmentIORDF.read(r, ref);
    	SimpleEvaluator se = new SimpleEvaluator();
    	se.evaluate(a,r);
    	System.out.println(se.toString());
    }

}
