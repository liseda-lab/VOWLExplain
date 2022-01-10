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

import javax.naming.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.LexicalType;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.ObjectAllValues;
import liseda.matcha.semantics.owl.ObjectSomeValues;
import liseda.matcha.util.LocalNamer;

public class CreatingJSON {
	
	FileWriter file = new FileWriter("C:\\Users\\filip\\OneDrive\\Ambiente de Trabalho\\Tese\\Projetos\\WebVOWL_individual\\WebVOWLindividual\\Matcha-master\\output.json");
	ArrayList<Object> _class = new ArrayList<Object>();
	ArrayList<Object> classAttribute = new ArrayList<Object>();
	ArrayList<Object> individual = new ArrayList<Object>();
	ArrayList<Object> individualAttribute = new ArrayList<Object>();
	ArrayList<Object> property = new ArrayList<Object>();
	ArrayList<Object> propertyAttribute = new ArrayList<Object>();
	SemanticMap sm = SemanticMap.getInstance();
	Set<String> classIris;
	Set<String> literalIris;
	Set<String> datatypeIris;
	Set<String> individualIris;
	Set<String> objPropertyIris;
	Set<String> dataPropertyIris;
	Set<String> classExpressions;
	Set<String> dataExpressions;
	Set<String> objectExpressions;
	Integer idCounter = 0;
	HashMap<String, Integer> IDMap = new HashMap<String, Integer>();
	
