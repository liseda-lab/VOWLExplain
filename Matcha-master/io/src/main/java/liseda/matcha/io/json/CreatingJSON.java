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
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.LexicalType;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.util.LocalNamer;

public class CreatingJSON {
	
	FileWriter file = new FileWriter("C:\\Users\\filip\\OneDrive\\Ambiente de Trabalho\\Tese\\Projetos\\WebVOWL_individual\\WebVOWLindividual\\Matcha-master\\output.json");
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
	Set<String> datatypeIris;
	Set<String> individualIris;
	Set<String> propertyIris;
	Integer idCounter = 0;
	HashMap<String, Integer> IDMap = new HashMap<String, Integer>();
	
	public CreatingJSON(Ontology ontology) throws OWLOntologyCreationException, IOException {
		writeJSON(ontology);	
	}
	
	
	private void writeJSON (Ontology ontology) throws OWLOntologyCreationException, JsonParseException, JsonMappingException, IOException {
		
//		Get the list of class IRIs
		classIris = ontology.getEntities(EntityType.CLASS);
//		System.out.print(classIris.toString());
//		Go through the class IRIs and create JSON Objects for each class with id and type
//		A map containing a pair of iri and id is also created so we can get the id from the iri
		for (String IRI : classIris) {
			createClasses(ontology, IRI);
		} 
		
//		Go through the class IRIs again, this time to create the classAttributes
		for (String IRI : classIris) {
			createClassAttributes(ontology, IRI);
		}
		
//		Gets the list of datatype IRIs
		datatypeIris = ontology.getEntities(EntityType.DATATYPE);
//		System.out.print(datatypeIris.toString()); //Empty, there are no datatypes...
//		Goes through them and creates JSON Objects for each one with the necessary fields, both for the class and the classAttribute
		for (String IRI : datatypeIris) {
//			System.out.print("Running datatype for loop");
			createDatatypes(ontology, IRI);
			createDatatypeAttributes(ontology, IRI);
			idCounter++;
		} 

		
//		Gets the list of individual IRIs
		individualIris = ontology.getEntities(EntityType.INDIVIDUAL);
//		System.out.print(individualIris.toString());
//		Goes through them and create JSON Objects for each one with the necessary fields, both for the individual and the individualAttribute
		for (String IRI : individualIris) {
			createIndividuals(ontology, IRI);
			createIndividualAttributes(ontology, IRI);
			idCounter++;
		}
		
//		Gets the list of property IRIs
		propertyIris = ontology.getEntities(EntityType.OBJECT_PROP);
//		System.out.print(datatypeIris.toString());
		createProperties(ontology);
		createPropertyAttributes(ontology);
		
		HashMap<String,Object> fileRoot = new HashMap<String,Object>();
		fileRoot.put("class",_class);
		fileRoot.put("classAttribute", classAttribute);
		fileRoot.put("individual", individual);
		fileRoot.put("individualAttribute", individualAttribute);
		JSONObject fileRootJSON = new JSONObject(fileRoot);
		
//		Write the JSON object to a JSON file
		ObjectMapper mapper = new ObjectMapper();
		Object json = mapper.readValue(fileRootJSON.toJSONString(), Object.class);
		String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		
		file.write(indented);
		file.flush();  
        file.close();
	}
	
	private void createDatatypeAttributes(Ontology ontology, String IRI) {
		HashMap<String,Object> datatypeAttributeIterator = new HashMap<String,Object>();
		datatypeAttributeIterator.put("iri", IRI);
		datatypeAttributeIterator.put("baseIri", IRI.substring(0, cutBySymbol(IRI)));
		HashMap<String,Object> datatypeAttributeLabels = new HashMap<String,Object>();
		datatypeAttributeLabels.put("IRI-based", IRI.substring(cutBySymbol(IRI)+1 , IRI.length()));
		datatypeAttributeIterator.put("label", datatypeAttributeLabels);
		datatypeAttributeIterator.put("id", IDMap.get(IRI).toString());
		JSONObject datatypeAttributeIteratorJSON = new JSONObject(datatypeAttributeIterator);
//		Adding the iteration to the array containing all classAttribute objects
		classAttribute.add(datatypeAttributeIteratorJSON);
	}



	private void createDatatypes(Ontology ontology, String IRI) {
//		System.out.print("Creating Datatypes");
		HashMap<String,Object> datatypeIterator = new HashMap<String,Object>();
		datatypeIterator.put("id", idCounter.toString());
		datatypeIterator.put("type", "rdfs:Datatype");
		JSONObject datatypeIteratorJSON = new JSONObject(datatypeIterator);
//		Adding the iteration to the array containing all "class" objects
		_class.add(datatypeIteratorJSON);
		
	}



	private void createPropertyAttributes(Ontology ontology) {
		// TODO Auto-generated method stub
		
	}



	private void createProperties(Ontology ontology) {
		// TODO Auto-generated method stub
		
	}



