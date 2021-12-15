/******************************************************************************
* Class for creating a JSON file with vowl like syntax from an OWL file       *
*                                                                             *
*                                                                             *
* @author Filipa Serrano                                                      *
******************************************************************************/
package liseda.matcha.io.json;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;

public class CreatingJSON {
	
	FileWriter file = new FileWriter("C:\\Users\\filip\\OneDrive\\Ambiente de Trabalho\\Tese\\Projetos\\WebVOWL_individual\\WebVOWLindividual\\Matcha-master\\output.json");
	JSONObject fileRoot = new JSONObject();
	JSONArray _class = new JSONArray();
	JSONArray classAttribute = new JSONArray();
	JSONArray datatype = new JSONArray();
	JSONArray datatypeAttribute = new JSONArray();
	JSONArray individual = new JSONArray();
	JSONArray individualAttribute = new JSONArray();
	JSONArray property = new JSONArray();
	JSONArray propertyAttribute = new JSONArray();
	SemanticMap sm = SemanticMap.getInstance();
	Set<String> classIris;
	Set<String> individualIris;
	Integer idCounter = 0;
	HashMap<String, Integer> IDMap = new HashMap<String, Integer>();
	
	public CreatingJSON(Ontology ontology) throws OWLOntologyCreationException, IOException {
		
//		Get the list of class IRIs
		classIris = ontology.getEntities(EntityType.CLASS);
//		Go through the class IRIs and create JSON Objects for each class with id and type
//		A map containing a pair of iri and id is also created so we can get the id from the iri
		for (String IRI : classIris) {
			createClasses(ontology, IRI);
		} 
		
//		Go through the class IRIs again, this time to create the classAttributes
		for (String IRI : classIris) {
			createClassAttributes(ontology, IRI);
		}
		

//		Get the list of individual IRIs
		individualIris = ontology.getEntities(EntityType.INDIVIDUAL);
//		Then go through them and create JSON Objects for each one with the necessary fields, both for the individual and the individualAttribute
		for (String IRI : individualIris) {
			createIndividuals(ontology, IRI);
			createIndividualAttributes(ontology, IRI);
		}
		
		createProperties(ontology);
		createPropertyAttributes(ontology);
		
		fileRoot.put("classes",_class);
		fileRoot.put("classAttribute", classAttribute);
		fileRoot.put("individual", individual);
		fileRoot.put("individualAttribute", individualAttribute);
		
//		Write the JSON object to a JSON file
		ObjectMapper mapper = new ObjectMapper();
		Object json = mapper.readValue(fileRoot.toJSONString(), Object.class);
		String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		
		file.write(indented);
		file.flush();  
        file.close();
	
	}
	
	
	
	private void createPropertyAttributes(Ontology ontology) {
		// TODO Auto-generated method stub
		
	}



	private void createProperties(Ontology ontology) {
		// TODO Auto-generated method stub
		
	}



	private void createClassAttributes(Ontology ontology, String IRI) {
		JSONObject classAttributeIterator = new JSONObject();
		classAttributeIterator.put("iri", IRI);
		classAttributeIterator.put("baseIri", IRI.substring(0, IRI.lastIndexOf("/")-1));
		Set<String> instances = sm.getClassIndividuals(IRI);
		classAttributeIterator.put("instances", instances.size());
		JSONObject classAttributeLabels = new JSONObject();
		classAttributeLabels.put("IRI-based", IRI.substring(IRI.lastIndexOf("/")+1 , IRI.length()));
//		TODO
		classAttributeLabels.put("language", ontology.getName(IRI));
		classAttributeIterator.put("label", classAttributeLabels);
		Set<String> subclassIRIs = sm.getSubclasses(IRI, false); //returns list of IRIs
		List subclassIDs = new ArrayList<Integer>();
		for (String subclassIRI : subclassIRIs) {
			subclassIDs.add(IDMap.get(subclassIRI).toString());
		}
		classAttributeIterator.put("subclasses", subclassIDs);
		Set<String> superclassIRIs = sm.getSuperclasses(IRI, false); //returns list of IRIs
		List superclassIDs = new ArrayList<Integer>();
		for (String superclassIRI : superclassIRIs) {
			superclassIDs.add(IDMap.get(superclassIRI).toString());
		}
		classAttributeIterator.put("superclasses", superclassIDs);
		classAttributeIterator.put("attributes", "attributes");
		classAttributeIterator.put("id", IDMap.get(IRI).toString());
//		Adding the iteration to the array containing all classAttribute objects
		classAttribute.add(classAttributeIterator);
		
	}



	private void createIndividualAttributes(Ontology ontology, String IRI) {
		JSONObject individualAttributeIterator = new JSONObject();
		individualAttributeIterator.put("iri", IRI);
		individualAttributeIterator.put("baseIri", IRI.substring(0, IRI.lastIndexOf("/")-1));
		JSONObject individualAttributeLabels = new JSONObject();
		individualAttributeLabels.put("IRI-based", IRI.substring(IRI.lastIndexOf("/") +1 , IRI.length()));
		individualAttributeLabels.put("language", ontology.getName(IRI));
		individualAttributeIterator.put("label", individualAttributeLabels);
//		String parentIRI = sm.getIndividualClasses ? get InstancedClasses? What's the difference?
//		They return sets of classes, why? Shouldn't it just be one? 
//		There's belongstoClass, but only verifies if so, based on individual and class IRI
		Set<String> parentclassIRIs = sm.getIndividualClasses(IRI);
		List parentclassIDs = new ArrayList<Integer>();
		for (String parentclassIRI : parentclassIRIs) {
			parentclassIDs.add(IDMap.get(parentclassIRI).toString());
		}
		individualAttributeIterator.put("parent-class", parentclassIDs);
		individualAttributeIterator.put("attributes", "attributes");
		individualAttributeIterator.put("id", idCounter);
//		Adding the iteration to the array containing all classAttribute objects
		individualAttribute.add(individualAttributeIterator);
		
	}



	private void createIndividuals(Ontology ontology, String IRI) {
		JSONObject individualIterator = new JSONObject();
		individualIterator.put("id", idCounter.toString());
		//TODO
		individualIterator.put("type", "owl:NamedIndividual");
//		Adding the iteration to the array containing all class objects
		individual.add(individualIterator);
		idCounter++;
		
	}



	private void createClasses(Ontology ontology, String IRI) {
		
		JSONObject classIterator = new JSONObject();
		classIterator.put("id", idCounter.toString());
		//TODO
		classIterator.put("type", "getType");
//		Adding the iteration to the array containing all class objects
		_class.add(classIterator);
		IDMap.put(IRI, idCounter);
		idCounter++; //Deixar aqui ou pôr fora?
	}

	
}


