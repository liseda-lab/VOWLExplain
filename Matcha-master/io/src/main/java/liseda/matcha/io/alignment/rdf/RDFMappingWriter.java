package liseda.matcha.io.alignment.rdf;

import java.io.PrintWriter;
import java.util.Vector;

import liseda.matcha.alignment.Mapping;
import liseda.matcha.io.EncodingException;
import liseda.matcha.io.alignment.MappingWriter;
import liseda.matcha.semantics.Expression;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.edoal.DataPropertyDomain;
import liseda.matcha.semantics.edoal.DataPropertyIntersection;
import liseda.matcha.semantics.edoal.DataPropertyNegation;
import liseda.matcha.semantics.edoal.DataPropertyRangeType;
import liseda.matcha.semantics.edoal.DataPropertyRangeValue;
import liseda.matcha.semantics.edoal.DataPropertyUnion;
import liseda.matcha.semantics.edoal.ObjectPropertyDomain;
import liseda.matcha.semantics.edoal.ObjectPropertyIntersection;
import liseda.matcha.semantics.edoal.ObjectPropertyNegation;
import liseda.matcha.semantics.edoal.ObjectPropertyRange;
import liseda.matcha.semantics.edoal.ObjectPropertyUnion;
import liseda.matcha.semantics.edoal.PropertyComparatorValue;
import liseda.matcha.semantics.edoal.ReflexiveProperty;
import liseda.matcha.semantics.edoal.SymmetricProperty;
import liseda.matcha.semantics.edoal.TransitiveProperty;
import liseda.matcha.semantics.owl.CardinalityRestriction;
import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.ClassIntersection;
import liseda.matcha.semantics.owl.ClassNegation;
import liseda.matcha.semantics.owl.ClassUnion;
import liseda.matcha.semantics.owl.Construction;
import liseda.matcha.semantics.owl.DataAllValues;
import liseda.matcha.semantics.owl.DataExactCardinality;
import liseda.matcha.semantics.owl.DataHasValue;
import liseda.matcha.semantics.owl.DataMinCardinality;
import liseda.matcha.semantics.owl.DataPropertyExpression;
import liseda.matcha.semantics.owl.DataSomeValues;
import liseda.matcha.semantics.owl.Datatype;
import liseda.matcha.semantics.owl.Individual;
import liseda.matcha.semantics.owl.InverseProperty;
import liseda.matcha.semantics.owl.Literal;
import liseda.matcha.semantics.owl.ObjectAllValues;
import liseda.matcha.semantics.owl.ObjectExactCardinality;
import liseda.matcha.semantics.owl.ObjectHasSelf;
import liseda.matcha.semantics.owl.ObjectHasValue;
import liseda.matcha.semantics.owl.ObjectMinCardinality;
import liseda.matcha.semantics.owl.ObjectSomeValues;
import liseda.matcha.semantics.owl.OneOf;
import liseda.matcha.semantics.owl.SimpleClass;
import liseda.matcha.semantics.owl.SimpleDataProperty;
import liseda.matcha.semantics.owl.SimpleObjectProperty;
import liseda.matcha.vocabulary.Namespace;
import liseda.matcha.vocabulary.RDFElement;

public class RDFMappingWriter implements MappingWriter
{
	
//Attributes
	
	private PrintWriter out;
	private boolean edoal;
	
//Public Methods
	
	public RDFMappingWriter(PrintWriter out, boolean edoal)
	{
		this.out = out;
		this.edoal = edoal;
	}
	
//Public Methods
		
	public void writeMapping(Mapping m) throws EncodingException //TODO: Refactor this to use SAXWriter
	{
		//TODO: Expand to include transformations and linkkeys
		out.println("\t\t<" + RDFElement.MAP + ">");
		out.println("\t\t\t<" + RDFElement.CELL_ + ">");
		if(edoal)
		{
			out.println("\t\t\t\t<entity1>");
			for(String s : toRDF(m.getEntity1()))
				out.println(s);
			out.println("\t\t\t\t</entity1>");
			out.println("\t\t\t\t<entity2>");
			for(String s : toRDF(m.getEntity2()))
				out.println(s);
			out.println("\t\t\t\t</entity2>");
		}
		else
		{
			out.println("\t\t\t\t<" + RDFElement.ENTITY1.toString() + " " + RDFElement.RDF_RESOURCE.toRDF() + "=\"" + m.getEntity1() +"\"/>");
			out.println("\t\t\t\t<" + RDFElement.ENTITY2.toString() + " " + RDFElement.RDF_RESOURCE.toRDF() + "=\"" + m.getEntity2() +"\"/>");
		}
		out.println("\t\t\t\t<" + RDFElement.MEASURE + " " + RDFElement.RDF_DATATYPE.toRDF() + "=\"" + Namespace.XSD.uri + "float\">" + m.getSimilarity() + "</measure>");
		out.println("\t\t\t\t<" + RDFElement.RELATION + ">" + m.getRelationship().toStringSafe() + "</relation>");
		if(m.getProvenance() != null)
			out.println("\t\t\t\t<" + RDFElement.PROVENANCE.toRDF() + ">Matcha; " + m.getProvenance() + "; " + m.getStatus().toString() + "</" + RDFElement.PROVENANCE.toRDF() + ">");
		out.println("\t\t\t</" + RDFElement.CELL_ + ">");
		out.println("\t\t</" + RDFElement.MAP + ">");
	}
	
//Private Methods
	