	public CreatingJSON(Ontology ontology) throws OWLOntologyCreationException, IOException {
		writeJSON(ontology);	
	}
	
	
	private void writeJSON (Ontology ontology) throws OWLOntologyCreationException, JsonParseException, JsonMappingException, IOException {
		
//		Get list of class IRIs
		classIris = ontology.getEntities(EntityType.CLASS); //Works
		System.out.println("Number of classes:");
		System.out.println(classIris.size());
//		Go through the class IRIs and create a Map for each class with its id and type
		for (String IRI : classIris) {
			createClass(IRI);
		} 
//		Go through the class IRIs again, this time to create the classAttributes
//		This has to be done after finishing the first cycle to guarantee we already have ids for all classes
		for (String IRI : classIris) {
			createClassAttribute(ontology, IRI);
		}
		
//		Go through the class IRIs and create subclassOf property for each set of class, superclass
		for (String IRI : classIris) {
			Set<String> superclassIRIs = sm.getSuperclasses(IRI, false); //returns list of superclass IRIs
			for (String superclassIRI : superclassIRIs) {
				createSubclass(IRI);
				createSubclassAttribute(IRI, superclassIRI);
				idCounter++;
			}	
		}

//		Get list of Literal IRIs
		literalIris = ontology.getEntities(EntityType.LITERAL); //Empty
		System.out.println("Literals:");
		System.out.println(literalIris.toString());
		
//		Get list of datatype IRIs (string, datetime, etc)
		datatypeIris = ontology.getEntities(EntityType.DATATYPE); //Empty
		System.out.println("Datatype:");
		System.out.println(datatypeIris.toString());
		
		for (String IRI : datatypeIris) {
			createDatatype(ontology, IRI);
			createDatatypeAttribute(ontology, IRI);
			idCounter++;
		} 

//		Get list of individual IRIs
		individualIris = ontology.getEntities(EntityType.INDIVIDUAL); //Works
//		Create individuals and individual properties
		for (String IRI : individualIris) {
			createIndividual(IRI);
			createIndividualAttribute(ontology, IRI);
			idCounter++;
		}
//		Create rdf:type properties
		for (String IRI : individualIris) {
			createType(IRI);
			createTypeAttributes(IRI);
			idCounter++;
		}
		
		
//		Not sure what to do with them, since it returns properties already included in someValuesFrom (part_of)
		objPropertyIris = ontology.getEntities(EntityType.OBJECT_PROP); //Works
		for (String IRI : objPropertyIris) {
			System.out.println("Object Property:");
			System.out.println(IRI);
			createObjectProperty(IRI); 
		}
		for (String IRI : objPropertyIris) {
			createObjectPropertyAttribute(ontology, IRI);
		}
		
//		Get list of datatypeProperty IRIs
		dataPropertyIris = ontology.getEntities(EntityType.DATA_PROP); // Works
		for (String IRI : dataPropertyIris) {
			createDatatypeProperty(ontology, IRI);
		}
		for (String IRI : dataPropertyIris) {
			createDatatypePropertyAttribute(ontology, IRI);
		}
		
//		Get Class Expressions - this method does not get anything (empty list)
		classExpressions = ontology.getEntities(EntityType.CLASS_EXPRESSION);
		System.out.println("Class expression");
		System.out.println(classExpressions.toString());
		
//		Different approach! Is working. Can this be considered Daniel's approach?
//		Is not working for our created owl file because of Patient getExpressions?
		for (String IRI : classIris) {
			System.out.println(IRI);
			Set<String> classExpressions = sm.getClassExpressions(IRI);
			System.out.println(classExpressions.toString());
			for (String stringExpression : classExpressions) {
				Expression expression = sm.getExpression(stringExpression);
//				System.out.println(expression);
				if(expression instanceof ObjectSomeValues && checkExpression(expression)) {
					createObjectSomeValues();
					createObjectSomeValuesProperty(IRI, expression);
					idCounter ++;
				} else if (expression instanceof ObjectAllValues && checkExpression(expression)) {
					createObjectAllValues();
					createObjectSomeValuesProperty(IRI, expression); //Reusing method from some values, since the processing is the same
					idCounter ++;
				} //Acrescentar aqui unionOf e intersectionOf e cardinality restrictions ?
			}
		}
		
//		Get Data Expressions
		dataExpressions = ontology.getEntities(EntityType.DATA_EXPRESSION); //Empty, same as class_expression, have to it a different way
		System.out.println("Data expression");
		System.out.println(dataExpressions.toString());
		
//		Get Object Property Expressions
		objectExpressions = ontology.getEntities(EntityType.OBJECT_EXPRESSION); //Empty, same as class_expression, have to it a different way
		System.out.println("Object expression");
		System.out.println(objectExpressions.toString());

		
//		Join all the Maps in a single Map with the JSON like structure for the file
		HashMap<String,Object> fileRoot = new HashMap<String,Object>();
		fileRoot.put("class",_class);
		fileRoot.put("classAttribute", classAttribute);
		fileRoot.put("individual", individual);
		fileRoot.put("individualAttribute", individualAttribute);
		fileRoot.put("property", property);
		fileRoot.put("propertyAttribute", propertyAttribute);
		JSONObject fileRootJSON = new JSONObject(fileRoot);
		
//		Write the JSON object to a JSON file
		ObjectMapper mapper = new ObjectMapper();
		Object json = mapper.readValue(fileRootJSON.toJSONString(), Object.class);
		String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		
		file.write(indented);
		file.flush();  
        file.close();
	}
	
	


	


	private boolean checkExpression(Expression expression) {
		List<Expression> components = expression.getComponents();
		Expression range = components.get(1);
		if (range.getEntityType() == EntityType.CLASS) {
			return true;
		}
		return false;
	}
	
	
	private void createClass(String IRI) {
//		Create Map to store the values to then convert to JSON Object
		HashMap<String,Object> classMap = new HashMap<String,Object>();
//		Store ID
		classMap.put("id", idCounter.toString());
//		Store type
		classMap.put("type", "owl:Class");
//		Convert Map into JSON Object
		JSONObject classMapJSON = new JSONObject(classMap);
//		Add the JSON object to the array containing all class objects
		_class.add(classMapJSON);
//		Add to the IDMap the IRI and ID pair to be able to access one from the other
		IDMap.put(IRI, idCounter);
		idCounter++;
	}
	
