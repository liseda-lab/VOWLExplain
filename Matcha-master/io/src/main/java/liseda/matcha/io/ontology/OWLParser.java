/******************************************************************************
* An Ontology file parser based on the OWL API.                               *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*                                                                             *
* @author Daniel Faria, Catia Pesquita, Beatriz Lima                          *
******************************************************************************/
package liseda.matcha.io.ontology;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import com.google.common.collect.Multimap;

import liseda.matcha.ontology.MediatorOntology;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.ReferenceMap;
import liseda.matcha.data.Map2Map;
import liseda.matcha.ontology.AttributeMap;
import liseda.matcha.ontology.lexicon.LexicalType;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;
import liseda.matcha.util.LocalNamer;
import liseda.matcha.util.StringParser;
import liseda.matcha.vocabulary.OBO;

public class OWLParser //TODO: Handle datatype definition expressions and custom datatypes
{

//Attributes
	
	private OWLAPIConnector con;
	
//Constructors
	
	public OWLParser(OWLAPIConnector con)
	{
		this.con = con;
	}
	
//Public Methods

	/**
	 * Parses an OWL ontology into a given Ontology object
	 * @param l: the Ontology object to populate
	 */
	public void parse(Ontology o)
	{
		IRI i = con.getOntologyIRI();
		if(i != null)
			o.setURI(i.toString());
		parseOWLClasses(con,o);
		parseOWLDataProperties(con,o);
		parseOWLObjectProperties(con,o);
		parseOWLNamedIndividuals(con,o);
		parseOWLAnonymousIndividuals(con,o);
		parseClassRelations(con,o);
		parseIndividualRelations(con,o);
		parseDataPropertyRelations(con,o);
		parseObjectPropertyRelations(con,o);
	}
	
	/**
	 * Parses an OWL ontology into a given MediatorOntology object
	 * @param m: the MediatorOntology object to populate
	 */
	public void parse(MediatorOntology m)
	{
		m.setURI(con.getOntologyIRI().toString());
		parseOWLClasses(con,m);
	}
	
//Private Methods	

	//Parse all classes in the ontology
	private void parseOWLClasses(OWLAPIConnector con, Ontology l)
	{
		//Get the core data structures
		SemanticMap sm = SemanticMap.getInstance();
		Lexicon cLex = l.getLexicon(EntityType.CLASS);
		ReferenceMap refs = l.getReferenceMap();
		for(OWLClass c : con.getClasses())
		{
			if(c.isOWLThing() || c.isAnonymous())
				continue;
			String classUri = c.toStringID();
			//Add the URI it to the SemanticMap and Ontology
			sm.addURI(classUri,EntityType.CLASS);
			l.add(classUri,EntityType.CLASS);
			//Add the lexical annotations to the Lexicon
			Map2Map<LexicalType,String,String> annot = con.getLexicalAnnotations(c);
			for(LexicalType type : annot.keySet())
				for(String val : annot.keySet(type))
					LexicalParser.addAnnotation(cLex, classUri, type, val, annot.get(type, val), false);
			//Check if it is deprecated
			if(con.isDeprecated(c))
				l.setDeprecated(classUri);
			//Add the class's cross references to the ReferenceMap
			HashSet<String> xrefs = con.getCrossReferences(c);
			for(String s : xrefs)
			{
				if(!s.startsWith("http") && !s.startsWith("url:"))
					refs.add(classUri,s.replace(':','_'));
				else
					refs.add(classUri,s);				
			}
			//If the local name is not an alphanumeric code, add it to the lexicon
			String localName = LocalNamer.getLocalName(classUri);
			if(!StringParser.isNumericId(localName))
				LexicalParser.addLocalName(cLex,classUri,localName,false);
		}
	}
	
