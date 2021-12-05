/******************************************************************************
* MappingReader for RDF mappings in Alignment/EDOAL syntax.                   *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.alignment.rdf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.dom4j.Element;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.LinkKeyMapping;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.alignment.TransformationMapping;
import liseda.matcha.io.EncodingException;
import liseda.matcha.io.alignment.MappingReader;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.edoal.Aggregate;
import liseda.matcha.semantics.edoal.Apply;
import liseda.matcha.semantics.edoal.Comparator;
import liseda.matcha.semantics.edoal.DataPropertyChain;
import liseda.matcha.semantics.edoal.DataPropertyDomain;
import liseda.matcha.semantics.edoal.DataPropertyIntersection;
import liseda.matcha.semantics.edoal.DataPropertyNegation;
import liseda.matcha.semantics.edoal.DataPropertyRangeType;
import liseda.matcha.semantics.edoal.DataPropertyRangeValue;
import liseda.matcha.semantics.edoal.DataPropertyUnion;
import liseda.matcha.semantics.edoal.LinkKey;
import liseda.matcha.semantics.edoal.ObjectPropertyDomain;
import liseda.matcha.semantics.edoal.ObjectPropertyIntersection;
import liseda.matcha.semantics.edoal.ObjectPropertyNegation;
import liseda.matcha.semantics.edoal.ObjectPropertyRange;
import liseda.matcha.semantics.edoal.PropertyComparatorValue;
import liseda.matcha.semantics.edoal.ReflexiveProperty;
import liseda.matcha.semantics.edoal.SymmetricProperty;
import liseda.matcha.semantics.edoal.Transformation;
import liseda.matcha.semantics.edoal.TransitiveProperty;
import liseda.matcha.semantics.owl.Cardinality;
import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.ClassIntersection;
import liseda.matcha.semantics.owl.ClassNegation;
import liseda.matcha.semantics.owl.ClassUnion;
import liseda.matcha.semantics.owl.DataExactCardinality;
import liseda.matcha.semantics.owl.DataHasValue;
import liseda.matcha.semantics.owl.DataMaxCardinality;
import liseda.matcha.semantics.owl.DataMinCardinality;
import liseda.matcha.semantics.owl.DataPropertyExpression;
import liseda.matcha.semantics.owl.DataSomeValues;
import liseda.matcha.semantics.owl.Datatype;
import liseda.matcha.semantics.owl.Individual;
import liseda.matcha.semantics.owl.InverseProperty;
import liseda.matcha.semantics.owl.Literal;
import liseda.matcha.semantics.owl.ObjectAllValues;
import liseda.matcha.semantics.owl.ObjectExactCardinality;
import liseda.matcha.semantics.owl.ObjectHasValue;
import liseda.matcha.semantics.owl.ObjectMaxCardinality;
import liseda.matcha.semantics.owl.ObjectMinCardinality;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;
import liseda.matcha.semantics.owl.ObjectSomeValues;
import liseda.matcha.semantics.owl.PropertyExpression;
import liseda.matcha.semantics.owl.SimpleClass;
import liseda.matcha.semantics.owl.SimpleDataProperty;
import liseda.matcha.semantics.owl.SimpleObjectProperty;
import liseda.matcha.semantics.owl.ValueExpression;
import liseda.matcha.util.StringParser;
import liseda.matcha.vocabulary.RDFElement;

public class RDFMappingReader implements MappingReader<Element>
{

//Attributes
	
	private Alignment a;
	private boolean reverse;
	
//Constructors
	
	public RDFMappingReader(Alignment a, boolean reverse)
	{
		this.a = a;
		this.reverse = reverse;
	}
	
	
//Public Methods
	
	public void readMapping(Element e) throws EncodingException
	{
       	SemanticMap sm = SemanticMap.getInstance();
        String sourceURI, targetURI;
		//Get the entities
		Element entity1 = e.element(RDFElement.ENTITY1.toString());
		Element entity2 = e.element(RDFElement.ENTITY2.toString());
        if(!entity1.hasContent())
        {
			sourceURI = StringParser.decodeURL(entity1.attributeValue(RDFElement.RDF_RESOURCE.toString()));
			if(sourceURI == null)
				throw new EncodingException("ERROR: Missing alignment entity!\n" + entity1.asXML());
        }
        else
        {
            Expression e1 = parseEntity(entity1);
            sm.addExpression(e1);
            sourceURI = e1.toString();
        }
        if(!entity2.hasContent())
        {
			targetURI = StringParser.decodeURL(entity2.attributeValue(RDFElement.RDF_RESOURCE.toString()));
			if(targetURI == null)
				throw new EncodingException("ERROR: Missing alignment entity!\n" + entity2.asXML());
        }
        else
        {
            Expression e2 = parseEntity(entity2);
            sm.addExpression(e2);
            targetURI = e2.toString();            
        }
		//Get the similarity measure
		String measure = e.elementText(RDFElement.MEASURE.toString());
		//Parse it, assuming 1 if a valid measure is not found
		double similarity = 1;
		if(measure != null)
		{
			try{ similarity = Double.parseDouble(measure); }
        	catch(Exception ex){/*Do nothing - use the default value*/};
        }
        if(similarity < 0 || similarity > 1)
        	similarity = 1;
        //Get the relation
        String r = e.elementText(RDFElement.RELATION.toString());
        if(r == null)
        	r = "=";
        MappingRelation rel = MappingRelation.parseRelation(r);
        //Get the provenance (an extension to the EDOAL format used by AML)
        String s = e.elementText(RDFElement.PROVENANCE.toString());
        if(s == null)
        	s = "?";
        MappingStatus st = MappingStatus.parseStatus(s);
        
		//Check for transformations (of which there can be any number) and linkkeys
		List<Element> transform = e.elements(RDFElement.TRANSFORMATION.toString());
		//If there are transformations, parse them
		if(transform != null && !transform.isEmpty())
		{
			Set<Transformation> t = new HashSet<Transformation>();
			for(Element f : transform)
				t.add(parseTransformation(f,reverse));
			//Check that the entities are class expressions
			if(!(sm.getExpression(sourceURI) instanceof ClassExpression && sm.getExpression(targetURI) instanceof ClassExpression))
				throw new EncodingException("ERROR: Transformation mapping can only apply to class expressions!\n" + e.asXML());
	        if(reverse)
	        	a.add(new TransformationMapping(targetURI,sourceURI,similarity,rel,s,st,t));
	        else
	        	a.add(new TransformationMapping(sourceURI,targetURI,similarity,rel,s,st,t));
		}
		//Otherwise, if there are linkkeys, parse them (there shouldn't be both transformations and linkkeys)
		Element key = e.element(RDFElement.LINKKEY.toString());
		if(key != null)
		{
			LinkKey l = parseLinkKey(key,reverse);
			//Check that the entities are class expressions
			if(!(sm.getExpression(sourceURI) instanceof ClassExpression && sm.getExpression(targetURI) instanceof ClassExpression))
				throw new EncodingException("ERROR: Transformation mapping can only apply to class expressions!\n" + e.asXML());
	        if(reverse)
	        	a.add(new LinkKeyMapping(targetURI,sourceURI,similarity,rel,s,st,l));
	        else
	        	a.add(new LinkKeyMapping(sourceURI,targetURI,similarity,rel,s,st,l));
		}				
		//Otherwise return a normal Mapping
        if(reverse)
        	a.add(new Mapping(targetURI,sourceURI,similarity,rel,s,st));
        else
        	a.add(new Mapping(sourceURI,targetURI,similarity,rel,s,st));
	}
	