	//Top RDF entity writer
	private Vector<String> toRDF(String entity) throws EncodingException
	{
		Vector<String> lines = new Vector<String>();
		SemanticMap sm = SemanticMap.getInstance();
		if(sm.isClass(entity))
			lines.add("\t\t\t\t\t<" + RDFElement.CLASS_.toRDF() + " " +  RDFElement.RDF_ABOUT.toRDF() + "=\"" +	entity + "\"/>");
		else if(sm.isDataProperty(entity))
			lines.add("\t\t\t\t\t<" + RDFElement.PROPERTY_.toRDF() + " " +  RDFElement.RDF_ABOUT.toRDF() + "=\"" +	entity + "\"/>");
		else if(sm.isObjectProperty(entity))
			lines.add("\t\t\t\t\t<" + RDFElement.RELATION_.toRDF() + " " +  RDFElement.RDF_ABOUT.toRDF() + "=\"" +	entity + "\"/>");
		else if(sm.isIndividual(entity))
			lines.add("\t\t\t\t\t<" + RDFElement.INSTANCE_.toRDF() + " " +  RDFElement.RDF_ABOUT.toRDF() + "=\"" +	entity + "\"/>");
		else
			toRDF(sm.getExpression(entity),lines,5);
		return lines;
	}

	//Recursive expression RDF writer
	private void toRDF(Expression x, Vector<String> lines, int tabs) throws EncodingException
	{
		//Process indentation
		String s = "";
		for(int i = 0; i < tabs; i++)
			s += "\t";
		//Simple cases
		if(x instanceof SimpleClass)
			lines.add(s + "<" + RDFElement.CLASS_.toRDF() + " " +  RDFElement.RDF_ABOUT.toRDF() + "=\"" +	x.toString() + "\"/>");
		else if(x instanceof SimpleDataProperty)
			lines.add(s + "<" + RDFElement.PROPERTY_.toRDF() + " " +  RDFElement.RDF_ABOUT.toRDF() + "=\"" +	x.toString() + "\"/>");
		else if(x instanceof SimpleObjectProperty)
			lines.add(s + "<" + RDFElement.RELATION_.toRDF() + " " +  RDFElement.RDF_ABOUT.toRDF() + "=\"" +	x.toString() + "\"/>");
		else if(x instanceof Individual)
			lines.add(s + "<" + RDFElement.INSTANCE_.toRDF() + " " +  RDFElement.RDF_ABOUT.toRDF() + "=\"" +	x.toString() + "\"/>");
		else if(x instanceof Datatype)
			lines.add(s + "<" + RDFElement.DATATYPE_.toRDF() + " " +  RDFElement.RDF_ABOUT.toRDF() + "=\"" +	x.toString() + "\"/>");
		else if(x instanceof Literal)
		{
			Literal l = (Literal)x;
			lines.add(s + "<" + RDFElement.LITERAL_.toRDF() +
					(l.getType() != null ? (" " + RDFElement.TYPE.toRDF() + "=\"" + l.getType() + "\"") : "") +
					(l.getLanguage() != null ? (" " + RDFElement.LANG.toRDF() + "=\"" + l.getLanguage() + "\"") : "") +
					" " + RDFElement.STRING.toRDF() + "=\"" + l.getValue() + "\"/>");
		}
		//Construction expressions (and, or, not, property chains)
		else if(x instanceof Construction)
		{
			//Process the type
			String type;
			if(x instanceof ClassExpression)
				type = RDFElement.CLASS_.toRDF();
			else if(x instanceof DataPropertyExpression)
				type = RDFElement.PROPERTY_.toRDF();
			else
				type = RDFElement.RELATION_.toRDF();
			//Process the operator
			String operator;
			if(x instanceof ClassIntersection || x instanceof DataPropertyIntersection || x instanceof ObjectPropertyIntersection)
				operator = RDFElement.AND.toRDF();
			else if(x instanceof ClassUnion || x instanceof DataPropertyUnion || x instanceof ObjectPropertyUnion)
				operator = RDFElement.OR.toRDF();
			else if(x instanceof ClassNegation || x instanceof DataPropertyNegation || x instanceof ObjectPropertyNegation)
				operator = RDFElement.NOT.toRDF();
			else
				operator = RDFElement.COMPOSE.toRDF();
			lines.add(s + "<" + type + ">");
			lines.add(s + "\t<" + operator + " "  + RDFElement.RDF_PARSETYPE.toRDF() + "=\"Collection\">");
			for(Expression e : x.getComponents())
				toRDF(e,lines,tabs+2);
			lines.add(s + "\t</" + operator + ">");
			lines.add(s + "</" + type + ">");
		}
		//Cardinality restrictions (attribute occurrence restrictions)
		else if(x instanceof CardinalityRestriction)
		{
			if(((CardinalityRestriction) x).isQualified())
				System.err.println("WARNING: Loss of precision; EDOAL does not support qualified cardinality restrictions");
			String comparator = "<" + RDFElement.COMPARATOR.toRDF() + " rdf:resource=\"";
			int cardinality = ((CardinalityRestriction) x).getCardinality();
			if(x instanceof DataExactCardinality || x instanceof ObjectExactCardinality)
				comparator += RDFElement.EQUALS.toRDFResource() + "\"/>";
			if(x instanceof DataMinCardinality || x instanceof ObjectMinCardinality)
			{
				comparator += RDFElement.GREATER.toRDFResource() + "\"/>";
				cardinality--; //OWL is greater or equal; EDOAL just greater
			}
			else
			{
				comparator += RDFElement.LOWER.toRDFResource() + "\"/>";
				cardinality++; //OWL is lesser or equal; EDOAL just lesser
			}
			lines.add(s + "<" + RDFElement.ATTR_OCCURRENCE_REST_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.ON_ATTRIBUTE.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.ON_ATTRIBUTE.toRDF() + ">");
			lines.add(s + "\t" + comparator);
			lines.add(s + "\t<" + RDFElement.VALUE.toRDF() + ">" + cardinality + "</" + RDFElement.VALUE.toRDF() + ">");
			lines.add(s + "</" + RDFElement.ATTR_OCCURRENCE_REST_.toRDF() + ">");
		}
		//Existential restrictions on data properties (attribute type restrictions)
		else if(x instanceof DataAllValues || x instanceof DataSomeValues)
		{
			System.err.println("WARNING: Loss of precision; EDOAL does not support quantifiers in existential restrictions on DataProperties");
			lines.add(s + "<" + RDFElement.ATTR_TYPE_REST_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.ON_ATTRIBUTE.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.ON_ATTRIBUTE.toRDF() + ">");
			toRDF(x.getComponents().get(1),lines,tabs+2);
			lines.add(s + "</" + RDFElement.ATTR_TYPE_REST_.toRDF() + ">");
		}
		//Existential restrictions on object properties (attribute type restrictions)
		else if(x instanceof ObjectAllValues || x instanceof ObjectSomeValues)
		{
			String qual;
			if(x instanceof ObjectAllValues)
				qual = RDFElement.ALL.toRDF();
			else
				qual = RDFElement.EXISTS.toRDF();
			lines.add(s + "<" + RDFElement.ATTR_DOMAIN_REST_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.ON_ATTRIBUTE.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.ON_ATTRIBUTE.toRDF() + ">");
			lines.add(s + "\t<" + qual + ">");
			toRDF(x.getComponents().get(1),lines,tabs+2);
			lines.add(s + "\t</" + qual + ">");
			lines.add(s + "</" + RDFElement.ATTR_DOMAIN_REST_.toRDF() + ">");
		}
		//Value restrictions (attribute value restrictions)
		else if(x instanceof DataHasValue || x instanceof ObjectHasValue || x instanceof PropertyComparatorValue)
		{
			int i = 0;
			lines.add(s + "<" + RDFElement.ATTR_VALUE_REST_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.ON_ATTRIBUTE.toRDF() + ">");
			toRDF(x.getComponents().get(i++),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.ON_ATTRIBUTE.toRDF() + ">");
			if(x instanceof PropertyComparatorValue)
				lines.add(s + "\t<" + RDFElement.COMPARATOR.toRDF() + " " + RDFElement.RDF_RESOURCE.toRDF() + "=\"" + x.getComponents().get(i++) + "\"/>");
			else
				lines.add(s + "\t<" + RDFElement.COMPARATOR.toRDF() + " " + RDFElement.RDF_RESOURCE.toRDF() + "=\"" + RDFElement.EQUALS.toRDFResource() + "\"/>");
			lines.add(s + "\t</" + RDFElement.VALUE.toRDF() + ">");
			toRDF(x.getComponents().get(i),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.VALUE.toRDF() + ">");
			lines.add(s + "</" + RDFElement.ATTR_VALUE_REST_.toRDF() + ">");
		}
		//Domain & range restrictions (property domain, type and value; relation domain and co-domain)
		else if(x instanceof DataPropertyDomain)
		{
			lines.add(s + "<" + RDFElement.PROPERTY_DOMAIN_REST_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.CLASS.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.CLASS.toRDF() + ">");
			lines.add(s + "</" + RDFElement.PROPERTY_DOMAIN_REST_.toRDF() + ">");
		}
		else if(x instanceof DataPropertyRangeType)
		{
			lines.add(s + "<" + RDFElement.PROPERTY_DOMAIN_REST_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.DATATYPE.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.DATATYPE.toRDF() + ">");
			lines.add(s + "</" + RDFElement.PROPERTY_DOMAIN_REST_.toRDF() + ">");
		}
		else if(x instanceof DataPropertyRangeValue)
		{
			lines.add(s + "<" + RDFElement.PROPERTY_VALUE_REST_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.COMPARATOR.toRDF() + " " + RDFElement.RDF_RESOURCE.toRDF() + "=\"" + x.getComponents().get(0) + "\"/>");
			lines.add(s + "\t<" + RDFElement.VALUE.toRDF() + ">");
			toRDF(x.getComponents().get(1),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.VALUE.toRDF() + ">");
			lines.add(s + "</" + RDFElement.PROPERTY_VALUE_REST_.toRDF() + ">");
		}
		else if(x instanceof ObjectPropertyDomain)
		{
			lines.add(s + "<" + RDFElement.RELATION_DOMAIN_REST_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.CLASS.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.CLASS.toRDF() + ">");
			lines.add(s + "</" + RDFElement.RELATION_DOMAIN_REST_.toRDF() + ">");
		}
		else if(x instanceof ObjectPropertyRange)
		{
			lines.add(s + "<" + RDFElement.RELATION_CODOMAIN_REST_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.CLASS.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.CLASS.toRDF() + ">");
			lines.add(s + "</" + RDFElement.RELATION_CODOMAIN_REST_.toRDF() + ">");
		}
		//Object property expressions (inverse, reflexive, symmetric, transitive)
		else if(x instanceof InverseProperty)
		{
			lines.add(s + "<" + RDFElement.RELATION_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.INVERSE.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.INVERSE.toRDF() + ">");
			lines.add(s + "</" + RDFElement.RELATION_.toRDF() + ">");
		}
		else if(x instanceof ReflexiveProperty)
		{
			lines.add(s + "<" + RDFElement.RELATION_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.REFLEXIVE.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.REFLEXIVE.toRDF() + ">");
			lines.add(s + "</" + RDFElement.RELATION_.toRDF() + ">");
		}
		else if(x instanceof SymmetricProperty)
		{
			lines.add(s + "<" + RDFElement.RELATION_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.SYMMETRIC.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.SYMMETRIC.toRDF() + ">");
			lines.add(s + "</" + RDFElement.RELATION_.toRDF() + ">");
		}
		else if(x instanceof TransitiveProperty)
		{
			lines.add(s + "<" + RDFElement.RELATION_.toRDF() + ">");
			lines.add(s + "\t<" + RDFElement.TRANSITIVE.toRDF() + ">");
			toRDF(x.getComponents().get(0),lines,tabs+2);
			lines.add(s + "\t</" + RDFElement.TRANSITIVE.toRDF() + ">");
			lines.add(s + "</" + RDFElement.RELATION_.toRDF() + ">");
		}
		//Unencodable restrictions
		else if(x instanceof ObjectHasSelf || x instanceof OneOf)
		{
			throw new EncodingException("ERROR: Cannot encode " + x.getClass() + " in EDOAL");
		}
	}
}