	//Parse all classes in the ontology
	private static void parseOWLClasses(OWLAPIConnector con, MediatorOntology l)
	{
		//Get the core data structures
		Lexicon cLex = l.getLexicon();
		ReferenceMap refs = l.getReferenceMap();
		for(OWLClass c : con.getClasses())
		{
			if(c.isOWLThing() || c.isAnonymous())
				continue;
			String classUri = c.toStringID();
			//If the local name is not an alphanumeric code, add it to the lexicon
			String localName = LocalNamer.getLocalName(classUri);
			if(!StringParser.isNumericId(localName))
				LexicalParser.addLocalName(cLex,classUri,localName,false);
			//Add the lexical annotations to the Lexicon
			Map2Map<LexicalType,String,String> annot = con.getLexicalAnnotations(c);
			for(LexicalType type : annot.keySet())
				for(String val : annot.keySet(type))
					LexicalParser.addAnnotation(cLex, classUri, type, val, annot.get(type, val), false);
			//Add the class's cross references to the ReferenceMap
			HashSet<String> xrefs = con.getCrossReferences(c);
			for(String s : xrefs)
			{
				if(!s.startsWith("http") && !s.startsWith("url:"))
					refs.add(classUri,s.replace(':','_'));
				else
					refs.add(classUri,s);				
			}
		}
	}
	
	//Parse all data properties in the ontology
	private static void parseOWLDataProperties(OWLAPIConnector con, Ontology l)
	{
		//Get the core data structures
		SemanticMap sm = SemanticMap.getInstance();
		Lexicon dLex = l.getLexicon(EntityType.DATA_PROP);

		for(OWLDataProperty dp : con.getDataProperties())
		{
			if(dp.isAnonymous() || dp.isOWLTopDataProperty())
				continue;
			String propUri = dp.toStringID();
			//Add the URI it to the SemanticMap and Ontology
			sm.addURI(propUri,EntityType.DATA_PROP);
			l.add(propUri,EntityType.DATA_PROP);
			//Add the lexical annotations to the Lexicon
			Map2Map<LexicalType,String,String> annot = con.getLexicalAnnotations(dp);
			for(LexicalType type : annot.keySet())
				for(String val : annot.keySet(type))
					LexicalParser.addAnnotation(dLex, propUri, type, val, annot.get(type, val), true);
			//Check if it is deprecated
			if(con.isDeprecated(dp))
				l.setDeprecated(propUri);
			//If the local name is not an alphanumeric code, add it to the lexicon
			String localName = LocalNamer.getLocalName(propUri);
			if(!StringParser.isNumericId(localName))
				LexicalParser.addLocalName(dLex,propUri,localName,true);
			//If the property is functional, add it to the SemanticMap
			if(EntitySearcher.isFunctional(dp, con.getOntology()))
				sm.addFunctional(propUri);
			//Add its domain and range to the SemanticMap
			DomainAndRangeParser.addDomain(propUri, con.getDataDomains(dp));
			DomainAndRangeParser.addDataRange(propUri, con.getDataRanges(dp));
		}
	}
	
	//Parse all object properties in the ontology
	private static void parseOWLObjectProperties(OWLAPIConnector con, Ontology l)
	{
		SemanticMap sm = SemanticMap.getInstance();
		Lexicon oLex = l.getLexicon(EntityType.OBJECT_PROP);
		for(OWLObjectProperty op : con.getObjectProperties())
		{
			if(op.isAnonymous() || op.isOWLTopObjectProperty())
				continue;
			String propUri = op.toStringID();
			//Add the URI it to the SemanticMap and Ontology
			sm.addURI(propUri,EntityType.OBJECT_PROP);
			l.add(propUri,EntityType.OBJECT_PROP);
			//Add the lexical annotations to the Lexicon
			Map2Map<LexicalType,String,String> annot = con.getLexicalAnnotations(op);
			for(LexicalType type : annot.keySet())
				for(String val : annot.keySet(type))
					LexicalParser.addAnnotation(oLex, propUri, type, val, annot.get(type, val), true);
			//Check if it is deprecated
			if(con.isDeprecated(op))
				l.setDeprecated(propUri);
			//If the local name is not an alphanumeric code, add it to the lexicon
			String localName = LocalNamer.getLocalName(propUri);
			if(!StringParser.isNumericId(localName))
				LexicalParser.addLocalName(oLex,propUri,localName,true);
			//Add the property's properties to the SemanticMap
			if(EntitySearcher.isFunctional(op,con.getOntology()))
				sm.addFunctional(propUri);
			if(EntitySearcher.isTransitive(op,con.getOntology()))
				sm.addTransitive(propUri);
			if(EntitySearcher.isReflexive(op,con.getOntology()))
				sm.addReflexive(propUri);
			if(EntitySearcher.isIrreflexive(op,con.getOntology()))
				sm.addIrreflexive(propUri);
			if(EntitySearcher.isSymmetric(op,con.getOntology()))
				sm.addSymmetric(propUri);
			if(EntitySearcher.isAsymmetric(op,con.getOntology()))
				sm.addAsymmetric(propUri);
			//Add its domain and range to the SemanticMap
			DomainAndRangeParser.addDomain(propUri, con.getObjectDomains(op));
			DomainAndRangeParser.addObjectRange(propUri, con.getObjectRanges(op));

		}
	}

