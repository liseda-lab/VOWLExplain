/******************************************************************************
* A SKOS thesaurus file parser based on the OWL API.                          *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.ontology;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import liseda.matcha.data.Map2Map;
import liseda.matcha.ontology.MediatorOntology;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.LexicalType;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Formalism;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.ObjectSomeValues;
import liseda.matcha.semantics.owl.SimpleClass;
import liseda.matcha.semantics.owl.SimpleObjectProperty;
import liseda.matcha.util.LocalNamer;
import liseda.matcha.util.StringParser;
import liseda.matcha.vocabulary.SKOS;

public class SKOSParser //TODO: revisit this and consider keeping SKOS semantics as they are
{

//Attributes

	private OWLAPIConnector con;
		
//Constructors
		
	public SKOSParser(OWLAPIConnector con)
	{
		this.con = con;
	}
		
//Public Methods

	/**
	 * Parses a SKOS thesaurus into a given Ontology object
	 * @param con: the connector to the OWL API
	 * @param o: the OWLOntology object to parse
	 * @param l: the Ontology object to populate
	 */
	public void parse(Ontology l)
	{
		//Set the ontology IRI as the concatenation of concept schemes in the SKOS thesaurus
		OWLClass scheme = con.getClass(SKOS.CONCEPT_SCHEME.toURI());
		l.setFormalism(Formalism.SKOS);
		String schemeURIs = "";
		for(OWLIndividual i : con.getClassIndividualsTransitive(scheme))
			if(i.isNamed())
				schemeURIs += i.asOWLNamedIndividual().getIRI().toString() + " | ";
		if(schemeURIs.length() > 0)
			l.setURI(schemeURIs.substring(0, schemeURIs.length() - 3));
		
		//SKOS concepts are instances of class "concept"
		Set<OWLIndividual> indivs = con.getClassIndividualsTransitive(con.getClass(SKOS.CONCEPT.toURI()));
		parseSKOSConcepts(con, indivs, l);
		parseSKOSRelations(con, indivs, l);
	}
	
	/**
	 * Parses a SKOS thesaurus into a given MediatorOntology object
	 * @param con: the connector to the OWL API
	 * @param o: the OWLOntology object to parse
	 * @param m: the MediatorOntology object to populate
	 */
	public void parse(MediatorOntology m)
	{
		Set<OWLIndividual> indivs = con.getClassIndividualsTransitive(con.getClass(SKOS.CONCEPT.toURI()));
		parseSKOSConcepts(con, indivs, m);
	}
	
//Private Methods
	
	//SKOS Concepts (which are technically OWL Individuals, but will be treated as Classes)
	private void parseSKOSConcepts(OWLAPIConnector con, Set<OWLIndividual> indivs, Ontology l)
	{
		SemanticMap sm = SemanticMap.getInstance();
		Lexicon lex = l.getLexicon(EntityType.CLASS);

		//Process the individuals as if they were classes
		for(OWLIndividual i : indivs)
		{
			if(!i.isNamed())
				continue;
			OWLNamedIndividual ind = i.asOWLNamedIndividual();
			String indivUri = ind.getIRI().toString();
			sm.addEntity(indivUri, EntityType.CLASS);
			l.add(indivUri, EntityType.CLASS);
			Map2Map<LexicalType,String,String> annot = con.getLexicalAnnotations(ind);
			for(LexicalType type : annot.keySet())
				for(String val : annot.keySet(type))
					LexicalParser.addAnnotation(lex, indivUri, type, val, annot.get(type, val), false);
			//If the local name is not an alphanumeric code, add it to the lexicon
			String localName = LocalNamer.getLocalName(indivUri);
			if(!StringParser.isNumericId(localName))
				LexicalParser.addLocalName(lex,indivUri,localName,false);
		}
	}
	
	//SKOS Concepts (which are technically OWL Individuals, but will be treated as Classes)
	private void parseSKOSConcepts(OWLAPIConnector con, Set<OWLIndividual> indivs, MediatorOntology l)
	{
		Lexicon lex = l.getLexicon();
		//Process the individuals as if they were classes
		for(OWLIndividual i : indivs)
		{
			if(!i.isNamed())
				continue;
			OWLNamedIndividual ind = i.asOWLNamedIndividual();
			String indivUri = ind.getIRI().toString();
			Map2Map<LexicalType,String,String> annot = con.getLexicalAnnotations(ind);
			for(LexicalType type : annot.keySet())
				for(String val : annot.keySet(type))
					LexicalParser.addAnnotation(lex, indivUri, type, val, annot.get(type, val), false);
			//If the local name is not an alphanumeric code, add it to the lexicon
			String localName = LocalNamer.getLocalName(indivUri);
			if(!StringParser.isNumericId(localName))
				LexicalParser.addLocalName(lex,indivUri,localName,false);
		}
	}

	//SKOS relationships
	private void parseSKOSRelations(OWLAPIConnector con, Set<OWLIndividual> indivs, Ontology l)
	{
		//We convert "broader", "broader_transitive", "narrower" and "narrower_transitive" to subclass relationships
		//We treat "related" as a someValues restriction on property "related" and also as a disjoint, by SKOS definition
		SemanticMap sm = SemanticMap.getInstance();
		String[] rels = {SKOS.BROADER.toURI(),SKOS.BROADER_TRANS.toURI(),SKOS.NARROWER.toURI(),SKOS.NARROWER_TRANS.toURI(),SKOS.RELATED.toURI()};
		String related = SKOS.RELATED.toURI().toString();
		//Add the "related" property to the ontology
		SimpleObjectProperty r = new SimpleObjectProperty(related);
		sm.addURI(related, EntityType.OBJECT_PROP);
		l.getLexicon(EntityType.OBJECT_PROP).add(related, SKOS.RELATED.toString(), "en", LexicalType.LOCAL_NAME,
				"", LexicalType.LOCAL_NAME.getDefaultWeight());
		l.add(related,EntityType.OBJECT_PROP);
		//Related is symmetric by definition and irreflexive (since it implies disjointness)
		sm.addSymmetric(related);
		sm.addIrreflexive(related);
		for(OWLIndividual i : indivs)
		{
			if(!i.isNamed())
				continue;
			OWLNamedIndividual ind = i.asOWLNamedIndividual();
			for(String iri : rels)
			{
				Set<OWLIndividual> relInd = con.getRelatedIndividuals(ind.getIRI(), IRI.create(iri));
				for(OWLIndividual j: relInd)
				{
					if(!j.isNamed())
						continue;
					if(iri.equals(SKOS.BROADER.toURI()) || iri.equals(SKOS.BROADER_TRANS.toURI()))
						sm.addSubclass(i.toStringID(), j.toStringID());
					else if(iri.equals(SKOS.NARROWER.toURI()) || iri.equals(SKOS.BROADER_TRANS.toURI()))
						sm.addSubclass(j.toStringID(), i.toStringID());
					else if(iri.equals(SKOS.RELATED.toURI()))
					{
						ObjectSomeValues sv1 = new ObjectSomeValues(r, new SimpleClass(j.toStringID()));
						l.add(sv1.toString(), EntityType.CLASS_EXPRESSION);
						sm.addExpression(sv1);
						sm.addSubclass(i.toStringID(), sv1.toString());
						ObjectSomeValues sv2 = new ObjectSomeValues(r, new SimpleClass(i.toStringID()));
						sm.addExpression(sv1);
						sm.addSubclass(j.toStringID(), sv2.toString());
						sm.addDisjoint(i.toStringID(), j.toStringID());
					}
				}
			}
		}
	}
}