	private void createClassAttributes(Ontology ontology, String IRI) {
		HashMap<String,Object> classAttributeMap = new HashMap<String,Object>();
		classAttributeMap.put("iri", IRI);
		classAttributeMap.put("baseIri", IRI.substring(0, cutBySymbol(IRI)));
		Set<String> instances = sm.getClassIndividuals(IRI);
		classAttributeMap.put("instances", instances.size());
		
		HashMap<String,String> classAttributeLabels = new HashMap<String,String>();
		Iterator<String> i = ontology.getLexicon(EntityType.CLASS).getNames(IRI, LexicalType.LOCAL_NAME).iterator();
		String localName;
		if (i.hasNext()) {
			localName = i.next();
		}
		else {
			localName = LocalNamer.getLocalName(IRI);
		}
		classAttributeLabels.put("IRI-based", localName);
		Set<String> names = ontology.getLexicon(EntityType.CLASS).getNames(IRI, LexicalType.LABEL);
		for (String name : names) {
			System.out.print(name);
			Set<String> languages = ontology.getLexicon(EntityType.CLASS).getLanguages(name, IRI, LexicalType.LABEL);
			for (String lang : languages ) {
				classAttributeLabels.put(lang, name);
			}
		}
		classAttributeMap.put("label", classAttributeLabels);
		
		Set<String> subclassIRIs = sm.getSubclasses(IRI, false); //returns list of IRIs
		List subclassIDs = new ArrayList<Integer>();
		for (String subclassIRI : subclassIRIs) {
			subclassIDs.add(IDMap.get(subclassIRI).toString());
		}
		classAttributeMap.put("subclasses", subclassIDs);
		Set<String> superclassIRIs = sm.getSuperclasses(IRI, false); //returns list of IRIs
		List superclassIDs = new ArrayList<Integer>();
		for (String superclassIRI : superclassIRIs) {
			superclassIDs.add(IDMap.get(superclassIRI).toString());
		}
		classAttributeMap.put("superclasses", superclassIDs);
		classAttributeMap.put("attributes", "attributes");
		classAttributeMap.put("id", IDMap.get(IRI).toString());
		JSONObject classAttributeMapJSON = new JSONObject(classAttributeMap);
//		Adding the iteration to the array containing all classAttribute objects
		classAttribute.add(classAttributeMapJSON);
		
	}



	private void createIndividualAttributes(Ontology ontology, String IRI) {
		HashMap<String,Object> individualAttributeMap = new HashMap<String,Object>();
		individualAttributeMap.put("iri", IRI);
		individualAttributeMap.put("baseIri", IRI.substring(0,cutBySymbol(IRI)));
		HashMap<String,Object> individualAttributeLabels = new HashMap<String,Object>();
		individualAttributeLabels.put("IRI-based", IRI.substring(cutBySymbol(IRI) +1 , IRI.length()));
		individualAttributeLabels.put("language", ontology.getName(IRI));
		JSONObject individualAttributeLabelsJSON = new JSONObject(individualAttributeLabels);
		individualAttributeMap.put("label", individualAttributeLabelsJSON);
//		String parentIRI = sm.getIndividualClasses ? get InstancedClasses? What's the difference?
//		They return sets of classes, why? Shouldn't it just be one? 
//		There's belongstoClass, but only verifies if so, based on individual and class IRI
		Set<String> parentclassIRIs = sm.getIndividualClasses(IRI);
		List<Integer> parentclassIDs = new ArrayList<Integer>();
		for (String parentclassIRI : parentclassIRIs) {
			parentclassIDs.add(IDMap.get(parentclassIRI));
		}
		individualAttributeMap.put("parentClass", parentclassIDs.toString());
		individualAttributeMap.put("attributes", "attributes");
		individualAttributeMap.put("id", idCounter);
		JSONObject individualAttributeMapJSON = new JSONObject(individualAttributeMap);
//		Adding the iteration to the array containing all classAttribute objects
		individualAttribute.add(individualAttributeMapJSON);
		
	}


	private void createIndividuals(Ontology ontology, String IRI) {
		HashMap<String,Object> individualMap = new HashMap<String,Object>();
		individualMap.put("id", idCounter.toString());
		individualMap.put("type", "owl:NamedIndividual");
		JSONObject individualMapJSON = new JSONObject(individualMap);
//		Adding the iteration to the array containing all class objects
		individual.add(individualMapJSON);
		
	}

	//returns number of symbol position (adjust for base IRI or IRI based label)
	private int cutBySymbol(String IRI) {
		int cardinal = 0;
		int slash = 0;
		int colon = 0;
		if (IRI.contains("#"))  
				cardinal = IRI.lastIndexOf("#");
		if (IRI.contains("/"))  
			slash = IRI.lastIndexOf("/");
		if (IRI.contains(":"))  
			colon = IRI.lastIndexOf(":");
		int a = Math.max(slash, cardinal);
		int b = Math.max(a, colon);
		return b;
	}
	

	private void createClasses(Ontology ontology, String IRI) {
		HashMap<String,Object> classMap = new HashMap<String,Object>();
		classMap.put("id", idCounter.toString());
		classMap.put("type", "owl:Class");
		JSONObject classMapJSON = new JSONObject(classMap);
//		Adding the iteration to the array containing all class objects
		_class.add(classMapJSON);
		IDMap.put(IRI, idCounter);
		idCounter++;
	}

	
}