	//Parse all named individuals in the ontology
	private static void parseOWLNamedIndividuals(OWLAPIConnector con, Ontology l)
	{
		//We need to parse individuals on an individual basis due to recursive calls
		for(OWLNamedIndividual i : con.getNamedIndividuals())
			parseOWLNamedIndividual(i,con,l);
	}
	
	//Parse an individual named individual (recursively if its annotations link to other individuals)
	private static void parseOWLNamedIndividual(OWLNamedIndividual i, OWLAPIConnector con, Ontology l)
	{
		//Get the core data structures
		SemanticMap sm = SemanticMap.getInstance();
		Lexicon iLex = l.getLexicon(EntityType.INDIVIDUAL);
		AttributeMap vMap = l.getAttributeMap();
		String indivUri = i.toStringID();
		//Add the URI it to the SemanticMap and Ontology
		sm.addURI(indivUri, EntityType.INDIVIDUAL);
		l.add(indivUri, EntityType.INDIVIDUAL);
		//For individuals, different annotations go to different places, so it is
		//more effective to process them all at once
		HashSet<OWLAnnotation> annots = new HashSet<OWLAnnotation>();
		EntitySearcher.getAnnotations(i, con.getOntology()).forEach(annots::add);
		for(OWLAnnotation a : annots)
		{
			String propUri = a.getProperty().toStringID();
			if(a.getValue() instanceof OWLLiteral)
			{
				OWLLiteral val = (OWLLiteral) a.getValue();
				//Lexical annotations go to the Lexicon
				if(LexicalType.getLexicalType(propUri) != null)
					LexicalParser.addAnnotation(iLex,indivUri, LexicalType.getLexicalType(propUri), val.getLiteral(), val.getLang(), false);
				//Other literal annotations go to the ValueMap
				else if(a.getValue() instanceof OWLLiteral)
				{
					//We must first add the annotation property to the SemanticMap and Ontology
					sm.addURI(propUri, EntityType.ANNOTATION_PROP);
					l.add(propUri, EntityType.ANNOTATION_PROP);
					//Then add the value to the ValueMap
					ValueParser.addValue(vMap, indivUri, propUri, val);
				}
			}
			//If ontologies fail to declare object properties, they register as annotation properties,
			//so we need to search the latter for encoded individual relations
			else if(a.getValue().isIRI())
			{
				//If the property is relating two individuals, we assume it to be an undeclared object property
				if(con.containsNamedIndividual(a.getValue().asIRI().get()))
				{
					//So we must add it to the SemanticMap and Ontology
					sm.addURI(propUri, EntityType.OBJECT_PROP);
					l.add(propUri, EntityType.OBJECT_PROP);
					//Then if the individual is not listed in the ontology, we need to process it
					if(!l.getTypes(a.getValue().asIRI().toString()).contains(EntityType.INDIVIDUAL))
						parseOWLNamedIndividual(con.getNamedIndividual(a.getValue().asIRI().toString()), con, l);
					//Finally, we need to add the relation between the individuals
					sm.addIndividualRelationship(indivUri, a.getValue().asIRI().toString(), propUri);
				}
				//Otherwise, we add it to the ValueMap
				else
				{
					//We must first add the annotation property to the SemanticMap and Ontology
					sm.addURI(propUri, EntityType.ANNOTATION_PROP);
					l.add(propUri, EntityType.ANNOTATION_PROP);
					//Then add the value to the ValueMap
					vMap.add(indivUri, propUri, a.getValue().asIRI().toString(), OWL2Datatype.XSD_ANY_URI.getIRI().toString());
				}
			}
		}
		String localName = LocalNamer.getLocalName(indivUri);
		if(!StringParser.isNumericId(localName))
			LexicalParser.addLocalName(iLex,indivUri,localName,false);
		//Get the data properties associated with the individual and their values
		Multimap<OWLDataPropertyExpression,OWLLiteral> dataPropValues = EntitySearcher.getDataPropertyValues(i,con.getOntology());
		for(OWLDataPropertyExpression prop : dataPropValues.keySet())
		{
			//Check that the data property expression is a named data property
			if(prop.isAnonymous())
				continue;
			String propUri = prop.asOWLDataProperty().toStringID();
			for(OWLLiteral val : dataPropValues.get(prop))
				ValueParser.addValue(vMap, indivUri, propUri, val);

			//If no type was declared for the individual, try to infer it from the data property domain restriction
			if(!con.hasType(i) && sm.isClass(sm.getDomain(propUri)))
				sm.addInstance(indivUri, sm.getDomain(propUri));
		}
		//Add the (non-redundant) types of the individual to the SemanticMap
		for(OWLClass c : con.getIndividualNonRedundantTypes(i))
			sm.addInstance(indivUri, c.toStringID());
	}

