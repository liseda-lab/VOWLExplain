/******************************************************************************
 * Reasons over and expands the SemanticMap.                                   *
 *                                                                             *
 * @author Daniel Faria, Beatriz Lima                                          *
 ******************************************************************************/
package liseda.matcha.semantics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import liseda.matcha.data.Map2Map;
import liseda.matcha.data.Map2Map2Map;
import liseda.matcha.semantics.owl.Cardinality;
import liseda.matcha.semantics.owl.CardinalityRestriction;
import liseda.matcha.semantics.owl.ClassIntersection;
import liseda.matcha.semantics.owl.ClassUnion;
import liseda.matcha.semantics.owl.DataAllValues;
import liseda.matcha.semantics.owl.DataExactCardinality;
import liseda.matcha.semantics.owl.DataHasValue;
import liseda.matcha.semantics.owl.DataMaxCardinality;
import liseda.matcha.semantics.owl.DataMinCardinality;
import liseda.matcha.semantics.owl.DataSomeValues;
import liseda.matcha.semantics.owl.Datatype;
import liseda.matcha.semantics.owl.ObjectAllValues;
import liseda.matcha.semantics.owl.ObjectHasValue;
import liseda.matcha.semantics.owl.ObjectSomeValues;
import liseda.matcha.semantics.owl.SimpleClass;
import liseda.matcha.semantics.owl.SimpleDataProperty;
import liseda.matcha.semantics.owl.SimpleObjectProperty;

public class Reasoner
{

	/**
	 * Compute the transitive closure of class (and expression) relationships by adding inherited
	 * relationships (and their distances). Implementation of the Semi-Naive Algorithm.
	 */

	public static void transitiveClosure()
	{
		SemanticMap s = SemanticMap.getInstance();
		//First perform subclass inferences for class unions and intersections
		for(String cx : s.getEntities(EntityType.CLASS_EXPRESSION))
		{
			Expression x = s.getExpression(cx);
			//A class that is a subclass or equivalent to an intersection
			//is a subclass of each class in the intersection
			if(x instanceof ClassIntersection)
			{
				List<Expression> components = x.getComponents();
				for(Expression e : components)
				{
					String uri = e.toString();
					for(String c : s.getEquivalences(cx, false))
						s.addSubclass(c, uri);
					for(String c : s.getSubclasses(cx, false))
						s.addSubclass(c, uri);
				}
			}
			//A class that is equivalent to a union is a superclass
			//of each class in the union
			if(x instanceof ClassUnion)
			{
				List<Expression> components = x.getComponents();
				for(Expression e : components)
				{
					String uri = e.toString();
					for(String c : s.getEquivalences(cx, false))
						s.addSubclass(uri, c);
				}
			}
		}
		//Then perform transitive closure for class relations
		Set<String> t = new HashSet<String>(s.getParents());
		int lastCount = 0;
		for(int distance = 1; lastCount != s.relationshipCount(); distance++)
		{
			lastCount = s.relationshipCount();
			for(String i : t)
			{
				Set<String> childs = s.getSubclasses(i,distance,false);
				Set<String> pars = s.getSuperclasses(i,1,true);
				pars.addAll(s.getEquivalences(i,true));
				for(String j : pars)
					for(String h : childs)
						s.addSubclass(h, j, s.getDistance(i,j) + s.getDistance(h,i));
			}
		}
	}

