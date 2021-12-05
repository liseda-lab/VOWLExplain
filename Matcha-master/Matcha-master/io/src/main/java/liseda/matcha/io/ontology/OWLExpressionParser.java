/******************************************************************************
* Utility class for parsing OWL expressions from OWLAPI and converting them   *
* into Matcha expressions.                                                    *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.ontology;

import java.util.Vector;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import liseda.matcha.semantics.owl.Cardinality;
import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.ClassIntersection;
import liseda.matcha.semantics.owl.ClassNegation;
import liseda.matcha.semantics.owl.ClassUnion;
import liseda.matcha.semantics.owl.DataAllValues;
import liseda.matcha.semantics.owl.DataExactCardinality;
import liseda.matcha.semantics.owl.DataHasValue;
import liseda.matcha.semantics.owl.DataMaxCardinality;
import liseda.matcha.semantics.owl.DataMinCardinality;
import liseda.matcha.semantics.owl.DataSomeValues;
import liseda.matcha.semantics.owl.Datatype;
import liseda.matcha.semantics.owl.Individual;
import liseda.matcha.semantics.owl.InverseProperty;
import liseda.matcha.semantics.owl.Literal;
import liseda.matcha.semantics.owl.ObjectAllValues;
import liseda.matcha.semantics.owl.ObjectExactCardinality;
import liseda.matcha.semantics.owl.ObjectHasSelf;
import liseda.matcha.semantics.owl.ObjectHasValue;
import liseda.matcha.semantics.owl.ObjectMaxCardinality;
import liseda.matcha.semantics.owl.ObjectMinCardinality;
import liseda.matcha.semantics.owl.ObjectPropertyExpression;
import liseda.matcha.semantics.owl.ObjectSomeValues;
import liseda.matcha.semantics.owl.OneOf;
import liseda.matcha.semantics.owl.SimpleClass;
import liseda.matcha.semantics.owl.SimpleDataProperty;
import liseda.matcha.semantics.owl.SimpleObjectProperty;

public class OWLExpressionParser
{
	/**
	 * Converts an OWLClassExpression into a Matcha ClassExpression recursively
	 * @param e: the OWLClassExpression to convert
	 * @return the corresponding ClassExpression
	 */
	public static ClassExpression parse(OWLClassExpression e)
	{
		ClassExpressionType type = e.getClassExpressionType();
		//Trivial case: simple class
		if(type.equals(ClassExpressionType.OWL_CLASS))
			return new SimpleClass(e.asOWLClass().toStringID());
		//Non-nested cases
		else if(type.equals(ClassExpressionType.DATA_HAS_VALUE))
		{
			OWLDataHasValue hv = (OWLDataHasValue)e;
			OWLDataProperty p = hv.getProperty().asOWLDataProperty();
			OWLLiteral l = hv.getFiller();
			return new DataHasValue(new SimpleDataProperty(p.toStringID()),
					new Literal(l.getLiteral(),l.getDatatype().toStringID(),l.getLang()));
		}
		else if(type.equals(ClassExpressionType.DATA_SOME_VALUES_FROM))
		{
			OWLDataSomeValuesFrom sv = (OWLDataSomeValuesFrom)e;
			//The only OWLDataPropertyExpression is the OWLDataProperty
			OWLDataProperty p = sv.getProperty().asOWLDataProperty();
			Datatype d = parse(sv.getFiller());
			return new DataSomeValues(new SimpleDataProperty(p.toStringID()),d);
		}
		else if(type.equals(ClassExpressionType.DATA_ALL_VALUES_FROM))
		{
			OWLDataAllValuesFrom av = (OWLDataAllValuesFrom)e;
			//The only OWLDataPropertyExpression is the OWLDataProperty
			OWLDataProperty p = av.getProperty().asOWLDataProperty();
			Datatype d = parse(av.getFiller());
			return new DataAllValues(new SimpleDataProperty(p.toStringID()),d);
		}
		else if(type.equals(ClassExpressionType.DATA_EXACT_CARDINALITY))
		{
			OWLDataExactCardinality ec = (OWLDataExactCardinality)e;
			OWLDataProperty p = ec.getProperty().asOWLDataProperty();
			int card = ec.getCardinality();
			if(!ec.isQualified())
				return new DataExactCardinality(new SimpleDataProperty(p.toStringID()), new Cardinality(card));
			Datatype d = parse(ec.getFiller());
			return new DataExactCardinality(new SimpleDataProperty(p.toStringID()), new Cardinality(card), d);
		}
		else if(type.equals(ClassExpressionType.DATA_MIN_CARDINALITY))
		{
			OWLDataMinCardinality mc = (OWLDataMinCardinality)e;
			OWLDataProperty p = mc.getProperty().asOWLDataProperty();
			int card = mc.getCardinality();
			if(!mc.isQualified())
				return new DataMinCardinality(new SimpleDataProperty(p.toStringID()), new Cardinality(card));
			Datatype d = parse(mc.getFiller());
			return new DataMinCardinality(new SimpleDataProperty(p.toStringID()), new Cardinality(card), d);
		}
		else if(type.equals(ClassExpressionType.DATA_MAX_CARDINALITY))
		{
			OWLDataMaxCardinality mc = (OWLDataMaxCardinality)e;
			OWLDataProperty p = mc.getProperty().asOWLDataProperty();
			int card = mc.getCardinality();
			if(!mc.isQualified())
				return new DataMaxCardinality(new SimpleDataProperty(p.toStringID()), new Cardinality(card));
			Datatype d = parse(mc.getFiller());
			return new DataMaxCardinality(new SimpleDataProperty(p.toStringID()), new Cardinality(card), d);
		}
		else if(type.equals(ClassExpressionType.OBJECT_HAS_SELF))
		{
			OWLObjectHasSelf hs = (OWLObjectHasSelf)e;
			OWLObjectPropertyExpression p = hs.getProperty();
			return new ObjectHasSelf(parse(p));
		}
		else if(type.equals(ClassExpressionType.OBJECT_HAS_VALUE))
		{
			OWLObjectHasValue hv = (OWLObjectHasValue)e;
			OWLObjectPropertyExpression p = hv.getProperty();
			OWLIndividual i = hv.getFiller();
			return new ObjectHasValue(parse(p),new Individual(i.toStringID()));
		}
		//Nested cases
		else if(type.equals(ClassExpressionType.OBJECT_COMPLEMENT_OF))
		{
			OWLObjectComplementOf oc = (OWLObjectComplementOf)e;
			ClassExpression c = parse(oc.getOperand());
			return new ClassNegation(c);
		}
		else if(type.equals(ClassExpressionType.OBJECT_INTERSECTION_OF))
		{
			Vector<ClassExpression> in = new Vector<ClassExpression>();
			((OWLObjectIntersectionOf)e).operands().forEach(a -> in.add(parse(a)));
			return new ClassIntersection(in);
		}
		else if(type.equals(ClassExpressionType.OBJECT_UNION_OF))
		{
			Vector<ClassExpression> un = new Vector<ClassExpression>();
			((OWLObjectUnionOf)e).operands().forEach(a -> un.add(parse(a)));
			return new ClassUnion(un);			
		}
		else if(type.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM))
		{
			OWLObjectSomeValuesFrom sv = (OWLObjectSomeValuesFrom)e;
			OWLObjectPropertyExpression p = sv.getProperty();
			OWLClassExpression c = sv.getFiller();
			return new ObjectSomeValues(parse(p),parse(c));
		}
		else if(type.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM))
		{
			OWLObjectAllValuesFrom av = (OWLObjectAllValuesFrom)e;
			OWLObjectPropertyExpression p = av.getProperty();
			OWLClassExpression c = av.getFiller();
			return new ObjectAllValues(parse(p),parse(c));
		}
		else if(type.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY))
		{
			OWLObjectExactCardinality ec = (OWLObjectExactCardinality)e;
			OWLObjectPropertyExpression p = ec.getProperty();
			int card = ec.getCardinality();
			if(!ec.isQualified())
				return new ObjectExactCardinality(parse(p), new Cardinality(card));
			OWLClassExpression c = ec.getFiller();
			return new ObjectExactCardinality(parse(p), new Cardinality(card), parse(c));
		}
		else if(type.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY))
		{
			OWLObjectMinCardinality mc = (OWLObjectMinCardinality)e;
			OWLObjectPropertyExpression p = mc.getProperty();
			int card = mc.getCardinality();
			if(!mc.isQualified())
				return new ObjectMinCardinality(parse(p), new Cardinality(card));
			OWLClassExpression c = mc.getFiller();
			return new ObjectMinCardinality(parse(p), new Cardinality(card), parse(c));
		}
		else if(type.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY))
		{
			OWLObjectMaxCardinality mc = (OWLObjectMaxCardinality)e;
			OWLObjectPropertyExpression p = mc.getProperty();
			int card = mc.getCardinality();
			if(!mc.isQualified())
				return new ObjectMaxCardinality(parse(p), new Cardinality(card));
			OWLClassExpression c = mc.getFiller();
			return new ObjectMaxCardinality(parse(p), new Cardinality(card), parse(c));
		}
		else if(type.equals(ClassExpressionType.OBJECT_ONE_OF))
		{
			Vector<Individual> indivs = new Vector<Individual>();
			((OWLObjectOneOf)e).individuals().forEach(a -> indivs.add(new Individual(a.toStringID())));
			return new OneOf(indivs);
		}
		return null;
	}
	
	/**
	 * Converts an OWLObjectPropertyExpression into a Matcha ObjectPropertyExpression
	 * @param e: the OWLObjectPropertyExpression to convert
	 * @return the corresponding ObjectPropertyExpression
	 */
	public static ObjectPropertyExpression parse(OWLObjectPropertyExpression e)
	{
		//An Object Property Expression is either an Object Property
		if(e.isOWLObjectProperty())
			return new SimpleObjectProperty(e.asOWLObjectProperty().toStringID());
		//Or the inverse of an Object Property
		else
			return new InverseProperty(new SimpleObjectProperty(((OWLObjectInverseOf)e).getNamedProperty().toStringID()));
	}
	
	/**
	 * Converts an OWLDataRange into a Matcha Datatype
	 * @param e: the OWLDataRange to convert
	 * @return the corresponding Datatype
	 */
	public static Datatype parse(OWLDataRange d)
	{
		if(d.isOWLDatatype())
			return new Datatype(d.asOWLDatatype().toStringID());
		else
			return new Datatype(d.toString()); //TODO: Fix this when we have the provisions for handling custom data ranges
	}
}