	//Parse all anonymous individuals in the ontology
	private static void parseOWLAnonymousIndividuals(OWLAPIConnector con, Ontology l)
	{
		SemanticMap sm = SemanticMap.getInstance();
		for(OWLAnonymousIndividual i : con.getAnonymousIndividuals())
		{
			//Get the ID of each anonymous individual
			String indivID = i.toStringID();
			//Add it to the Semantic Map and Ontology
			sm.addURI(indivID, EntityType.ANON_INDIVIDUAL);
			//Add it to the Ontology
			l.add(indivID, EntityType.ANON_INDIVIDUAL);

			//Get the data properties associated with the individual and their values
			Multimap<OWLDataPropertyExpression,OWLLiteral> dataPropValues = EntitySearcher.getDataPropertyValues(i,con.getOntology());
			for(OWLDataPropertyExpression prop : dataPropValues.keySet())
			{
				//Check that the data property expression is a named data property
				if(prop.isAnonymous() || !prop.isOWLDataProperty())
					continue;
				//And if so, process its URI
				String propUri = prop.asOWLDataProperty().toStringID();
				for(OWLLiteral val : dataPropValues.get(prop))
					ValueParser.addValue(l.getAttributeMap(), indivID, propUri, val);
				//If no type was declared for the individual, try to infer it from the data property domain restriction
				if(!con.hasType(i) && sm.isClass(sm.getDomain(propUri)))
					sm.addInstance(indivID, sm.getDomain(propUri));
			}
			//Add the (non-redundant) types of the individual to the SemanticMap
			for(OWLClass c : con.getIndividualNonRedundantTypes(i))
				sm.addInstance(indivID, c.toStringID());
		}
	}
	