	/**
	 * Propagate type and property assertions of sameAs individuals
	 */
	public static void sameAsExpansion()
	{
		SemanticMap s = SemanticMap.getInstance();
		//For each individual
		for(String i1: s.getSameIndividuals()) 
		{
			//and each sameAs invidual
			for(String i2: s.getSameIndividuals(i1)) 
			{
				//1 - Add the types (classes) of the individual to same sameAs individual
				for(String eqClass: s.getIndividualClasses(i1)) 
					s.addInstance(i2, eqClass);
				//2 - Add the triples in which the individual is the subject to the sameAs individual
				for(String i3: s.getIndividualActiveRelations(i1)) 
				{
					for(String relation: s.getIndividualProperties(i1, i3)) 
					{
						//a - with the original predicate
						s.addIndividualRelationship(i2, i3, relation);
						//b - with all sameAs predicates
						for(String i4: s.getSameIndividuals(i3)) 
							s.addIndividualRelationship(i2, i4, relation);
					}	
				}
				//3 - Add the triples in which the individual is the predicate to the sameAs individual
				for(String i3: s.getIndividualPassiveRelations(i1)) 
				{
					for(String relation: s.getIndividualProperties(i3, i1)) 
					{
						//a - with the original subject
						s.addIndividualRelationship(i3, i2, relation);
						//b - with all sameAs subjects
						for(String i4: s.getSameIndividuals(i3)) 
							s.addIndividualRelationship(i4, i2, relation);
					}	
				}
			}	
		}
	}