	private void createClassAttribute(Ontology ontology, String IRI) {
//		Create Map to store the values to then convert to JSON Object
		HashMap<String,Object> classAttributeMap = new HashMap<String,Object>();
//		Get IRI
		classAttributeMap.put("iri", IRI);
//		Get Base IRI
		classAttributeMap.put("baseIri", IRI.substring(0, cutBySymbol(IRI)));
//		Get number of instances
		Set<String> instances = sm.getClassIndividuals(IRI);
		classAttributeMap.put("instances", instances.size());
//		Get subclasses
		Set<String> subclassIRIs = sm.getSubclasses(IRI, false); //returns list of IRIs
		List<String> subclassIDs = new ArrayList<String>();
		for (String subclassIRI : subclassIRIs) {
			subclassIDs.add(IDMap.get(subclassIRI).toString());
		}
		classAttributeMap.put("subclasses", subclassIDs);
//		Get superclasses
		Set<String> superclassIRIs = sm.getSuperclasses(IRI, false); //returns list of IRIs
		List<String> superclassIDs = new ArrayList<String>();
		for (String superclassIRI : superclassIRIs) {
			superclassIDs.add(IDMap.get(superclassIRI).toString());
		}
		classAttributeMap.put("superclasses", superclassIDs);
//		Get labels
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
			Set<String> languages = ontology.getLexicon(EntityType.CLASS).getLanguages(name, IRI, LexicalType.LABEL);
			for (String lang : languages ) {
				classAttributeLabels.put(lang, name);
			}
		}
		classAttributeMap.put("label", classAttributeLabels);
//		Get attributes
		classAttributeMap.put("attributes", "attributes");
//		Get annotations
		HashMap<String,String> classAttributeAnnotations = new HashMap<String,String>();
		Set<String> synonyms = ontology.getLexicon(EntityType.CLASS).getNames(IRI, LexicalType.EXACT_SYNONYM);
		for (String name : synonyms) {
			Set<String> languages = ontology.getLexicon(EntityType.CLASS).getLanguages(name, IRI, LexicalType.EXACT_SYNONYM);
			for (String lang : languages ) {
				classAttributeAnnotations.put(lang, name);
			}
		}
		classAttributeMap.put("annotations", classAttributeAnnotations);
//		Get id
		classAttributeMap.put("id", IDMap.get(IRI).toString());
//		Create JSON object from the map
		JSONObject classAttributeMapJSON = new JSONObject(classAttributeMap);
//		Adding the JSON object to the array containing all classAttribute objects
		classAttribute.add(classAttributeMapJSON);
	}
	
	
//	Not used yet
	private void createDatatype(Ontology ontology, String IRI) {
		HashMap<String,Object> datatypeIterator = new HashMap<String,Object>();
//		Add ID
		datatypeIterator.put("id", idCounter.toString());
//		Add type
		datatypeIterator.put("type", "rdfs:Datatype");
		JSONObject datatypeIteratorJSON = new JSONObject(datatypeIterator);
//		Add the iteration to the array containing all "class" like objects
		_class.add(datatypeIteratorJSON);
		IDMap.put(IRI, idCounter);
	}
