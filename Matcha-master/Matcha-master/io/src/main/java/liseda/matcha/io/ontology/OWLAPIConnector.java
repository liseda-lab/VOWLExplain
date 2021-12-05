/******************************************************************************
* An interface with the OWL API.                                              *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.ontology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.data.Map2Map;
import liseda.matcha.ontology.lexicon.LexicalType;
import liseda.matcha.util.StringParser;
import liseda.matcha.vocabulary.OBO;
import liseda.matcha.vocabulary.SKOS;

public class OWLAPIConnector
{

//Attributes

	private OWLOntologyManager manager;
	private OWLDataFactory factory;
	private OWLOntologyLoaderConfiguration conf;
	private OWLOntology o;
	private Set<OWLOntology> imports;
	private static final String[] LIMIT = {"entityExpansionLimit", "1000000"};

//Constructors
	
	public OWLAPIConnector()
	{
		//Increase the entity expansion limit to allow large ontologies
		System.setProperty(LIMIT[0], LIMIT[1]);
		this.manager = OWLManager.createOWLOntologyManager();
		this.factory = manager.getOWLDataFactory();
		this.conf = new OWLOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		this.manager.setOntologyLoaderConfiguration(conf);
	}
	
//Public Methods

	public void addAxiom(OWLAxiom a)
	{
		if(!o.containsAxiom(a))
			manager.addAxiom(o,a);
	}
	
	public void addDisjoint(OWLClassExpression c1, OWLClassExpression c2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLDisjointClassesAxiom(c1, c2, annot));
	}

	public void addDisjoint(OWLDataProperty p1, OWLDataProperty p2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLDisjointDataPropertiesAxiom(p1, p2, annot));
	}
	
	public void addDisjoint(OWLObjectPropertyExpression p1, OWLObjectPropertyExpression p2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLDisjointObjectPropertiesAxiom(p1, p2, annot));
	}

	public void addDisjoint(OWLNamedIndividual i1, OWLNamedIndividual i2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLDifferentIndividualsAxiom(i1, i2, annot));
	}

	public void addEntity(OWLEntity e)
	{
		addAxiom(factory.getOWLDeclarationAxiom(e));
	}
	
	public void addEquivalent(OWLClassExpression e1, OWLClassExpression e2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLEquivalentClassesAxiom(e1, e2, annot));
	}
	
	public void addEquivalent(OWLDataProperty e1, OWLDataProperty e2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLEquivalentDataPropertiesAxiom(e1, e2, annot));
	}
	
	public void addEquivalent(OWLObjectPropertyExpression e1, OWLObjectPropertyExpression e2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLEquivalentObjectPropertiesAxiom(e1, e2, annot));
	}

	public void addEquivalent(OWLNamedIndividual e1, OWLNamedIndividual e2, Collection<OWLAnnotation> annot)
	{	
			addAxiom(factory.getOWLSameIndividualAxiom(e1, e2, annot));
	}

	public void addImports(Alignment a)
	{
		IRI src = IRI.create(StringParser.toURI(a.getSourceLocation()));
		OWLImportsDeclaration i1 = factory.getOWLImportsDeclaration(src);
		manager.applyChange(new AddImport(o, i1));
		IRI tgt = IRI.create(StringParser.toURI(a.getTargetLocation()));
		OWLImportsDeclaration i2 = factory.getOWLImportsDeclaration(tgt);
		manager.applyChange(new AddImport(o, i2));
	}
	
	public void addInstance(OWLNamedIndividual i, OWLClass c, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLClassAssertionAxiom(c, i, annot));
	}
	
	public void addObjectAssertion(OWLIndividual i1, OWLObjectPropertyExpression op, OWLIndividual i2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLObjectPropertyAssertionAxiom(op, i1, i2, annot));
	}
	
	public void addObjectSomeValues(OWLClassExpression c1, OWLObjectPropertyExpression op, OWLClassExpression c2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLSubClassOfAxiom(c1, factory.getOWLObjectSomeValuesFrom(op, c2), annot));
	}
	
	public void addReflexive(OWLObjectPropertyExpression op)
	{
		addAxiom(factory.getOWLReflexiveObjectPropertyAxiom(op));
	}
	
	public void addSubsumption(OWLClassExpression e1, OWLClassExpression e2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLSubClassOfAxiom(e1, e2, annot));
	}
	
	public void addSubsumption(OWLDataProperty e1, OWLDataProperty e2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLSubDataPropertyOfAxiom(e1, e2, annot));
	}
	
	public void addSubsumption(OWLObjectPropertyExpression e1, OWLObjectPropertyExpression e2, Collection<OWLAnnotation> annot)
	{
		addAxiom(factory.getOWLSubObjectPropertyOfAxiom(e1, e2, annot));
	}
	
	public boolean containsAnnotationProperty(IRI iri)
	{
		return o.containsAnnotationPropertyInSignature(iri);
	}
	
	public boolean containsClass(IRI iri)
	{
		return o.containsClassInSignature(iri);
	}
	
	public boolean containsDataProperty(IRI iri)
	{
		return o.containsDataPropertyInSignature(iri);
	}
	
	public boolean containsNamedIndividual(IRI iri)
	{
		return o.containsIndividualInSignature(iri);
	}
	
	public boolean containsObjectProperty(IRI iri)
	{
		return o.containsObjectPropertyInSignature(iri);
	}

	public void createOntology(IRI iri) throws OWLOntologyCreationException
	{
		o = manager.createOntology(iri);
	}
	
	public OWLAnnotation getAnnotation(String prop, double val)
	{
		return factory.getOWLAnnotation(getAnnotationProperty(prop), factory.getOWLLiteral(val));
	}

	public OWLAnnotation getAnnotation(String prop, String val)
	{
		return factory.getOWLAnnotation(getAnnotationProperty(prop), factory.getOWLLiteral(val));
	}

	public OWLAnnotationProperty getAnnotationProperty(String iri)
	{
		return factory.getOWLAnnotationProperty(IRI.create(iri));
	}

	public Set<OWLAnonymousIndividual> getAnonymousIndividuals()
	{
		HashSet<OWLAnonymousIndividual> anon = new HashSet<OWLAnonymousIndividual>();
		o.anonymousIndividuals().forEach(anon::add);
		return anon;
	}
	
	public HashSet<String> getCrossReferences(OWLEntity e)
	{
		HashSet<String> refs = new HashSet<String>();
		HashSet<OWLAnnotation> aux = new HashSet<OWLAnnotation>();
		OWLAnnotationProperty xref = getAnnotationProperty(OBO.XREF.toURI());
		if(o.containsAnnotationPropertyInSignature(xref.getIRI()))
		{
			EntitySearcher.getAnnotationObjects(e, o, xref).forEach(aux::add);
			for(OWLOntology ont : imports)
				EntitySearcher.getAnnotationObjects(e, ont, xref).forEach(aux::add);
			for(OWLAnnotation a : aux)
			{
				if(a.getValue().asLiteral().isPresent())
					refs.add(a.getValue().asLiteral().get().getLiteral());
			}
		}
		return refs;
	}

	public OWLClass getClass(String iri)
	{
		return factory.getOWLClass(IRI.create(iri));
	}
	
	public Set<OWLIndividual> getClassIndividualsTransitive(OWLClass c)
	{
		HashSet<OWLIndividual> indivs = new HashSet<OWLIndividual>();
		EntitySearcher.getIndividuals(c, o).forEach(indivs::add);
		//Do the same for the instances of its subclasses
		HashSet<OWLClassExpression> cl = new HashSet<OWLClassExpression>();
		EntitySearcher.getSubClasses(c, o).forEach(cl::add);
		for(OWLClassExpression ce : cl)
			if(ce instanceof OWLClass)
				EntitySearcher.getIndividuals(ce.asOWLClass(), o).forEach(indivs::add);
		return indivs;
	}
	
	public Set<OWLClass> getClasses()
	{
		HashSet<OWLClass> classes = new HashSet<OWLClass>();
		o.classesInSignature(Imports.INCLUDED).forEach(classes::add);
		return classes;
	}
	
	public Set<OWLClassExpression> getDataDomains(OWLDataProperty dp)
	{
		HashSet<OWLClassExpression> domains = new HashSet<OWLClassExpression>();
		EntitySearcher.getDomains(dp, o).forEach(domains::add);
		return domains;
	}
	
	public Set<OWLDataProperty> getDataProperties()
	{
		HashSet<OWLDataProperty> dProps = new HashSet<OWLDataProperty>();
		o.dataPropertiesInSignature(Imports.INCLUDED).forEach(dProps::add);
		return dProps;
	}
	
	public OWLDataProperty getDataProperty(String iri)
	{
		return factory.getOWLDataProperty(IRI.create(iri));
	}
	
	public Set<OWLDataRange> getDataRanges(OWLDataProperty dp)
	{
		HashSet<OWLDataRange> ranges = new HashSet<OWLDataRange>();
		EntitySearcher.getRanges(dp, o).forEach(ranges::add);
		return ranges;
	}
	
	public Set<OWLClassExpression> getDisjointClasses(OWLClass c)
	{
		HashSet<OWLClassExpression> disjClasses = new HashSet<OWLClassExpression>();
		EntitySearcher.getDisjointClasses(c,o).forEach(disjClasses::add);
		for(OWLOntology ont : imports)
			EntitySearcher.getDisjointClasses(c,ont).forEach(disjClasses::add);
		disjClasses.remove(c);
		return disjClasses;
	}
	
	public Set<OWLClassExpression> getEquivalentClasses(OWLClass c)
	{
		HashSet<OWLClassExpression> eq = new HashSet<OWLClassExpression>();
		EntitySearcher.getEquivalentClasses(c, o).forEach(eq::add);
		for(OWLOntology ont : imports)
			EntitySearcher.getEquivalentClasses(c, ont).forEach(eq::add);
		return eq;
	}

	public Set<OWLEquivalentClassesAxiom> getEquivalentClassAxioms()
	{
		HashSet<OWLEquivalentClassesAxiom> eq = new HashSet<OWLEquivalentClassesAxiom>();
		o.axioms(AxiomType.EQUIVALENT_CLASSES, Imports.INCLUDED).forEach(eq::add);
		return eq;
	}
	
	/**
	 * @return the OWLDataFactory
	 */
	public OWLDataFactory getFactory()
	{
		return factory;
	}
	
	public Set<OWLClass> getIndividualNonRedundantTypes(OWLIndividual i)
	{
		HashSet<OWLClassExpression> types = new HashSet<OWLClassExpression>();
		HashSet<OWLClass> nonRedundant = new HashSet<OWLClass>();
		EntitySearcher.getTypes(i,o).forEach(types::add);
		for(OWLOntology ont : imports)
			EntitySearcher.getTypes(i,ont).forEach(types::add);
		for(OWLClassExpression c : types)
		{
			if(!c.isOWLClass())
				continue;
			//Only add a type if no subclass is declared as a type for that individual
			Set<OWLClassExpression> sub = getSubClasses(c.asOWLClass());
			sub.retainAll(types);
			if(sub.size() == 0)
				nonRedundant.add(c.asOWLClass());
		}
		return nonRedundant;
	}
	
	public Set<OWLObjectPropertyExpression> getInverseProperties(OWLObjectProperty op)
	{
		HashSet<OWLObjectPropertyExpression> invProps = new HashSet<OWLObjectPropertyExpression>();
		EntitySearcher.getInverses(op,o).forEach(invProps::add);
		for(OWLOntology ont : imports)
			EntitySearcher.getInverses(op,ont).forEach(invProps::add);
		return invProps;
	}
	
	public Map2Map<LexicalType,String,String> getLexicalAnnotations(OWLEntity e)
	{
		Map2Map<LexicalType,String,String> annots = new Map2Map<LexicalType,String,String>();
		HashSet<OWLAnnotation> aux = new HashSet<OWLAnnotation>();
		OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		EntitySearcher.getAnnotations(e, o).forEach(aux::add);
		for(OWLOntology ont : imports)
			EntitySearcher.getAnnotations(e, ont).forEach(aux::add);
		for(OWLAnnotation a : aux)
		{
			LexicalType t = LexicalType.getLexicalType(a.getProperty().toStringID());
			if(t == null)
				continue;
			if(a.getValue() instanceof OWLLiteral)
			{
				OWLLiteral val = a.getValue().asLiteral().get();
				annots.add(t, val.getLiteral(), val.getLang());
			}
			//Some ontologists like to get creative and use individuals to represent synonyms, in
			//which case the label of those individuals will be treated as the true annotation value
			else if(a.getValue().isIRI() && o.containsIndividualInSignature(a.getValue().asIRI().get()))
			{
				OWLNamedIndividual ni = factory.getOWLNamedIndividual(a.getValue().asIRI().get());
				HashSet<OWLAnnotation> annot2 = new HashSet<OWLAnnotation>();
				EntitySearcher.getAnnotationObjects(ni,o,label).forEach(annot2::add);
				for(OWLAnnotation an : annot2)
				{
					if(an.getValue() instanceof OWLLiteral)
					{
						OWLLiteral val = an.getValue().asLiteral().get();
						annots.add(t, val.getLiteral(), val.getLang());
					}
				}
			}
		}
		return annots;
	}
	
	public OWLNamedIndividual getNamedIndividual(String iri)
	{
		return factory.getOWLNamedIndividual(IRI.create(iri));
	}
	
	public Set<OWLNamedIndividual> getNamedIndividuals()
	{
		HashSet<OWLNamedIndividual> indivs = new HashSet<OWLNamedIndividual>();
		o.individualsInSignature(Imports.INCLUDED).forEach(indivs::add);
		return indivs;
	}

	public Set<OWLClassExpression> getObjectDomains(OWLObjectProperty op)
	{
		HashSet<OWLClassExpression> domains = new HashSet<OWLClassExpression>();
		EntitySearcher.getDomains(op, o).forEach(domains::add);
		return domains;
	}
	
	public Set<OWLObjectProperty> getObjectProperties()
	{
		HashSet<OWLObjectProperty> oProps = new HashSet<OWLObjectProperty>();
		o.objectPropertiesInSignature(Imports.INCLUDED).forEach(oProps::add);
		return oProps;
	}
	
	public OWLObjectProperty getObjectProperty(String iri)
	{
		return factory.getOWLObjectProperty(IRI.create(iri));
	}

	public Set<OWLClassExpression> getObjectRanges(OWLObjectProperty op)
	{
		HashSet<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
		EntitySearcher.getRanges(op, o).forEach(ranges::add);
		return ranges;
	}
	

	public OWLOntology getOntology()
	{
		return o;
	}
	
	public IRI getOntologyIRI()
	{
		return o.getOntologyID().getOntologyIRI().orElse(null);
	}
	
	public Set<OWLIndividual> getRelatedIndividuals(IRI individual, IRI relation)
	{
		HashSet<OWLIndividual> indivs = new HashSet<OWLIndividual>();
		if(o.containsIndividualInSignature(individual))
		{
			OWLNamedIndividual i = factory.getOWLNamedIndividual(individual);
			//If the ontology lists the object property, we can process it normally
			if(o.containsObjectPropertyInSignature(relation))
			{
				OWLObjectProperty op = factory.getOWLObjectProperty(relation);
				EntitySearcher.getObjectPropertyValues(i, op, o).forEach(indivs::add);
			}
			//But if it doesn't, it probably registers as an annotation property
			else if(o.containsAnnotationPropertyInSignature(relation))
			{
				OWLAnnotationProperty ap = factory.getOWLAnnotationProperty(relation);
				HashSet<OWLAnnotation> aux = new HashSet<OWLAnnotation>();
				EntitySearcher.getAnnotationObjects(i, o, ap).forEach(aux::add);
				for(OWLAnnotation a : aux)
					if(a.getValue().isIRI() && o.containsIndividualInSignature(a.getValue().asIRI().get()))
						indivs.add(factory.getOWLNamedIndividual(a.getValue().asIRI().get()));
			}
		}
		return indivs;
	}
	
	public Set<OWLIndividual> getSameAsIndividuals(OWLIndividual i)
	{
		HashSet<OWLIndividual> same = new HashSet<OWLIndividual>();
		EntitySearcher.getSameIndividuals(i,o).forEach(same::add);
		for(OWLOntology ont : imports)
			EntitySearcher.getSameIndividuals(i,ont).forEach(same::add);
		return same;
	}
	
	public Set<OWLClassExpression> getSubClasses(OWLClass c)
	{
		HashSet<OWLClassExpression> superClasses = new HashSet<OWLClassExpression>();
		EntitySearcher.getSubClasses(c,o).forEach(superClasses::add);
		for(OWLOntology ont : imports)
			EntitySearcher.getSubClasses(c,ont).forEach(superClasses::add);
		return superClasses;
	}
	
	public Set<OWLSubPropertyChainOfAxiom> getSubPropertyChainAxioms(OWLObjectProperty op)
	{
		HashSet<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		EntitySearcher.getReferencingAxioms(op, o).forEach(axioms::add);
		for(OWLOntology ont : imports)
			EntitySearcher.getReferencingAxioms(op, ont).forEach(axioms::add);
		HashSet<OWLSubPropertyChainOfAxiom> chain = new HashSet<OWLSubPropertyChainOfAxiom>();
		for(OWLAxiom a : axioms)
			if(a.isOfType(AxiomType.SUB_PROPERTY_CHAIN_OF))
				chain.add((OWLSubPropertyChainOfAxiom)a);
		return chain;
	}
	
	public Set<OWLClassExpression> getSuperClasses(OWLClass c)
	{
		HashSet<OWLClassExpression> superClasses = new HashSet<OWLClassExpression>();
		EntitySearcher.getSuperClasses(c,o).forEach(superClasses::add);
		for(OWLOntology ont : imports)
			EntitySearcher.getSuperClasses(c,ont).forEach(superClasses::add);
		return superClasses;
	}
	
	public Set<OWLDataPropertyExpression> getSuperProperties(OWLDataProperty dp)
	{
		Set<OWLDataPropertyExpression> sProps = new HashSet<OWLDataPropertyExpression>();
		EntitySearcher.getSuperProperties(dp,o);
		for(OWLOntology ont : imports)
			EntitySearcher.getSuperProperties(dp,ont);
		return sProps;
	}
	
	public Set<OWLObjectPropertyExpression> getSuperProperties(OWLObjectProperty op)
	{
		Set<OWLObjectPropertyExpression> sProps = new HashSet<OWLObjectPropertyExpression>();
		EntitySearcher.getSuperProperties(op,o);
		for(OWLOntology ont : imports)
			EntitySearcher.getSuperProperties(op,ont);
		return sProps;
	}
	
	public boolean hasType(OWLIndividual i)
	{
		return EntitySearcher.getTypes(i, o).count() > 0; //TODO: check that this doesn't return owl:thing
	}
	
	public boolean isDeprecated(OWLEntity e)
	{
		OWLAnnotationProperty deprecated = factory.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_DEPRECATED.getIRI());
		HashSet<OWLAnnotation> aux = new HashSet<OWLAnnotation>();
		EntitySearcher.getAnnotationObjects(e, o, deprecated).forEach(aux::add);
		for(OWLAnnotation a : aux)
			if(a.isDeprecatedIRIAnnotation())
				return true;
		return false;
	}
	
	public boolean isSKOS()
	{
		return o.containsClassInSignature(IRI.create(SKOS.CONCEPT_SCHEME.toURI())) &&
				o.containsClassInSignature(IRI.create(SKOS.CONCEPT.toURI()));
	}
	
	/**
	 * Reads an OWLOntology from a local file
	 * @param path: the path to the file to parse
	 * @return the OWLOntology encoding the ontology/thesaurus in the file
	 * @throws OWLOntologyCreationException
	 */
	public OWLOntology openOntology(String path) throws OWLOntologyCreationException
	{
		URI p = StringParser.toURI(path);
		if(p == null)
			throw new OWLOntologyCreationException("File not found: " + path);
		return openOntology(p);
	}

	/**
	 * Reads an OWLOntology from a URI location
	 * @param uri: the URI to the ontology/thesaurus to parse
	 * @return the OWLOntology encoding the ontology/thesaurus in the file
	 * @throws OWLOntologyCreationException
	 */
	public OWLOntology openOntology(URI uri) throws OWLOntologyCreationException
	{
		if(uri.toString().startsWith("file:"))
		{
			File f = new File(uri);
			o = manager.loadOntologyFromOntologyDocument(f);
		}
		else
		{
			IRI i = IRI.create(uri);
			o = manager.loadOntology(i);
		}
		imports = new HashSet<OWLOntology>();
		o.imports().forEach(imports::add);
		//Return the ontology
		return o;
	}
	
	public void saveOntology(String file) throws OWLOntologyStorageException, FileNotFoundException
	{
		o.saveOntology(new FileOutputStream(file));
	}
}