	//TODO: Implement this
	public static void processSemanticDisjoints()
	{		

		SemanticMap s = SemanticMap.getInstance();
		//Incompatible cardinality restrictions for the same property
		Map2Map2Map<String, String, Integer, String> maxCard = new Map2Map2Map<String, String, Integer, String>();
		Map2Map2Map<String, String, Integer, String>  minCard = new Map2Map2Map<String, String, Integer, String> ();
		Map2Map2Map<String, String, Integer, String>  exact = new Map2Map2Map<String, String, Integer, String> ();
		//Different values for the same functional data property or incompatible value
		//restrictions on the same non-functional data property
		Map2Map<String, String, String> dataAllValues = new Map2Map<String,String,String>();
		Map2Map<String, String, String> dataHasValue = new Map2Map<String,String,String>();
		Map2Map<String, String, String> dataSomeValues = new Map2Map<String,String,String>();
		// Disjoint classes for the same functional object property or incompatible value
		// restrictions on disjoint classes for the same non-functional object property
		Map2Map<String, String, String> objectAllValues = new Map2Map<String,String,String>();
		Map2Map<String, String, String> objectHasValue = new Map2Map<String,String,String>();
		Map2Map<String, String, String> objectSomeValues = new Map2Map<String,String,String>();


		for(String cx : s.getEntities(EntityType.CLASS_EXPRESSION))
		{
			Expression x = s.getExpression(cx);
			List<Expression> components = x.getComponents();

			if(x instanceof DataMaxCardinality)
			{
				if(components.get(0) instanceof SimpleDataProperty)
				{
					if(((CardinalityRestriction)x).isQualified())
						maxCard.add(components.get(0).toString(), components.get(2).toString(), ((Cardinality)components.get(1)).getCardinality(), cx);
					else
						maxCard.add(components.get(0).toString(), " ", ((Cardinality)components.get(1)).getCardinality(), cx);
				}
			}
			else if(x instanceof DataMinCardinality)
			{
				if(components.get(0) instanceof SimpleDataProperty)
				{
					if(((CardinalityRestriction)x).isQualified())
					{
						minCard.add(components.get(0).toString(), components.get(2).toString(), ((Cardinality)components.get(1)).getCardinality(), cx);
						if(((Cardinality)components.get(1)).getCardinality() == 1)
							dataSomeValues.add(components.get(0).toString(), "1", cx);
					}	
					else
					{
						minCard.add(components.get(0).toString(), " ", ((Cardinality)components.get(1)).getCardinality(), cx);
						if(((Cardinality)components.get(1)).getCardinality() == 1)
							dataSomeValues.add(components.get(0).toString(), "1", cx);
					}
				}
			}
			else if(x instanceof DataExactCardinality)
			{
				if(components.get(0) instanceof SimpleDataProperty)
				{
					if(((CardinalityRestriction)x).isQualified())
						exact.add(components.get(0).toString(), components.get(2).toString(), ((Cardinality)components.get(1)).getCardinality(), cx);
					else
						exact.add(components.get(0).toString(), " ", ((Cardinality)components.get(1)).getCardinality(), cx);
				}
			}
			else if(x instanceof DataAllValues)
			{
				if(components.get(0) instanceof SimpleDataProperty)
					if(components.get(1) instanceof Datatype)
						dataAllValues.add(components.get(0).toString(), components.get(1).toString(), cx);
			}
			else if(x instanceof DataHasValue)
			{
				if(components.get(0) instanceof SimpleDataProperty)
					if(components.get(1) instanceof Datatype)
						dataHasValue.add(components.get(0).toString(), components.get(1).toString(), cx);
			}
			else if(x instanceof DataSomeValues)
			{
				if(components.get(0) instanceof SimpleDataProperty)
					if(components.get(1) instanceof Datatype)
						dataSomeValues.add(components.get(0).toString(), components.get(1).toString(), cx);
			}
			else if(x instanceof ObjectAllValues)
			{
				if(components.get(0) instanceof SimpleObjectProperty)
					if(components.get(1) instanceof SimpleClass)
						objectAllValues.add(components.get(0).toString(), components.get(1).toString(), cx);
			}
			else if(x instanceof ObjectHasValue)
			{
				if(components.get(0) instanceof SimpleObjectProperty)
					if(components.get(1) instanceof SimpleClass)
						objectHasValue.add(components.get(0).toString(), components.get(1).toString(), cx);
			}
			else if(x instanceof ObjectSomeValues)
			{
				if(components.get(0) instanceof SimpleObjectProperty)
					if(components.get(1) instanceof SimpleClass)
						objectSomeValues.add(components.get(0).toString(), components.get(1).toString(), cx);
			}
		}			

		//Finally process the semantically disjoint classes
		//Classes that have incompatible cardinalities on the same property
		//First exact cardinalities vs exact, min and max cardinalities
		for(String prop : exact.keySet())
		{
			//TODO: cardinality restrictions on object properties need to be processed differently
			for(String r : exact.keySet(prop)) {
				Vector<Integer> c = new Vector<Integer>(exact.get(prop, r).keySet());
				for(int i = 0; i < c.size()-1; i++) {
					for(int j = i + 1; j < c.size(); j++) {
						s.addDisjoint(exact.get(prop, r, c.get(i)), exact.get(prop, r, c.get(j)));
					}
				}
				if(maxCard.keySet(prop) != null) {
					Vector<Integer> max = new Vector<Integer>(maxCard.get(prop, r).keySet());
					for(int i = 0; i < c.size(); i++) {
						for(Integer j : max) {
							if(c.get(i) > max.get(j)) {
								s.addDisjoint(exact.get(prop, r, c.get(i)), maxCard.get(prop, r, max.get(j)));
							}
						}
					}
				}
				if(minCard.keySet(prop) != null) {
					Vector<Integer> min = new Vector<Integer>(minCard.get(prop, r).keySet());
					for(int i = 0; i < c.size(); i++) {
						for(Integer j : min) {
							if(c.get(i) < min.get(j)) {
								s.addDisjoint(exact.get(prop, r, c.get(i)), minCard.get(prop, r, min.get(j)));
							}
						}
					}
				}

			}			
		}
		//Then min vs max cardinalities
		for(String prop : minCard.keySet())
		{
			//TODO: cardinality restrictions on object properties need to be processed differently
			for(String r : minCard.keySet(prop)) {
				Vector<Integer> min = new Vector<Integer>(minCard.get(prop, r).keySet());
				if(maxCard.keySet(prop) != null) {
					Vector<Integer> max = new Vector<Integer>(maxCard.get(prop, r).keySet());
					for(Integer i : min)
						for(Integer j : max)
							if(i > j)
								s.addDisjoint(minCard.get(prop, r, min.get(i)), maxCard.get(prop, r, min.get(j)));
				}
			}
		
		}
		//Data properties with incompatible values
		//First hasValue restrictions on functional data properties
		for(String prop : dataHasValue.keySet())
		{
			Vector<String> cl = new Vector<String>(dataHasValue.keySet(prop));
			for(int i = 0; i < cl.size()-1; i++)
				for(int j = i+1; j < cl.size(); j++)
					if(!dataHasValue.get(prop, cl.get(i)).equals(dataHasValue.get(prop, cl.get(j))))
						s.addDisjoint(cl.get(i), cl.get(j));
		}
		//Then incompatible someValues restrictions on functional data properties
		for(String prop : dataSomeValues.keySet())
		{
			Vector<String> cl = new Vector<String>(dataSomeValues.keySet(prop));
			for(int i = 0; i < cl.size()-1; i++)
			{
				for(int j = i+1; j < cl.size(); j++)
				{
					String[] datatypes = dataSomeValues.get(prop, cl.get(j)).split(" ");
					for(String d: datatypes)
					{
						if(!dataSomeValues.get(prop, cl.get(i)).contains(d))
						{
							s.addDisjoint(cl.get(i), cl.get(j));
							break;
						}
					}
				}
			}
		}
		//Then incompatible allValues restrictions on all data properties
		//(allValues vs allValues and allValues vs someValues)
		for(String prop : dataAllValues.keySet())
		{
			Vector<String> cl = new Vector<String>(dataAllValues.keySet(prop));
			for(int i = 0; i < cl.size()-1; i++)
			{
				for(int j = i+1; j < cl.size(); j++)
				{
					String[] datatypes = dataAllValues.get(prop, cl.get(j)).split(" ");
					for(String d: datatypes)
					{
						if(!dataAllValues.get(prop, cl.get(i)).contains(d))
						{
							s.addDisjoint(cl.get(i), cl.get(j));
							break;
						}
					}
				}
			}
			Set<String> sv = dataSomeValues.keySet(prop);
			if(sv == null)
				continue;
			for(String i : cl)
			{
				for(String j : sv)
				{
					String[] datatypes = dataSomeValues.get(prop, j).split(" ");
					for(String d: datatypes)
					{
						if(!dataAllValues.get(prop, i).contains(d))
						{
							s.addDisjoint(i, j);
							break;
						}
					}
				}
			}
		}
		//Classes with incompatible value restrictions for the same object property
		//(i.e., the restrictions point to disjoint classes)
		//First allValues restrictions
		for(String prop : objectAllValues.keySet())
		{
			Vector<String> cl = new Vector<String>(objectAllValues.keySet(prop));
			for(int i = 0; i < cl.size() - 1; i++)
			{
				String c1 = objectAllValues.get(prop, cl.get(i));
				for(int j = i + 1; j < cl.size(); j++)
				{
					String c2 = objectAllValues.get(prop, cl.get(j));
					if(!c1.equals(c2) && s.areDisjoint(c1, c2))
						s.addDisjoint(cl.get(i), cl.get(j));
				}
			}

			Set<String> sv = objectSomeValues.keySet(prop);
			if(sv == null)
				continue;
			for(String i : cl)
			{
				String c1 = objectAllValues.get(prop, i);
				for(String j : sv)
				{
					String c2 = objectSomeValues.get(prop, j);
					if(!c1.equals(c2) && s.areDisjoint(c1, c2))
						s.addDisjoint(i, j);
				}
			}
		}
		//Finally someValues restrictions on functional properties
		for(String prop : objectSomeValues.keySet())
		{
			if(!s.isFunctional(prop))
				continue;
			Set<String> sv = objectSomeValues.keySet(prop);
			if(sv == null)
				continue;
			Vector<String> cl = new Vector<String>(sv);
			for(int i = 0; i < cl.size() - 1; i++)
			{
				String c1 = objectSomeValues.get(prop, cl.get(i));
				for(int j = i + 1; j < cl.size(); j++)
				{
					String c2 = objectSomeValues.get(prop, cl.get(j));
					if(!c1.equals(c2) && s.areDisjoint(c1, c2))
						s.addDisjoint(cl.get(i), cl.get(j));
				}
			}
		}

	}
}
