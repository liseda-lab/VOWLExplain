package liseda.matcha.io.alignment.owl;

import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import liseda.matcha.alignment.LinkKeyMapping;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.alignment.TransformationMapping;
import liseda.matcha.io.EncodingException;
import liseda.matcha.io.alignment.MappingWriter;
import liseda.matcha.io.ontology.OWLAPIConnector;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.vocabulary.RDFElement;
import liseda.matcha.vocabulary.SKOS;

public class OWLMappingWriter implements MappingWriter
{
	
//Attributes
	
	private OWLAPIConnector con;
	private static final String MEASURE = RDFElement.MEASURE.toRDFExtended();
	private static final String PROVENANCE = RDFElement.PROVENANCE.toRDFExtended();
	private static final String RELATED = SKOS.RELATED.toURI();
	
//Constructor

	public OWLMappingWriter(OWLAPIConnector con)
	{
		this.con = con;
	}
	
//Public Methods
	
	public void writeMapping(Mapping m) throws EncodingException
	{
		if(m instanceof LinkKeyMapping || m instanceof TransformationMapping)
			throw new EncodingException("ERROR: " + m.getClass() + " cannot be encoded in OWL");
		SemanticMap sm = SemanticMap.getInstance();
		//Setup the annotations for similarity score and provenance
		HashSet<OWLAnnotation> annot = new HashSet<OWLAnnotation>();
		annot.add(con.getAnnotation(MEASURE, m.getSimilarity()));
		annot.add(con.getAnnotation(PROVENANCE, "Matcha; " + m.getProvenance() + "; " + m.getStatus().toString()));
		//Simple cases
		if(sm.isClass(m.getEntity1()) && sm.isClass(m.getEntity2()))
			writeClassMapping(con.getClass(m.getEntity1()), con.getClass(m.getEntity2()), m.getRelationship(), annot);
		else if(sm.isDataProperty(m.getEntity1()) && sm.isDataProperty(m.getEntity2()))
			writeDataPropertyMapping(con.getDataProperty(m.getEntity1()), con.getDataProperty(m.getEntity2()),
					m.getRelationship(), annot);
		else if(sm.isObjectProperty(m.getEntity1()) && sm.isObjectProperty(m.getEntity2()))
			writeObjectPropertyMapping(con.getObjectProperty(m.getEntity1()), con.getObjectProperty(m.getEntity2()),
					m.getRelationship(), annot);
		else if(sm.isIndividual(m.getEntity1()) && sm.isIndividual(m.getEntity2()))
			writeIndividualMapping(con.getNamedIndividual(m.getEntity1()), con.getNamedIndividual(m.getEntity2()),
					m.getRelationship(), annot);
		else if(sm.isIndividual(m.getEntity1()) && sm.isClass(m.getEntity2()) &&
				m.getRelationship().equals(MappingRelation.INSTANCE_OF))
			writeInstanceMapping(con.getNamedIndividual(m.getEntity1()),
					con.getClass(m.getEntity2()), annot);
		else if(sm.isClass(m.getEntity1()) && sm.isIndividual(m.getEntity2()) &&
				m.getRelationship().equals(MappingRelation.HAS_INSTANCE))
			writeInstanceMapping(con.getNamedIndividual(m.getEntity2()),
					con.getClass(m.getEntity1()), annot);
		//TODO: Complex cases
		else
		{
			throw new EncodingException("ERROR: Cannot encode mapping : " + m.toString() + " in OWL");
		}
	}

//Private Methods
	
	private void writeClassMapping(OWLClass source, OWLClass target, MappingRelation r, HashSet<OWLAnnotation> annot) throws EncodingException
	{
		if(r.equals(MappingRelation.EQUIVALENCE))
			con.addEquivalent(source, target, annot);
		else if(r.equals(MappingRelation.SUBSUMED_BY))
			con.addSubsumption(source, target, annot);
		else if(r.equals(MappingRelation.SUBSUMES))
			con.addSubsumption(target, source, annot);
		else if(r.equals(MappingRelation.INCOMPATIBLE))
			con.addDisjoint(source, target, annot);
		else if(r.equals(MappingRelation.RELATED))
			con.addObjectSomeValues(source, con.getObjectProperty(RELATED), target, annot);
		else 
			throw new EncodingException("ERROR: Cannot encode relation " + r.getLabel() + " in an OWL class mapping");
	}
	
	private void writeDataPropertyMapping(OWLDataProperty source, OWLDataProperty target, MappingRelation r, HashSet<OWLAnnotation> annot) throws EncodingException
	{
		if(r.equals(MappingRelation.EQUIVALENCE))
			con.addEquivalent(source, target, annot);
		else if(r.equals(MappingRelation.SUBSUMED_BY))
			con.addSubsumption(source, target, annot);
		else if(r.equals(MappingRelation.SUBSUMES))
			con.addSubsumption(target, source, annot);
		else if(r.equals(MappingRelation.INCOMPATIBLE))
			con.addDisjoint(source, target, annot);
		else 
			throw new EncodingException("ERROR: Cannot encode relation " + r.getLabel() + " in an OWL data property mapping");
	}
	
	private void writeObjectPropertyMapping(OWLObjectProperty source, OWLObjectProperty target, MappingRelation r, HashSet<OWLAnnotation> annot) throws EncodingException
	{
		if(r.equals(MappingRelation.EQUIVALENCE))
			con.addEquivalent(source, target, annot);
		else if(r.equals(MappingRelation.SUBSUMED_BY))
			con.addSubsumption(source, target, annot);
		else if(r.equals(MappingRelation.SUBSUMES))
			con.addSubsumption(target, source, annot);
		else if(r.equals(MappingRelation.INCOMPATIBLE))
			con.addDisjoint(source, target, annot);
		else 
			throw new EncodingException("ERROR: Cannot encode relation " + r.getLabel() + " in an OWL object property mapping");
	}
	
	private void writeIndividualMapping(OWLNamedIndividual source, OWLNamedIndividual target, MappingRelation r, HashSet<OWLAnnotation> annot) throws EncodingException
	{
		if(r.equals(MappingRelation.EQUIVALENCE))
			con.addEquivalent(source, target, annot);
		else if(r.equals(MappingRelation.INCOMPATIBLE))
			con.addDisjoint(source, target, annot);
		else if(r.equals(MappingRelation.RELATED))
			con.addDisjoint(source, target, annot);
		else 
			throw new EncodingException("ERROR: Cannot encode relation " + r.getLabel() + " in an OWL individual mapping");
	}
	
	private void writeInstanceMapping(OWLNamedIndividual i, OWLClass c, HashSet<OWLAnnotation> annot) throws EncodingException
	{
		con.addInstance(i, c, annot);
	}
}