//	Not used yet
	private void createDatatypeAttribute(Ontology ontology, String IRI) {
		HashMap<String,Object> datatypeAttributeIterator = new HashMap<String,Object>();
		datatypeAttributeIterator.put("iri", IRI);
		datatypeAttributeIterator.put("baseIri", IRI.substring(0, cutBySymbol(IRI)));
		HashMap<String,Object> datatypeAttributeLabels = new HashMap<String,Object>();
		datatypeAttributeLabels.put("IRI-based", IRI.substring(cutBySymbol(IRI)+1 , IRI.length()));
		datatypeAttributeIterator.put("label", datatypeAttributeLabels);
		datatypeAttributeIterator.put("id", IDMap.get(IRI).toString());
		JSONObject datatypeAttributeIteratorJSON = new JSONObject(datatypeAttributeIterator);
//		Adding JSON object to array containing all "classAttribute" objects
		classAttribute.add(datatypeAttributeIteratorJSON);
	}
	
	
	private void createDatatypeProperty(Ontology ontology, String IRI) {
		HashMap<String,Object> propertyMap = new HashMap<String,Object>();
//		Add ID
		propertyMap.put("id", idCounter.toString());
//		Add type
		propertyMap.put("type", "owl:datatypeProperty");
		JSONObject propertyMapJSON = new JSONObject(propertyMap);
//		Add JSON object to array containing all "property" objects
		property.add(propertyMapJSON);
		IDMap.put(IRI, idCounter);
		idCounter++;
	}
	
	private void createDatatypePropertyAttribute(Ontology ontology, String IRI) {
		HashMap<String,Object> dataPropertyAttributeMap = new HashMap<String,Object>();
//		Get IRI
		dataPropertyAttributeMap.put("iri", IRI);
//		Get baseIRI
		dataPropertyAttributeMap.put("baseIri", IRI.substring(0, cutBySymbol(IRI)));
//		Get labels
		HashMap<String,String> dataPropertyAttributeLabels = new HashMap<String,String>();
		Iterator<String> i = ontology.getLexicon(EntityType.DATA_PROP).getNames(IRI, LexicalType.LOCAL_NAME).iterator();
		String localName;
		if (i.hasNext()) {
			localName = i.next();
		}
		else {
			localName = LocalNamer.getLocalName(IRI);
		}
		dataPropertyAttributeLabels.put("IRI-based", localName);
		Set<String> names = ontology.getLexicon(EntityType.DATA_PROP).getNames(IRI, LexicalType.LABEL);
		for (String name : names) {
			Set<String> languages = ontology.getLexicon(EntityType.DATA_PROP).getLanguages(name, IRI, LexicalType.LABEL);
			for (String lang : languages ) {
				dataPropertyAttributeLabels.put(lang, name);
			}
		}
		dataPropertyAttributeMap.put("label", dataPropertyAttributeLabels);
//		Get attributes
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("datatype");
//		Add remaining attributes
		
		dataPropertyAttributeMap.put("attributes", attributes);
//		Get range and domain
		dataPropertyAttributeMap.put("range", IDMap.get(sm.getRange(IRI))); //Nao consigo passar para string
		dataPropertyAttributeMap.put("domain", (IDMap.get(sm.getDomain(IRI)))); //Nao consigo passar para string
//		Get ID
		dataPropertyAttributeMap.put("id", IDMap.get(IRI).toString());
		JSONObject dataPropertyAttributeMapJSON = new JSONObject(dataPropertyAttributeMap);
//		Add JSON object to array containing all "classAttribute" objects
		propertyAttribute.add(dataPropertyAttributeMapJSON);
	}
	
	
	private void createIndividual(String IRI) {
		HashMap<String,Object> individualMap = new HashMap<String,Object>();
		individualMap.put("id", idCounter.toString());
		individualMap.put("type", "owl:NamedIndividual");
		JSONObject individualMapJSON = new JSONObject(individualMap);
//		Adding the iteration to the array containing all class objects
		individual.add(individualMapJSON);
		IDMap.put(IRI, idCounter);	
	}

	private void createIndividualAttribute(Ontology ontology, String IRI) {
		HashMap<String,Object> individualAttributeMap = new HashMap<String,Object>();
//		Get IRI
		individualAttributeMap.put("iri", IRI);
//		Get baseIRI
		individualAttributeMap.put("baseIri", IRI.substring(0,cutBySymbol(IRI)));
//		Get labels
		HashMap<String,String> classAttributeLabels = new HashMap<String,String>();
		Iterator<String> i = ontology.getLexicon(EntityType.INDIVIDUAL).getNames(IRI, LexicalType.LOCAL_NAME).iterator();
		String localName;
		if (i.hasNext()) {
			localName = i.next();
		}
		else {
			localName = LocalNamer.getLocalName(IRI);
		}
		classAttributeLabels.put("IRI-based", localName);
		Set<String> names = ontology.getLexicon(EntityType.INDIVIDUAL).getNames(IRI, LexicalType.LABEL);
		for (String name : names) {
			Set<String> languages = ontology.getLexicon(EntityType.INDIVIDUAL).getLanguages(name, IRI, LexicalType.LABEL);
			for (String lang : languages ) {
				classAttributeLabels.put(lang, name);
			}
		}
		individualAttributeMap.put("label", classAttributeLabels);	
//		Get parent class
//		String parentIRI = sm.getIndividualClasses ? get InstancedClasses? What's the difference?
//		They return sets of classes, why? Shouldn't it just be one?
		Set<String> parentclassIRIs = sm.getIndividualClasses(IRI);
		List<String> parentclassIDs = new ArrayList<String>();
		for (String parentclassIRI : parentclassIRIs) {
			parentclassIDs.add(IDMap.get(parentclassIRI).toString());
		}
		individualAttributeMap.put("parentClass", parentclassIDs);
//		Get attributes
		individualAttributeMap.put("attributes", "attributes");
//		Get ID
		individualAttributeMap.put("id", idCounter.toString());
		JSONObject individualAttributeMapJSON = new JSONObject(individualAttributeMap);
//		Add JSON object to the array containing all individualAttribute objects
		individualAttribute.add(individualAttributeMapJSON);
	}
	
	
	private void createObjectAllValues() {
		HashMap<String,Object> objectAllValuesMap = new HashMap<String,Object>();
		objectAllValuesMap.put("id", idCounter.toString());
		objectAllValuesMap.put("type", "owl:allValuesFrom");
		JSONObject objectAllValuesJSON = new JSONObject(objectAllValuesMap);
//		Adding the iteration to the array containing all "property" objects
		property.add(objectAllValuesJSON);
//		Do I need to save the ID in a Map?	
	}

	
	private void createObjectSomeValues() {
		HashMap<String,Object> objectSomeValuesMap = new HashMap<String,Object>();
		objectSomeValuesMap.put("id", idCounter.toString());
		objectSomeValuesMap.put("type", "owl:someValuesFrom");
		JSONObject objectSomeValuesJSON = new JSONObject(objectSomeValuesMap);
//		Adding the iteration to the array containing all "property" objects
		property.add(objectSomeValuesJSON);
//		Do I need to save the ID in a Map?	
	}
	