	//Parse all class relationships
	private static void parseClassRelations(OWLAPIConnector con, Ontology l)
	{
		SemanticMap sm = SemanticMap.getInstance();
		//For each class index
		for(OWLClass c : con.getClasses())
		{
			//Get its identifier
			String child = c.toStringID();
			if(!sm.isClass(child))
				continue;
			//Get the subclass expressions to capture and add relationships
			for(OWLClassExpression e : con.getSuperClasses(c))
			{
				if(e.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS))
				{
					String parent = e.asOWLClass().getIRI().toString();
					if(!sm.isClass(parent))
						continue;
					sm.addSubclass(child, parent);
				}
				else
				{
					ClassExpression x = OWLExpressionParser.parse(e);
					if(x == null)
						continue;
					sm.addExpression(x);
					sm.addSubclass(child, x.toString());
				}
			}		
			//Get the equivalence expressions to capture and add relationships
			for(OWLClassExpression e : con.getEquivalentClasses(c))
			{
				if(e.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS))
				{
					String parent = e.asOWLClass().toStringID();
					if(!sm.isClass(parent))
						continue;
					sm.addEquivalentClasses(child, parent);
				}
				//Look for OBO logical definitions, which are encoded as equivalent class expressions with
				//someValuesFrom restrictions onProperty "has_part" over an intersection of class expressions
				else if(e.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM))
				{
					//Typecast the expression and get the restricted property
					OWLObjectSomeValuesFrom sv = (OWLObjectSomeValuesFrom)e;
					OWLObjectPropertyExpression p = sv.getProperty();
					//If it isn't the OBO has_part property, skip it 
					if(p.isOWLObjectProperty() && p.asOWLObjectProperty().toStringID().equals(OBO.HAS_PART.toURI()))
					{
						//Get the restricted class expression
						OWLClassExpression logicDef = sv.getFiller();
						//If it isn't an intersection, skip it
						if(!logicDef.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF))
							continue;
						//Get the intersected class expressions
						Set<OWLClassExpression> conj = logicDef.asConjunctSet();
						boolean check = true;
						for(OWLClassExpression n : conj)
						{
							//Check if all intersected classes are either simple classes or someValuesFrom expressions
							if(!(n.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS) || n.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)))
							{
								check = false;
								break;
							}
						}
						if(check)
							l.getReferenceMap().add(child, logicDef.toString());
						//If it is not a logical definition, then it is a plain someValues restriction
						else
						{
							ClassExpression x = OWLExpressionParser.parse(e);
							if(x == null)
								continue;
							sm.addExpression(x);
							sm.addEquivalentClasses(child, x.toString());
						}
					}
				}
				else
				{
					ClassExpression x = OWLExpressionParser.parse(e);
					if(x == null)
						continue;
					sm.addExpression(x);
					sm.addEquivalentClasses(child, x.toString());
				}
			}
			//Get the syntactic disjoints
			for(OWLClassExpression dClass : con.getDisjointClasses(c))
			{
				if(dClass.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS))
				{
					String parent = dClass.asOWLClass().toStringID();
					if(sm.isClass(parent))
						sm.addDisjoint(child, parent);
				}
				//OWL disjoints are typically declared between classes, not class expressions, but just in case...
				else
				{
					ClassExpression ce = OWLExpressionParser.parse(dClass);
					if(ce == null)
						continue;
					if(!sm.contains(ce.toString()))
						sm.addExpression(ce);
					sm.addDisjoint(child, ce.toString());
				}
			}	
		}
	}
	
	//Parse all relationships between individuals (named and anonymous) in the ontology
	private static void parseIndividualRelations(OWLAPIConnector con, Ontology l)
	{
		SemanticMap sm = SemanticMap.getInstance();
		Set<OWLIndividual> ind = new HashSet<OWLIndividual>(con.getNamedIndividuals());
		ind.addAll(con.getAnonymousIndividuals());
		for(OWLIndividual i : ind)
		{
			//Get the numeric id for each individual
			String indivUri = i.toStringID();
			if(!sm.isIndividual(indivUri))
				continue;
			
			//Add sameAs individuals
			for(OWLIndividual i2: con.getSameAsIndividuals(i))
				if(i2.isOWLNamedIndividual() && !i2.equals(i))
					sm.addSameAsIndividuals(indivUri, i2.asOWLNamedIndividual().toStringID());

			Multimap<OWLObjectPropertyExpression, OWLIndividual> iProps = EntitySearcher.getObjectPropertyValues(i,con.getOntology());
			for(OWLObjectPropertyExpression prop : iProps.keySet())
			{
				if(prop.isAnonymous())
					continue;
				String propUri = prop.asOWLObjectProperty().toStringID();
				if(!sm.isObjectProperty(propUri))
					continue;
				//If we still don't know the type of the individual, we can try to infer it from the object property domain restriction
				if(sm.getIndividualClasses(indivUri).isEmpty() && sm.isClass(sm.getDomain(propUri)))
					sm.addInstance(indivUri, sm.getDomain(propUri));

				for(OWLIndividual rI : iProps.get(prop))
				{
					String relIndivUri = rI.toStringID();
					if(!sm.isIndividual(relIndivUri))
						continue;
					sm.addIndividualRelationship(indivUri, relIndivUri, propUri);
					//We can also fill in missing types for related individuals based on the object property range restriction
					if(sm.getIndividualClasses(relIndivUri).isEmpty() && sm.isClass(sm.getRange(propUri)))
						sm.addInstance(relIndivUri, sm.getRange(propUri));
				}
			}
		}
	}

	//Parse all relationships between data properties in the ontology
	private static void parseDataPropertyRelations(OWLAPIConnector con, Ontology l)
	{
		SemanticMap sm = SemanticMap.getInstance();
		for(OWLDataProperty dp : con.getDataProperties())
		{
			String propUri = dp.getIRI().toString();
			if(!sm.isDataProperty(propUri))
				continue;
			for(OWLDataPropertyExpression de : con.getSuperProperties(dp))
			{
				//The only OWLDataPropertyExpression possible is OWLDataProperty
				OWLDataProperty sProp = de.asOWLDataProperty();
				String sPropUri = sProp.getIRI().toString();
				if(sm.isDataProperty(sPropUri))
					sm.addSubproperty(propUri,sPropUri);	
			}
		}
	}
	
	//Parse all relationships between data properties in the ontology
	private static void parseObjectPropertyRelations(OWLAPIConnector con, Ontology l)
	{
		SemanticMap sm = SemanticMap.getInstance();
		for(OWLObjectProperty op : con.getObjectProperties())
		{
			String propUri = op.getIRI().toString();
			if(!sm.isObjectProperty(propUri))
				continue;
			//Superproperties
			for(OWLObjectPropertyExpression oe : con.getSuperProperties(op))
			{
				String sPropUri;
				if(oe.isOWLObjectProperty())
					sPropUri = oe.asOWLObjectProperty().toStringID();
				else
					sPropUri = OWLExpressionParser.parse(oe).toString();
					if(sPropUri == null)
						continue;
				if(sm.contains(sPropUri))
					sm.addSubproperty(propUri,sPropUri);
			}
			//Inverse properties
			for(OWLObjectPropertyExpression oe : con.getInverseProperties(op))
			{
				if(!oe.isOWLObjectProperty())
					continue;
				String iPropUri = oe.asOWLObjectProperty().toStringID();
				if(sm.isObjectProperty(iPropUri))
					sm.addInverseProp(propUri,iPropUri);
				//We can fill in missing domain/range declarations from those of the inverse property
				if(sm.getDomain(propUri) == null && sm.getRange(iPropUri) != null)
					sm.addDomain(propUri, sm.getRange(iPropUri));
				if(sm.getRange(propUri) == null && sm.getDomain(iPropUri) != null)
					sm.addRange(propUri, sm.getDomain(iPropUri));
			}
			//Property chains
			for(OWLSubPropertyChainOfAxiom a : con.getSubPropertyChainAxioms(op))
			{
				List<OWLObjectPropertyExpression> chain = ((OWLSubPropertyChainOfAxiom)a).getPropertyChain();
				List<ObjectPropertyExpression> parsedChain = new Vector<ObjectPropertyExpression>();
				for(OWLObjectPropertyExpression e : chain)
				{
					ObjectPropertyExpression ce = OWLExpressionParser.parse(e);
					if(ce != null)
						parsedChain.add(ce);
				}
			}
		}
	}
}