//Private Methods
	
	private Expression parseEntity(Element e) throws EncodingException
	{
		List<Element> sub = e.elements();
		if(sub.isEmpty())
		{
			String uri = StringParser.decodeURL(e.attributeValue(RDFElement.RDF_RESOURCE.toString()));
			if(uri == null)
				throw new EncodingException("ERROR: Missing alignment entity!\n" + e.asXML());
			Set<EntityType> t = SemanticMap.getInstance().getTypes(uri);
			if(t.contains(EntityType.CLASS))
				return new SimpleClass(uri);
			else if(t.contains(EntityType.OBJECT_PROP))
				return new SimpleObjectProperty(uri);
			else if(t.contains(EntityType.DATA_PROP))
				return new SimpleDataProperty(uri);
			else if(t.contains(EntityType.INDIVIDUAL))
				return new Individual(uri);
			else
				throw new EncodingException("ERROR: Unknown entity type!\n" + e.asXML());
		}
		//An EDOAL entity always corresponds to a single XML element (which may contain several sub-elements)
		else if(sub.size() == 1)
		{
			return parseEDOALEntity(sub.get(0));
		}
		return null;
	}

	private Expression parseEDOALEntity(Element e) throws EncodingException
	{
		Expression a = null;
		//Parse it according to its name
		if(e.getName().equals(RDFElement.CLASS_.toString()))
			a = parseClass(e);
		else if(e.getName().equals(RDFElement.PROPERTY_.toString()))
			a = parseProperty(e);
		else if(e.getName().equals(RDFElement.RELATION_.toString()))
			a = parseObjectProperty(e);
		else if(e.getName().equals(RDFElement.INSTANCE_.toString()))
			a = parseInstance(e);
		else if(e.getName().equals(RDFElement.ATTR_DOMAIN_REST_.toString()))
			a = parseADR(e);
		else if(e.getName().equals(RDFElement.ATTR_OCCURRENCE_REST_.toString()))
			a = parseAOR(e);
		else if(e.getName().equals(RDFElement.ATTR_TYPE_REST_.toString()))
			a = parseATR(e);
		else if(e.getName().equals(RDFElement.ATTR_VALUE_REST_.toString()))
			a = parseAVR(e);
		else if(e.getName().equals(RDFElement.PROPERTY_DOMAIN_REST_.toString()))
			a = parsePDR(e);
		else if(e.getName().equals(RDFElement.PROPERTY_TYPE_REST_.toString()))
			a = parsePTR(e);
		else if(e.getName().equals(RDFElement.PROPERTY_VALUE_REST_.toString()))
			a = parsePVR(e);
		else if(e.getName().equals(RDFElement.RELATION_CODOMAIN_REST_.toString()))
			a = parseRCR(e);
		else if(e.getName().equals(RDFElement.RELATION_DOMAIN_REST_.toString()))
			a = parseRDR(e);
		else if(e.getName().equals(RDFElement.APPLY_.toString()))
			a = parseApply(e);
		else if(e.getName().equals(RDFElement.AGGREGATE_.toString()))
			a = parseAggregate(e);
		if(a == null)
			throw new EncodingException("ERROR: Could not parse entity!\n" + e.asXML());
		return a;
	}
	
	private ClassExpression parseClass(Element e) throws EncodingException
	{
		//<Class> nodes are either class ids (with no subnodes)
		List<Element> list = e.elements();
		if(list.isEmpty())
		{
			String uri = StringParser.decodeURL(e.attributeValue(RDFElement.RDF_ABOUT.toString()));
			if(uri != null)
				return new SimpleClass(uri);
		}
		//Or compositions (and, or, not)
		else if(list.size() == 1)
		{
			Element f = list.get(0);
			//Start by checking if it is a negation, which is simpler
			if(f.getName().equals(RDFElement.NOT.toString()))
			{
				List<Element> l = f.elements();
				if(l.size() != 1)
					return null;
				Expression a = parseEDOALEntity(l.get(0));
				if(a instanceof ClassExpression)
					return new ClassNegation((ClassExpression)a);
			}
			//Then intersection
			else if(f.getName().equals(RDFElement.AND.toString()))
			{
				List<Element> l = f.elements();
				if(l.isEmpty())
					return null;
				List<ClassExpression> composition = new Vector<ClassExpression>();
				for(Element g : l)
				{
					Expression a = parseEDOALEntity(g);
					if(a instanceof ClassExpression)
						composition.add((ClassExpression)a);
					else
						return null;
				}
				if(!composition.isEmpty())
					return new ClassIntersection(composition);
			}
			//Then union
			else if(f.getName().equals(RDFElement.OR.toString()))
			{
				List<Element> l = f.elements();
				if(l.isEmpty())
					return null;
				List<ClassExpression> composition = new Vector<ClassExpression>();
				for(Element g : l)
				{
					Expression a = parseEDOALEntity(g);
					if(a instanceof ClassExpression)
						composition.add((ClassExpression)a);
					else
						return null;
				}
				if(!composition.isEmpty())
					return new ClassUnion(composition);
			}
		}
		return null;
	}
	
	private DataPropertyExpression parseProperty(Element e) throws EncodingException
	{
		//<Property> nodes are either property ids (with no subnodes)
		List<Element> list = e.elements();
		if(list.isEmpty())
		{
			String uri = StringParser.decodeURL(e.attributeValue(RDFElement.RDF_ABOUT.toString()));
			//An EDOAL Property (OWL DataProperty) can include a language tag, but that makes no sense, so we ignore it
			//String lang = e.attributeValue(RDFElement.LANG.toString());
			if(uri != null)
				return new SimpleDataProperty(uri);
		}
		else if(list.size() == 1)
		{
			Element f = list.get(0);
			//Start by checking if it is a negation, which is simpler
			if(f.getName().equals(RDFElement.NOT.toString()))
			{
				List<Element> l = f.elements();
				if(l.size() != 1)
					return null;
				Expression a = parseEDOALEntity(l.get(0));
				if(a instanceof DataPropertyExpression)
					return new DataPropertyNegation((DataPropertyExpression)a);
			}
			//If it is not a negation, it should be a composition, intersection or union
			//Check for intersection first
			else if(f.getName().equals(RDFElement.AND.toString()))
			{
				List<Element> l = f.elements();
				if(l.isEmpty())
					return null;
				List<DataPropertyExpression> composition = new Vector<DataPropertyExpression>();
				for(Element g : l)
				{
					Expression a = parseEDOALEntity(g);
					if(a instanceof DataPropertyExpression)
						composition.add((DataPropertyExpression)a);
					else
						return null;
				}
				if(!composition.isEmpty())
					return new DataPropertyIntersection(composition);
			}
			//Then union
			else if(f.getName().equals(RDFElement.OR.toString()))
			{
				List<Element> l = f.elements();
				if(l.isEmpty())
					return null;
				List<DataPropertyExpression> composition = new Vector<DataPropertyExpression>();
				for(Element g : l)
				{
					Expression a = parseEDOALEntity(g);
					if(a instanceof DataPropertyExpression)
						composition.add((DataPropertyExpression)a);
					else
						return null;
				}
				if(!composition.isEmpty())
					return new DataPropertyUnion(composition);
			}
			//Then compose
			else if(f.getName().equals(RDFElement.COMPOSE.toString()))
			{
				List<Element> l = f.elements();
				if(l.size() < 2)
					return null;
				Vector<PropertyExpression> composition = new Vector<PropertyExpression>();
				for(int i = 0; i < l.size(); i++)
				{
					Expression a = parseEDOALEntity(l.get(i));
					if((i < l.size()-1 && a instanceof ObjectPropertyExpression) ||
							(i == l.size()-1 && a instanceof DataPropertyExpression))
						composition.add((PropertyExpression)a);
					else
						return null;
				}
				if(!composition.isEmpty())
					return new DataPropertyChain(composition);
			}
		}
		return null;
	}
	

	private ObjectPropertyExpression parseObjectProperty(Element e) throws EncodingException
	{
		//<ObjectProperty> nodes are either property ids (with no subnodes)
		List<Element> list = e.elements();
		if(list.isEmpty())
		{
			String uri = StringParser.decodeURL(e.attributeValue(RDFElement.RDF_ABOUT.toString()));
			if(uri != null)
				return new SimpleObjectProperty(uri);
		}
		//Or compositions (compose, and, or, not, inverse, reflexive, symmetric, transitive)
		else if(list.size() == 1)
		{
			Element f = list.get(0);
			//Start by checking the cases not involving a collection
			if(f.getName().equals(RDFElement.NOT.toString()))
			{
				List<Element> l = f.elements();
				if(l.size() != 1)
					return null;
				Expression a = parseEDOALEntity(l.get(0));
				if(a instanceof ObjectPropertyExpression)
					return new ObjectPropertyNegation((ObjectPropertyExpression)a);
			}
			else if(f.getName().equals(RDFElement.INVERSE.toString()))
			{
				List<Element> l = f.elements();
				if(l.size() != 1)
					return null;
				Expression a = parseEDOALEntity(l.get(0));
				if(a instanceof ObjectPropertyExpression)
					return new InverseProperty((ObjectPropertyExpression)a);
			}
			else if(f.getName().equals(RDFElement.REFLEXIVE.toString()))
			{
				List<Element> l = f.elements();
				if(l.size() != 1)
					return null;
				Expression a = parseEDOALEntity(l.get(0));
				if(a instanceof ObjectPropertyExpression)
					return new ReflexiveProperty((ObjectPropertyExpression)a);
			}
			else if(f.getName().equals(RDFElement.SYMMETRIC.toString()))
			{
				List<Element> l = f.elements();
				if(l.size() != 1)
					return null;
				Expression a = parseEDOALEntity(l.get(0));
				if(a instanceof ObjectPropertyExpression)
					return new SymmetricProperty((ObjectPropertyExpression)a);
			}
			else if(f.getName().equals(RDFElement.TRANSITIVE.toString()))
			{
				List<Element> l = f.elements();
				if(l.size() != 1)
					return null;
				Expression a = parseEDOALEntity(l.get(0));
				if(a instanceof ObjectPropertyExpression)
					return new TransitiveProperty((ObjectPropertyExpression)a);
			}
			//If it is not a negation, it should be a composition, intersection or union
			//Check for intersection first
			else if(f.getName().equals(RDFElement.AND.toString()))
			{
				List<Element> l = f.elements();
				if(l.isEmpty())
					return null;
				List<ObjectPropertyExpression> composition = new Vector<ObjectPropertyExpression>();
				for(Element g : l)
				{
					Expression a = parseEDOALEntity(g);
					if(a instanceof ObjectPropertyExpression)
						composition.add((ObjectPropertyExpression)a);
					else
						return null;
				}
				if(!composition.isEmpty())
					return new ObjectPropertyIntersection(composition);
			}
			else if(f.getName().equals(RDFElement.OR.toString()))
			{
				List<Element> l = f.elements();
				if(l.isEmpty())
					return null;
				List<ObjectPropertyExpression> composition = new Vector<ObjectPropertyExpression>();
				for(Element g : l)
				{
					Expression a = parseEDOALEntity(g);
					if(a instanceof ObjectPropertyExpression)
						composition.add((ObjectPropertyExpression)a);
					else
						return null;
				}
				if(!composition.isEmpty())
					return new ObjectPropertyIntersection(composition);
			}
			else if(f.getName().equals(RDFElement.COMPOSE.toString()))
			{
				List<Element> l = f.elements();
				if(l.isEmpty())
					return null;
				List<ObjectPropertyExpression> composition = new Vector<ObjectPropertyExpression>();
				for(Element g : l)
				{
					Expression a = parseEDOALEntity(g);
					if(a instanceof ObjectPropertyExpression)
						composition.add((ObjectPropertyExpression)a);
					else
						return null;
				}
				if(!composition.isEmpty())
					return new ObjectPropertyIntersection(composition);
			}
		}
		return null;
	}
	

	private ClassExpression parseADR(Element e) throws EncodingException
	{
		//ADRs should have exactly 2 elements: an onAttribute statement and a qualified class restriction,
		//each of which should consist of a single element
		List<Element> list = e.elements();
		if(list.size() == 2 && list.get(0).getName().equals(RDFElement.ON_ATTRIBUTE.toString()) &&
				list.get(0).elements().size() == 1 && list.get(1).elements().size() == 1)
		{
			Expression a = parseEDOALEntity((Element)list.get(0).elements().get(0));
			Expression c = parseEDOALEntity((Element)list.get(1).elements().get(0));
			String name = list.get(1).getName();
			if(a instanceof ObjectPropertyExpression && c instanceof ClassExpression && name != null)
			{
				if(name.equals(RDFElement.ALL.toString()))
					return new ObjectAllValues((ObjectPropertyExpression)a,(ClassExpression)c);
				else if(name.equals(RDFElement.EXISTS.toString()) || name.equals(RDFElement.CLASS.toString()))
					return new ObjectSomeValues((ObjectPropertyExpression)a,(ClassExpression)c);
			}
		}
		return null;
	}
	

	private ClassExpression parseAOR(Element e) throws EncodingException
	{
		//AORs should have exactly 3 elements: an onAttribute statement (with 1 sub-element),
		//a comparator (with no sub-elements), and a value expression (with 1 sub-element or 1
		//value that must be a positive integer literal)
		List<Element> list = e.elements();
		if(list.size() == 3 && list.get(0).getName().equals(RDFElement.ON_ATTRIBUTE.toString()) &&
				list.get(0).elements().size() == 1 && list.get(1).getName().equals(RDFElement.COMPARATOR.toString()) &&
				list.get(1).nodeCount() == 0 &&	list.get(2).getName().equals(RDFElement.VALUE.toString()))
		{
			Expression a = parseEDOALEntity((Element)list.get(0).elements().get(0));
			String comp = list.get(1).attributeValue(RDFElement.RDF_RESOURCE.toString());
			Expression v = parseValue(list.get(2));
			if(comp != null && v instanceof Cardinality)
			{
				if(comp.endsWith(RDFElement.GREATER.toString()))
				{
					((Cardinality)v).increment();
					if(a instanceof DataPropertyExpression)
						return new DataMinCardinality((DataPropertyExpression)a,(Cardinality)v);
					else if(a instanceof ObjectPropertyExpression)
						return new ObjectMinCardinality((ObjectPropertyExpression)a,(Cardinality)v);				
				}
				else if(comp.endsWith(RDFElement.LOWER.toString()))
				{
					((Cardinality)v).decrement();
					if(a instanceof DataPropertyExpression)
						return new DataMaxCardinality((DataPropertyExpression)a,(Cardinality)v);
					else if(a instanceof ObjectPropertyExpression)
						return new ObjectMaxCardinality((ObjectPropertyExpression)a,(Cardinality)v);				
				}
				else if(comp.endsWith(RDFElement.EQUALS.toString()))
				{
					if(a instanceof DataPropertyExpression)
						return new DataExactCardinality((DataPropertyExpression)a,(Cardinality)v);
					else if(a instanceof ObjectPropertyExpression)
						return new ObjectExactCardinality((ObjectPropertyExpression)a,(Cardinality)v);				
				}
			}
		}
		return null;
	}
	

	private ClassExpression parseATR(Element e) throws EncodingException
	{
		//ATRs should have exactly 2 elements: an onAttribute statement (with 1 sub-element),
		//and a type restriction (with exactly 1 datatype as sub-element)
		List<Element> list = e.elements();
		if(list.size() == 2 && list.get(0).getName().equals(RDFElement.ON_ATTRIBUTE.toString()) &&
				list.get(0).elements().size() == 1 && 
				list.get(1).getName().equals(RDFElement.DATATYPE.toString()) &&
				list.get(0).elements().size() == 1)
		{
			Expression a = parseEDOALEntity((Element)list.get(0).elements().get(0));
			Datatype d = parseDatatype((Element)list.get(1).elements().get(0));
			if(d != null && a instanceof DataPropertyExpression)
				return new DataSomeValues((DataPropertyExpression)a, d);
		}
		return null;
	}
	

	private ClassExpression parseAVR(Element e) throws EncodingException
	{
		//AVRs should have exactly 3 elements: an onAttribute statement (with 1 sub-element),
		//a comparator (with no sub-elements), and a value expression (with 1 sub-element or 1
		//value)
		List<Element> list = e.elements();
		if(list.size() == 3 && list.get(0).getName().equals(RDFElement.ON_ATTRIBUTE.toString()) &&
				list.get(0).elements().size() == 1 && 
				list.get(1).getName().equals(RDFElement.COMPARATOR.toString()) &&
				list.get(1).nodeCount() == 0 &&
				list.get(2).getName().equals(RDFElement.VALUE.toString()))
		{
			Expression a = parseEDOALEntity((Element)list.get(0).elements().get(0));
			String comp = list.get(1).attributeValue(RDFElement.RDF_RESOURCE.toString());
			Expression v = parseValue(list.get(2));
			if(comp != null && v instanceof ValueExpression)
			{
				if(comp.endsWith(RDFElement.EQUALS.toString()) && a instanceof DataPropertyExpression && v instanceof Literal)
					return new DataHasValue((DataPropertyExpression)a,(Literal)v);
				else if(comp.endsWith(RDFElement.EQUALS.toString()) && a instanceof ObjectPropertyExpression && v instanceof Individual)
					return new ObjectHasValue((ObjectPropertyExpression)a,(Individual)v);				
				else
					return new PropertyComparatorValue((PropertyExpression)a,new Comparator(comp),(ValueExpression)v);				
			}
		}
		return null;
	}
	

	private PropertyExpression parsePDR(Element e) throws EncodingException
	{
		//PDRs should have a single element that is a class restriction, consisting also of a single class expression
		List<Element> list = e.elements();
		if(list.size() == 1 && list.get(0).getName().equals(RDFElement.CLASS.toString()) &&
				list.get(0).elements().size() == 1)
		{
			Expression c = parseEDOALEntity((Element)list.get(0).elements().get(0));
			if(c instanceof ClassExpression)
				return new DataPropertyDomain((ClassExpression)c);
		}
		return null;
	}
	

	private PropertyExpression parsePTR(Element e)
	{
		//PTRs should have a single element that is a datatype restriction, consisting of a datatype
		List<Element> list = e.elements();
		if(list.size() == 1 && list.get(0).getName().equals(RDFElement.DATATYPE.toString()) &&
				list.get(0).elements().size() == 1)
		{
			Datatype d = parseDatatype((Element)list.get(0).elements().get(0));
			if(d != null)
				return new DataPropertyRangeType(d);
		}
		return null;
	}
	

	private PropertyExpression parsePVR(Element e) throws EncodingException
	{
		//PVRs should have exactly 2 elements: a comparator (with no sub-elements),
		//and a value expression (with 1 sub-element or 1 value)
		List<Element> list = e.elements();
		if(list.size() == 2 && list.get(0).getName().equals(RDFElement.COMPARATOR.toString()) &&
				list.get(1).nodeCount() == 0 && list.get(1).getName().equals(RDFElement.VALUE.toString()))
		{
			String comp = list.get(1).attributeValue(RDFElement.RDF_RESOURCE.toString());
			Expression v = parseValue(list.get(1));
			if(comp != null && v instanceof ValueExpression)
				return new DataPropertyRangeValue(new Comparator(comp), (ValueExpression)v);
		}
		return null;
	}
	

	private PropertyExpression parseRDR(Element e) throws EncodingException
	{
		//PDRs should have a single element that is a class restriction, consisting also of a single class expression
		List<Element> list = e.elements();
		if(list.size() == 1 && list.get(0).getName().equals(RDFElement.CLASS.toString()) &&
				list.get(0).elements().size() == 1)
		{
			Expression c = parseEDOALEntity((Element)list.get(0).elements().get(0));
			if(c instanceof ClassExpression)
				return new ObjectPropertyDomain((ClassExpression)c);
		}
		return null;
	}
	

	private PropertyExpression parseRCR(Element e) throws EncodingException
	{
		//PDRs should have a single element that is a class restriction, consisting also of a single class expression
		List<Element> list = e.elements();
		if(list.size() == 1 && list.get(0).getName().equals(RDFElement.CLASS.toString()) &&
				list.get(0).elements().size() == 1)
		{
			Expression c = parseEDOALEntity((Element)list.get(0).elements().get(0));
			if(c instanceof ClassExpression)
				return new ObjectPropertyRange((ClassExpression)c);
		}
		return null;
	}
	
	
	private Individual parseInstance(Element e)
	{
		//Individual expressions currently encompass only individual ids
		if(e.nodeCount() == 0)
		{
			String uri = e.attributeValue(RDFElement.RDF_ABOUT.toString());
			if(uri != null)
				return new Individual(uri);
		}
		return null;
	}
	
	private Datatype parseDatatype(Element e)
	{
		if(e.nodeCount() == 0)
		{
			String uri = e.attributeValue(RDFElement.RDF_ABOUT.toString());
			if(uri != null)
				return new Datatype(uri);
		}
		return null;
	}
	
	private Expression parseValue(Element e) throws EncodingException
	{
		//If the value is provided as text, it must be a literal
		if(e.isTextOnly())
		{
			String value = e.getText();
			//Check if it is a NonNegativeInteger
			try
			{
				int v = Integer.parseInt(value);
				if(v > -1)
					return new Cardinality(v);
			}
			catch(NumberFormatException n){	/*Do nothing*/ }
			return new Literal(value, null, null);
		}
		//Otherwise
		else if(e.elements().size() == 1)
		{
			Element f = (Element)e.elements().get(0);
			//It may be a literal
			if(f.getName().equals(RDFElement.LITERAL_.toString()))
			{
				String value = f.attributeValue(RDFElement.STRING.toString());
				String type = f.attributeValue(RDFElement.TYPE.toString());
				String lang = f.attributeValue(RDFElement.LANG.toString());
				if(value == null)
					return null;
				if((type == null && lang == null) || type.contains("int"))
				{
					try
					{
						int v = Integer.parseInt(value);
						if(v > -1)
							return new Cardinality(v);
					}
					catch(NumberFormatException n){	/*Do nothing*/ }
				}
				return new Literal(value, type, lang);
			}
			//But also one of several expressions
			Expression a = parseEDOALEntity(f);
			if(a instanceof ValueExpression)
				return a;
		}
		return null;
	}


	private Apply parseApply(Element e) throws EncodingException
	{
		//The operator should be an attribute of the Apply element itself
		String operator = e.attributeValue(RDFElement.OPERATOR.toString());
		//Apply should have a single sub-element "arguments" which is a collection of value expressions
		List<Element> list = e.elements();
		if(operator != null && list.size() == 1 && list.get(0).getName().equals(RDFElement.ARGUMENTS.toString()))
		{
			Vector<ValueExpression> attributes = new Vector<ValueExpression>();
			list = list.get(0).elements();
			for(Element f : list)
			{
				Expression x = parseValue(f);
				if(!(x instanceof ValueExpression))
					return null;
				attributes.add((ValueExpression)x);
			}
			if(!attributes.isEmpty())
				return new Apply(operator, attributes);
		}
		return null;
	}
	

	private Aggregate parseAggregate(Element e) throws EncodingException
	{
		//The operator should be an attribute of the Aggregate element itself
		String operator = e.attributeValue(RDFElement.OPERATOR.toString());
		//Aggregate should have a single sub-element "arguments" which is a collection of value expressions
		List<Element> list = e.elements();
		if(operator != null && list.size() == 1 && list.get(0).getName().equals(RDFElement.ARGUMENTS.toString()))
		{
			Vector<ValueExpression> attributes = new Vector<ValueExpression>();
			list = list.get(0).elements();
			for(Element f : list)
			{
				Expression x = parseValue(f);
				if(!(x instanceof ValueExpression))
					return null;
				attributes.add((ValueExpression)x);
			}
			if(!attributes.isEmpty())
				return new Aggregate(operator, attributes);
		}
		return null;
	}
	

	private LinkKey parseLinkKey(Element key, boolean reverse) throws EncodingException
	{
		//The first element under <linkkey> should be <Linkkey>
		Element e = key.element(RDFElement.LINKKEY_.toString());
		if(e == null)
			return null;
		//<Linkkey> may have a <lk:type> and should have one or two <binding>
		List<Element> list = e.elements();
		String type = null;
		HashMap<PropertyExpression,PropertyExpression> equals = new HashMap<PropertyExpression,PropertyExpression>();
		HashMap<PropertyExpression,PropertyExpression> intersects = new HashMap<PropertyExpression,PropertyExpression>();
		for(Element f : list)
		{
			if(f.getName().equals("lk:type"))
				type = f.getText();
			else if(f.getName().equals(RDFElement.BINDING.toString()))
			{
				List<Element> binding = f.elements();
				if(binding == null || binding.size() != 1)
					return null;
				String name = binding.get(0).getName();
				boolean eq = name.equals(RDFElement.EQUALS_.toString());
				if(!eq && !name.equals(RDFElement.INTERSECTS_.toString()))
					return null;
				if(binding.get(0).elements().size() != 2)
					return null;
				Element prop1 = binding.get(0).element(RDFElement.PROPERTY1.toString());
				Element prop2 = binding.get(0).element(RDFElement.PROPERTY2.toString());
				if(prop1 == null || prop2 == null ||
						prop1.elements().size() != 1 ||
						prop2.elements().size() != 1)
					return null;
				prop1 = (Element)prop1.elements().get(0);
				prop2 = (Element)prop2.elements().get(0);
				PropertyExpression p1 = null, p2 = null;
				if(prop1.getName().equals(RDFElement.RELATION_.toString()))
					p1 = parseObjectProperty(prop1);
				else if(prop1.getName().equals(RDFElement.PROPERTY_.toString()))
					p1 = parseProperty(prop1);
				else
					return null;
				if(prop2.getName().equals(RDFElement.RELATION_.toString()))
					p2 = parseObjectProperty(prop2);
				else if(prop2.getName().equals(RDFElement.PROPERTY_.toString()))
					p2 = parseProperty(prop2);
				else
					return null;
				if(reverse)
				{
					if(eq)
						equals.put(p2, p1);
					else
						intersects.put(p2, p1);
				}
				else
				{
					if(eq)
						equals.put(p1, p2);
					else
						intersects.put(p1, p2);
				}
			}
		}
		if(equals.size() + intersects.size() == 0)
			return null;
		else
			return new LinkKey(type, equals, intersects);
	}

	private Transformation parseTransformation(Element e, boolean reverse) throws EncodingException
	{
		//Set e to the <Transformation> element, as the entities will be listed therein 
		Element f = e.element(RDFElement.TRANSFORMATION_.toString());
		//Record the direction attribute
		String direction = f.attributeValue(RDFElement.DIRECTION.toString());
		//Parse the entities
		Element entity1 = f.element(RDFElement.ENTITY1.toString());
		Element entity2 = f.element(RDFElement.ENTITY2.toString());
		if(entity1 == null || entity2 == null)
			return null;
		Expression source, target;
		if(reverse)
		{
			source = parseEDOALEntity(entity2);
			target = parseEDOALEntity(entity1);
			if(direction != null && direction.length() == 2)
				direction = "" + direction.charAt(1) + direction.charAt(0);
		}
		else
		{
			source = parseEDOALEntity(entity1);
			target = parseEDOALEntity(entity2);
		}
		//The entities in a transformation should be value expressions and one of them should
		//be an apply or aggregate
		if(!(source instanceof ValueExpression && target instanceof ValueExpression) ||
				!(source instanceof Apply || source instanceof Aggregate || 
				target instanceof Apply || target instanceof Aggregate))
			return null;
		return new Transformation(direction,(ValueExpression)source,(ValueExpression)target);
	}
}