/******************************************************************************
* MappingReader for OWL mappings.                                             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.alignment.owl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.data.Triple;
import liseda.matcha.io.EncodingException;
import liseda.matcha.io.alignment.MappingReader;
import liseda.matcha.io.ontology.OWLExpressionParser;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;
import liseda.matcha.semantics.owl.ObjectSomeValues;
import liseda.matcha.vocabulary.RDFElement;
import liseda.matcha.vocabulary.SKOS;

public class OWLMappingReader implements MappingReader<OWLAxiom>
{

//Attributes
	
	private Alignment a;
	private Ontology source;
	private Ontology target;
	private SemanticMap sm;
	private static final String MEASURE = RDFElement.MEASURE.toRDFExtended();
	private static final String PROVENANCE = RDFElement.PROVENANCE.toRDFExtended();
	private static final String RELATED = SKOS.RELATED.toURI();
	
//Constructors
	
	public OWLMappingReader(Alignment a)
	{
		this.a = a;
		this.source = a.getSourceOntology();
		this.target = a.getTargetOntology();
		this.sm = SemanticMap.getInstance();
	}
	
//Public Methods
	
	public void readMapping(OWLAxiom x) throws EncodingException
	{
		if(x instanceof OWLEquivalentClassesAxiom)
			parse((OWLEquivalentClassesAxiom)x);
		else if(x instanceof OWLSubClassOfAxiom)
			parse((OWLSubClassOfAxiom)x);
		else if(x instanceof OWLDisjointClassesAxiom)
			parse((OWLDisjointClassesAxiom)x);
		else if(x instanceof OWLEquivalentDataPropertiesAxiom)
			parse((OWLEquivalentDataPropertiesAxiom)x);
		else if(x instanceof OWLSubDataPropertyOfAxiom)
			parse((OWLSubDataPropertyOfAxiom)x);
		else if(x instanceof OWLDisjointDataPropertiesAxiom)
			parse((OWLDisjointDataPropertiesAxiom)x);
		else if(x instanceof OWLEquivalentObjectPropertiesAxiom)
			parse((OWLEquivalentObjectPropertiesAxiom)x);
		else if(x instanceof OWLSubObjectPropertyOfAxiom)
			parse((OWLSubObjectPropertyOfAxiom)x);
		else if(x instanceof OWLDisjointObjectPropertiesAxiom)
			parse((OWLDisjointObjectPropertiesAxiom)x);
		else if(x instanceof OWLSameIndividualAxiom)
			parse((OWLSameIndividualAxiom)x);
		else if(x instanceof OWLDifferentIndividualsAxiom)
			parse((OWLDifferentIndividualsAxiom)x);
		else if(x instanceof OWLClassAssertionAxiom)
			parse((OWLClassAssertionAxiom)x);
		else
			throw new EncodingException("ERROR: Cannot parse axiom type " + x.getAxiomType());
	}

//Private Methods
	
	//Adds a mapping to the alignment, ensuring the right order of the entities
	private void addMapping(String src, String tgt, double sim, MappingRelation rel, String prov, MappingStatus s) throws EncodingException
	{
		//Both entities are fully contained by the ontologies, in the right order
		if(source.contains(src) && target.contains(tgt))
			a.add(new Mapping(src,tgt,sim,rel,prov,s));
		//Both entities are fully contained by the ontologies, in reverse order
		else if(source.contains(tgt) && target.contains(src))
			a.add(new Mapping(tgt,src,sim,rel.inverse(),prov,s));
		//One entity is fully contained by one of the ontologies, the other is partially contained by the other, in the right order
		else if((source.containsPartial(src) && target.contains(tgt)) || (source.contains(src) && target.containsPartial(tgt)))
			a.add(new Mapping(src,tgt,sim,rel,prov,s));
		//One entity is fully contained by one of the ontologies, the other is partially contained by the other, in reverse order
		else if((source.containsPartial(tgt) && target.contains(src)) || (source.contains(tgt) && target.containsPartial(src)))
			a.add(new Mapping(tgt,src,sim,rel.inverse(),prov,s));
		//Both entities are partially contained by the ontologies, in the right order
		if(source.containsPartial(src) && target.containsPartial(tgt))
			a.add(new Mapping(src,tgt,sim,rel,prov,s));
		//Both entities are partially contained by the ontologies, in reverse order
		else if(source.containsPartial(tgt) && target.containsPartial(src))
			a.add(new Mapping(tgt,src,sim,rel.inverse(),prov,s));
		else
			throw new EncodingException("ERROR: Mapped entities not listed in the input ontologies (" + src + "; " + tgt + ")");
	}
	
	private void parse(OWLEquivalentClassesAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		Stream<OWLClassExpression> sx = x.classExpressions();
		//Mappings cannot include more than two entities
		if(sx.count() != 2)
			return;
		Iterator<OWLClassExpression> it = sx.iterator();
		parseClasses(it.next(),it.next(),prov.get1(),MappingRelation.EQUIVALENCE,prov.get2(),prov.get3());
	}
	
	private void parse(OWLSubClassOfAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		parseClasses(x.getSubClass(),x.getSuperClass(),prov.get1(),MappingRelation.SUBSUMED_BY,prov.get2(),prov.get3());
	}
	
	private void parse(OWLDisjointClassesAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		Stream<OWLClassExpression> sx = x.classExpressions();
		//Mappings cannot include more than two entities
		if(sx.count() != 2)
			return;
		Iterator<OWLClassExpression> it = sx.iterator();
		parseClasses(it.next(),it.next(),prov.get1(),MappingRelation.INCOMPATIBLE,prov.get2(),prov.get3());
	}
	
	private void parse(OWLEquivalentDataPropertiesAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		Stream<OWLDataPropertyExpression> sx = x.properties();
		//Mappings cannot include more than two entities
		if(sx.count() != 2)
			return;
		Iterator<OWLDataPropertyExpression> it = sx.iterator();
		//The only type of DataPropertyExpression in OWL is DataProperty, so this should be safe
		addMapping(it.next().asOWLDataProperty().toStringID(),it.next().asOWLDataProperty().toStringID(),
				prov.get1(), MappingRelation.EQUIVALENCE, prov.get2(), prov.get3());
	}
	
	private void parse(OWLSubDataPropertyOfAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		//The only type of DataPropertyExpression in OWL is DataProperty, so this should be safe
		addMapping(x.getSubProperty().asOWLDataProperty().toStringID(),x.getSuperProperty().asOWLDataProperty().toStringID(),
				prov.get1(), MappingRelation.SUBSUMED_BY, prov.get2(), prov.get3());
	}
	
	private void parse(OWLDisjointDataPropertiesAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		Stream<OWLDataPropertyExpression> sx = x.properties();
		//Mappings cannot include more than two entities
		if(sx.count() != 2)
			return;
		Iterator<OWLDataPropertyExpression> it = sx.iterator();
		//The only type of DataPropertyExpression in OWL is DataProperty, so this should be safe
		addMapping(it.next().asOWLDataProperty().toStringID(),it.next().asOWLDataProperty().toStringID(),
				prov.get1(), MappingRelation.INCOMPATIBLE, prov.get2(), prov.get3());
	}
	
	private void parse(OWLEquivalentObjectPropertiesAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		Stream<OWLObjectPropertyExpression> sx = x.properties();
		//Mappings cannot include more than two entities
		if(sx.count() != 2)
			return;
		Iterator<OWLObjectPropertyExpression> it = sx.iterator();
		parseObjectProperties(it.next(),it.next(),prov.get1(),MappingRelation.EQUIVALENCE,prov.get2(),prov.get3());
	}
	
	private void parse(OWLSubObjectPropertyOfAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		parseObjectProperties(x.getSubProperty(),x.getSuperProperty(),prov.get1(),MappingRelation.SUBSUMED_BY,prov.get2(),prov.get3());
	}
	
	private void parse(OWLDisjointObjectPropertiesAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		Stream<OWLObjectPropertyExpression> sx = x.properties();
		//Mappings cannot include more than two entities
		if(sx.count() != 2)
			return;
		Iterator<OWLObjectPropertyExpression> it = sx.iterator();
		parseObjectProperties(it.next(),it.next(),prov.get1(),MappingRelation.INCOMPATIBLE,prov.get2(),prov.get3());
	}
	
	private void parse(OWLSameIndividualAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		Stream<OWLIndividual> sx = x.individuals();
		//Mappings cannot include more than two entities
		if(sx.count() != 2)
			return;
		Iterator<OWLIndividual> it = sx.iterator();
		//We assume that sameAs aren't declared for anonymous individuals
		addMapping(it.next().asOWLNamedIndividual().toStringID(),it.next().asOWLNamedIndividual().toStringID(),
				prov.get1(), MappingRelation.EQUIVALENCE, prov.get2(), prov.get3());		
	}
	
	private void parse(OWLDifferentIndividualsAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		Stream<OWLIndividual> sx = x.individuals();
		//Mappings cannot include more than two entities
		if(sx.count() != 2)
			return;
		Iterator<OWLIndividual> it = sx.iterator();
		//We assume that sameAs aren't declared for anonymous individuals
		addMapping(it.next().asOWLNamedIndividual().toStringID(),it.next().asOWLNamedIndividual().toStringID(),
				prov.get1(), MappingRelation.INCOMPATIBLE, prov.get2(), prov.get3());
	}

	private void parse(OWLClassAssertionAxiom x) throws EncodingException
	{
		//Check the annotations to make sure x is a mapping
		Triple<Double,String,MappingStatus> prov;
		try
		{
			prov = parseAnnotation(x);
		}
		catch(EncodingException e)
		{
			return;
		}
		addMapping(x.getIndividual().asOWLNamedIndividual().toStringID(),x.getClassExpression().asOWLClass().toStringID(),
				prov.get1(), MappingRelation.INCOMPATIBLE, prov.get2(), prov.get3());		
	}

	private Triple<Double,String,MappingStatus> parseAnnotation(OWLAxiom x) throws EncodingException
	{
		double sim = 0.0;
		String prov = "";
		MappingStatus m = MappingStatus.UNREVISED;
		boolean check = false;
		HashSet<OWLAnnotation> annots = new HashSet<OWLAnnotation>();
		x.annotations().forEach(annots::add);
		for(OWLAnnotation an : annots)
		{
			OWLAnnotationProperty ap = an.getProperty();
			if(ap.toStringID().equals(MEASURE))
			{
				sim = an.getValue().asLiteral().get().parseDouble();
				check = true;
			}
			else if(ap.toStringID().equals(PROVENANCE))
			{
				String[] value = an.getValue().asLiteral().get().getLiteral().split("; ");
				if(value.length == 3)
				{
					prov = value[1];
					m = MappingStatus.parseStatus(value[2]);
				}
				check = true;
			}
		}
		if(!check)
			throw new EncodingException("Not a mapping");
		return new Triple<Double,String,MappingStatus>(sim,prov,m);
	}
	
	private void parseClasses(OWLClassExpression c1, OWLClassExpression c2, double sim, MappingRelation rel, String prov, MappingStatus s) throws EncodingException
	{
		String src, tgt;
		if(c1.isOWLClass())
			src = c1.asOWLClass().toStringID();
		else
		{
			ClassExpression ce = OWLExpressionParser.parse(c1);
			if(ce == null)
				return;
			src = ce.toString();
			sm.addExpression(ce);
		}
		if(c2.isOWLClass())
			tgt = c2.asOWLClass().toStringID();
		else
		{
			ClassExpression ce = OWLExpressionParser.parse(c2);
			if(ce == null)
				return;
			if(rel.equals(MappingRelation.SUBSUMED_BY) && ce instanceof ObjectSomeValues && ce.getComponents().get(0).toString().equals(RELATED))
			{
				tgt = ce.getComponents().get(1).toString();
				rel = MappingRelation.RELATED;
			}
			else
			{
				tgt = ce.toString();
				sm.addExpression(ce);
			}
		}
		addMapping(src,tgt,sim,rel,prov,s);
	}
	
	private void parseObjectProperties(OWLObjectPropertyExpression c1, OWLObjectPropertyExpression c2, double sim, MappingRelation rel, String prov, MappingStatus s) throws EncodingException
	{
		String src, tgt;
		if(c1.isOWLObjectProperty())
			src = c1.asOWLObjectProperty().toStringID();
		else
		{
			ObjectPropertyExpression ce = OWLExpressionParser.parse(c1);
			if(ce == null)
				return;
			src = ce.toString();
			sm.addExpression(ce);
		}
		if(c2.isOWLObjectProperty())
			tgt = c2.asOWLObjectProperty().toStringID();
		else
		{
			ObjectPropertyExpression ce = OWLExpressionParser.parse(c2);
			if(ce == null)
				return;
			tgt = ce.toString();
			sm.addExpression(ce);
		}
		addMapping(src,tgt,sim,rel,prov,s);
	}
}