//	Also used for ObjectAllValues
	private void createObjectSomeValuesProperty(String IRI, Expression expression) {
		HashMap<String,Object> objectSomeValuesAttributeMap = new HashMap<String,Object>();
//		Get each element (domain, property, range and respective IDs)
		List<Expression> components = expression.getComponents();
		String property = components.get(0).toString();
		String range = components.get(1).toString();
		Integer rangeID = IDMap.get(range);
		String domain = IRI;
		Integer domainID = IDMap.get(domain);
		objectSomeValuesAttributeMap.put("iri", property);
		objectSomeValuesAttributeMap.put("baseIri", property.substring(0, cutBySymbol(IRI)));
		objectSomeValuesAttributeMap.put("range", rangeID.toString());
		objectSomeValuesAttributeMap.put("domain", domainID.toString());
		objectSomeValuesAttributeMap.put("id", idCounter.toString());
		JSONObject objectSomeValuesAttributeJSON = new JSONObject(objectSomeValuesAttributeMap);
		propertyAttribute.add(objectSomeValuesAttributeJSON);
	}


	private void createObjectProperty(String IRI) {
		HashMap<String,String> objectPropertyMap = new HashMap<String, String>();
		objectPropertyMap.put("id", idCounter.toString());
		objectPropertyMap.put("type", "owl:objectProperty");
		JSONObject objectPropertyJSON = new JSONObject(objectPropertyMap);
//		Adding the iteration to the array containing all "property" objects
		property.add(objectPropertyJSON);
		IDMap.put(IRI, idCounter);
		idCounter++;		
	}
	
	private void createObjectPropertyAttribute(Ontology ontology, String IRI) {
		HashMap<String,Object> objectPropertyAttributeMap = new HashMap<String,Object>();
//		Get IRI
		objectPropertyAttributeMap.put("iri", IRI);
//		Get baseIRI
		objectPropertyAttributeMap.put("baseIri", IRI.substring(0, cutBySymbol(IRI)));
//		Get labels
		HashMap<String,String> objectPropertyAttributeLabels = new HashMap<String,String>();
		Iterator<String> i = ontology.getLexicon(EntityType.OBJECT_PROP).getNames(IRI, LexicalType.LOCAL_NAME).iterator();
		String localName;
		if (i.hasNext()) {
			localName = i.next();
		}
		else {
			localName = LocalNamer.getLocalName(IRI);
		}
		objectPropertyAttributeLabels.put("IRI-based", localName);
		Set<String> names = ontology.getLexicon(EntityType.OBJECT_PROP).getNames(IRI, LexicalType.LABEL);
		for (String name : names) {
			Set<String> languages = ontology.getLexicon(EntityType.OBJECT_PROP).getLanguages(name, IRI, LexicalType.LABEL);
			for (String lang : languages ) {
				objectPropertyAttributeLabels.put(lang, name);
			}
		}
		objectPropertyAttributeMap.put("label", objectPropertyAttributeLabels);
//		Get attributes
		ArrayList<String> attributes = new ArrayList<String>();
		objectPropertyAttributeMap.put("attributes", attributes);
//		Get range and domain
		objectPropertyAttributeMap.put("range", sm.getRange(IRI));
		objectPropertyAttributeMap.put("domain", sm.getDomain(IRI));
//		Get ID
		objectPropertyAttributeMap.put("id", IDMap.get(IRI).toString());
		JSONObject objectPropertyAttributeMapJSON = new JSONObject(objectPropertyAttributeMap);
//		Add JSON object to array containing all "classAttribute" objects
		propertyAttribute.add(objectPropertyAttributeMapJSON);
	}


	private void createSubclass(String IRI) {
		HashMap<String,String> subclassMap = new HashMap<String, String>();
		subclassMap.put("id", idCounter.toString());
		subclassMap.put("type", "rdfs:SubClassOf");
		JSONObject subclassJSON = new JSONObject(subclassMap);
//		Adding the iteration to the array containing all "property" objects
		property.add(subclassJSON);
	}
	
	private void createSubclassAttribute(String IRI, String superclassIRI) {
		HashMap<String,Object> subclassAttributeMap = new HashMap<String,Object>();
//		Get domain
		Integer domainID = IDMap.get(IRI);
		subclassAttributeMap.put("domain", domainID.toString());
//		Get range
		Integer rangeID = IDMap.get(superclassIRI);
		subclassAttributeMap.put("range", rangeID.toString());
//		Add ID
		subclassAttributeMap.put("id", idCounter.toString());
		JSONObject subclassAttributeJSON = new JSONObject(subclassAttributeMap);
//		Add JSON object to array containing all "property" objects
		propertyAttribute.add(subclassAttributeJSON);
	}


	private void createType(String IRI) {
		HashMap<String,String> rdfTypeMap = new HashMap<String, String>();
		rdfTypeMap.put("id", idCounter.toString());
		rdfTypeMap.put("type", "rdf:type");
		JSONObject rdfTypeJSON = new JSONObject(rdfTypeMap);
//		Adding the iteration to the array containing all "property" objects
		property.add(rdfTypeJSON);
	}
	
	private void createTypeAttributes(String IRI) {
		HashMap<String,Object> typeAttributeMap = new HashMap<String,Object>();
//		Get domain
		Integer domainID = IDMap.get(IRI);
		typeAttributeMap.put("domain", domainID.toString());
//		Get range
		Set<String> parentclassIRIs = sm.getIndividualClasses(IRI);
		List<String> parentclassIDs = new ArrayList<String>();
		for (String parentclassIRI : parentclassIRIs) {
			parentclassIDs.add(IDMap.get(parentclassIRI).toString());
		}
		typeAttributeMap.put("range", parentclassIDs);
//		Add ID
		typeAttributeMap.put("id", idCounter.toString());
		
		JSONObject typeAttributeJSON = new JSONObject(typeAttributeMap);
//		Add JSON object to array containing all "property" objects
		propertyAttribute.add(typeAttributeJSON);
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

	
}

