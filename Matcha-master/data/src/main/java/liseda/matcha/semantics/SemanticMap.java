/*******************************************************************************
 * Registry of entities in all open ontologies and all of their semantic       *
 * relations and restrictions                                                  *
 *                                                                             *
 * @author Daniel Faria                                                        *
 ******************************************************************************/
package liseda.matcha.semantics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import liseda.matcha.data.Map2Map;
import liseda.matcha.data.Map2Map2Set;
import liseda.matcha.data.Map2Set;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.owl.ObjectPropertyChain;


public class SemanticMap
{

//Attributes

	//0) The singleton instance of the SemanticMap
	private static SemanticMap sm = new SemanticMap();
	
	//1) The map of entities to EntityTypes 
	private Map2Set<String,EntityType> entityType;
	private Map2Set<EntityType,String> typeEntity;
	
	//2) Map of expression ids (String form of the expression) to Expression objects
	private HashMap<String,Expression> expressions;
	private HashMap<Expression,String> expressionIds;
	
	//3) Class & class expression semantics
	//Table of class-ancestor relations <class, ancestor, distance> (distance=0 -> equivalence)
	private Map2Map<String,String,Integer> ancestorClasses;
	//Table of class-descendant relations <class, descendant, distance> (distance=0 -> equivalence)
	private Map2Map<String,String,Integer> descendantClasses;
	//Table of disjointness between classes <class/expression, disjoint class/expression>
	private Map2Set<String,String> disjointMap;
	//Set of high level classes
	private HashSet<String> highLevelClasses;
	
	//4) Individual semantics
	//Table of individual-class instancing <individual, class>
	private Map2Set<String,String> instanceOfMap;
	//Table of class-individual instancing <class, individual>
	private Map2Set<String,String> hasInstanceMap;
	//Table of equivalent individuals <individual, equivalent individual>	
	private Map2Set<String,String> sameIndivAs;
	//Table of relations for which the individual is the subject <individual, related individual, property>	
	private Map2Map2Set<String,String,String> activeRelation;
	//Table of relations for which the individual is the object <individual, related individual, property>
	private Map2Map2Set<String,String,String> passiveRelation;
	//Table of individual relations by property <property, subject individual, object individual>	
	private Map2Map2Set<String,String,String> propertyInstances; 

	//5) Property semantics
	//Table of subproperties <property, subproperty>
	private Map2Set<String,String> subProp;
	//Table of superproperties <property, superproperty>
	private Map2Set<String,String> superProp;
	//Table of inverse properties <property/expression, inverse property>
	private Map2Set<String,String> inverseProp;
	//Property domains <property, class expression>
	private HashMap<String,String> domain;
	//Property ranges <property, class expression/data range>
	private HashMap<String,String> range;
	//Property properties (asymmetric, functional, inverseFunctional, irreflexive, reflexive, and symmetric)
	private Map2Set<String,PropertyProperty> propProps;
	//Property chains (e.g. father * father = grandfather, transitive over: P * Q = P) <property, property chain>
	private Map2Set<String,ObjectPropertyChain> propChain;
	private Map2Set<ObjectPropertyChain,String> chainProp;

//Constructors

	/**
	 * Creates a new empty RelationshipMap
	 */
	private SemanticMap()
	{
		entityType = new Map2Set<String,EntityType>();
		typeEntity = new Map2Set<EntityType,String>();
		descendantClasses = new Map2Map<String,String,Integer>();
		ancestorClasses = new Map2Map<String,String,Integer>();
		highLevelClasses = new HashSet<String>();
		disjointMap = new Map2Set<String,String>();
		expressions = new HashMap<String,Expression>();
		expressionIds = new HashMap<Expression,String>();
		instanceOfMap = new Map2Set<String,String>();
		hasInstanceMap = new Map2Set<String,String>();
		sameIndivAs = new Map2Set<String,String>();
		activeRelation = new Map2Map2Set<String,String,String>();
		propertyInstances = new Map2Map2Set<String,String,String>();
		passiveRelation = new Map2Map2Set<String,String,String>();
		subProp = new Map2Set<String,String>();
		superProp = new Map2Set<String,String>();
		inverseProp = new Map2Set<String,String>();
		domain = new HashMap<String,String>();
		range = new HashMap<String,String>();
		propProps = new Map2Set<String,PropertyProperty>();
		propChain = new Map2Set<String,ObjectPropertyChain>();
		chainProp = new Map2Set<ObjectPropertyChain,String>();
	}

//Public Methods

	/**
	 * Sets a  property as asymmetric
	 * @param prop: the property to set as asymmetric
	 */
	public void addAsymmetric(String prop)
	{
		propProps.add(prop, PropertyProperty.ASYMMETRIC);
	}

	/**
	 * Adds a new disjointness relations between two classes
	 * @param class1: the uri of the first disjoint class
	 * @param class2: the uri of the second disjoint class
	 */
	public void addDisjoint(String class1, String class2)
	{
		if(!class1.equals(class2))
		{
			disjointMap.add(class1, class2);
			disjointMap.add(class2, class1);
		}
	}

	/**
	 * Adds a new domain (class) to a given property
	 * @param propId: the uri of the property with the domain
	 * @param uri: the uri of the class in the domain of the property
	 */
	public void addDomain(String propId, String uri)
	{
		domain.put(propId, uri);
	}

	/**
	 * Adds a new relation between a class and a class expression
	 * @param uri: the uri of the entity
	 * @param t: the type of the entity
	 */
	public void addEntity(String uri, EntityType t)
	{
		entityType.add(uri, t);
		typeEntity.add(t, uri);
	}
	
	/**
	 * Adds an equivalence relationship between two classes
	 * @param class1: the uri of the first equivalent class
	 * @param class2: the uri of the second equivalent class
	 */
	public void addEquivalentClasses(String class1, String class2)
	{
		//Get existing equivalences for both classes
		Set<String> eq1 = getEquivalences(class1,true);
		Set<String> eq2 = getEquivalences(class2,true);
		//Add the equivalence
		addSubclass(class1,class2,0);
		//Add all transitive equivalences
		for(String e1 : eq1)
			addSubclass(e1,class2,0);
		for(String e2 : eq2)
			addSubclass(class1,e2,0);
	}

	/**
	 * Adds an equivalence relationship between two classes
	 * @param class1: the uri of the first equivalent class
	 * @param class2: the uri of the second equivalent class
	 */
	public void addSameAsIndividuals(String indiv1, String indiv2)
	{
		//Get existing sameAsIndividuals for both individuals
		Set<String> sa1 = this.getSameIndividuals(indiv1);
		Set<String> sa2 = this.getSameIndividuals(indiv2);
		//Add the sameAs bidirectionally
		sameIndivAs.add(indiv1, indiv2);
		sameIndivAs.add(indiv2, indiv1);
		//Add all transitive sameAs
		for(String s1 : sa1)
		{
			sameIndivAs.add(s1, indiv2);
			sameIndivAs.add(indiv2, s1);
		}
		for(String s2 : sa2)
		{
			sameIndivAs.add(indiv1, s2);
			sameIndivAs.add(s2, indiv1);
		}
	}

	/**
	 * Adds an exact cardinality restriction to the EntityMap
	 * @param exp: the uri of the allValues class expression
	 * @param prop: the restricted property
	 * @param range: the restricted range (class expression)
	 * @param int: the restricted cardinality
	 */
	public void addExpression(Expression x)
	{
		String uri = x.toString();
		if(!entityType.contains(uri))
		{
			entityType.add(uri, x.getEntityType());
			typeEntity.add(x.getEntityType(), uri);			
			expressions.put(uri, x);
			expressionIds.put(x, uri);
		}
	}

	/**
	 * @param prop: the property to set as functional
	 */
	public void addFunctional(String prop)
	{
		propProps.add(prop, PropertyProperty.FUNCTIONAL);
	}

	/**
	 * Adds a relationship between two individuals through a given property
	 * @param indiv1: the uri of the first individual
	 * @param indiv2: the uri of the second individual
	 * @param prop: the property in the relationship
	 */
	public void addIndividualRelationship(String indiv1, String indiv2, String prop)
	{
		activeRelation.add(indiv1,indiv2,prop);
		propertyInstances.add(prop, indiv1,indiv2);
		passiveRelation.add(indiv2,indiv1,prop);
	}

	/**
	 * Adds an instantiation relationship between an individual and a class
	 * @param individualId: the uri of the individual
	 * @param uri: the uri of the class
	 */
	public void addInstance(String individualId, String uri)
	{
		instanceOfMap.add(individualId,uri);
		hasInstanceMap.add(uri,individualId);
	}

	/**
	 * Adds a new inverse relationship between two properties if it doesn't exist
	 * @param property1: the uri of the first property
	 * @param property2: the uri of the second property
	 */
	public void addInverseProp(String property1, String property2)
	{
		if(!property1.equals(property2))
		{
			inverseProp.add(property1, property2);
			inverseProp.add(property2, property1);
		}
	}
	
	/**
	 * @param prop: the property to set as inverseFunctional
	 */
	public void addInverseFunctional(String prop)
	{
		propProps.add(prop, PropertyProperty.INVERSE_FUNCTIONAL);
	}

	/**
	 * @param prop: the property to set as irreflexive
	 */
	public void addIrreflexive(String prop)
	{
		propProps.add(prop, PropertyProperty.IRREFLEXIVE);
	}

	/**
	 * Adds a property chain axiom to a given property
	 * @param prop: the uri of the property that is a subproperty of the chain
	 * @param chain: the property chain that is a superproperty of the prop
	 */
	public void addPropertyChain(String prop, ObjectPropertyChain chain)
	{
		propChain.add(prop, chain);
		chainProp.add(chain, prop);
	}

	/**
	 * Adds a new range (class) to a given object property
	 * @param propId: the uri of the property with the range
	 * @param uri: the uri of the class in the range of the property
	 */
	public void addRange(String propId, String uri)
	{
		range.put(propId, uri);
	}

	/**
	 * @param prop: the property to set as reflexive
	 */
	public void addReflexive(String prop)
	{
		propProps.add(prop, PropertyProperty.REFLEXIVE);
	}

	/**
	 * Adds a direct hierarchical relationship between two classes
	 * @param child: the uri of the child class
	 * @param parent: the uri of the parent class
	 */
	public void addSubclass(String child, String parent)
	{
		addSubclass(child,parent,1);
	}

	/**
	 * Adds a subclass relationship between two classes with a given distance
	 * @param child: the uri of the child class
	 * @param parent: the uri of the parent class
	 * @param distance: the distance (number of edges) between the classes
	 */
	public void addSubclass(String child, String parent, int distance)
	{
		descendantClasses.add(parent,child,distance);
		ancestorClasses.add(child,parent,distance);
	}

	/**
	 * Adds a relationship between two properties
	 * @param child: the uri of the child property
	 * @param parent: the uri of the parent property
	 */
	public void addSubproperty(String child, String parent)
	{
		subProp.add(parent,child);
		superProp.add(child,parent);
	}

	/**
	 * @param prop: the property to set as symmetric
	 */
	public void addSymmetric(String prop)
	{
		propProps.add(prop, PropertyProperty.SYMMETRIC);
	}

	/**
	 * @param prop: the property to set as transitive
	 */
	public void addTransitive(String prop)
	{
		propProps.add(prop, PropertyProperty.TRANSITIVE);
	}

	/**
	 * @param uri: the uri to add to AML
	 */
	public void addURI(String uri, EntityType t)
	{
		entityType.add(uri,t);
		typeEntity.add(t,uri);
	}

	/**
	 * @param class1: the first class to check for disjointness
	 * @param class2: the second class to check for disjointness
	 * @return whether one and two are disjoint considering transitivity
	 */
	public boolean areDisjoint(String class1, String class2)
	{
		//Get the transitive disjoint clauses involving class one
		Set<String> disj = getDisjointTransitive(class1);
		if(disj.size() > 0)
		{
			//Then get the list of superclasses of class two
			Set<String> ancs = new HashSet<String>(getSuperclasses(class2,true));
			//Including class two itself
			ancs.add(class2);

			//Two classes are disjoint if the list of transitive disjoint clauses
			//involving one of them contains the other or any of its 'is_a' ancestors
			for(String i : ancs)
				if(disj.contains(i))
					return true;
		}
		return false;
	}

	/**
	 * @param child: the uri of the child class
	 * @param parent: the uri of the parent class
	 * @return whether the RelationshipMap contains a relationship between child and parent
	 */
	public boolean areRelatedClasses(String child, String parent)
	{
		return descendantClasses.contains(parent,child);
	}

	/**
	 * Checks whether an individual belongs to a class
	 * @param indivId: the uri of the individual to check
	 * @param uri: the uri of the class to check
	 * @return whether indivId is an instance of uri or
	 * of one of its subclasses
	 */
	public boolean belongsToClass(String indivId, String uri)
	{
		if(instanceOfMap.contains(indivId, uri))
			return true;
		for(String suburi : getSubclasses(uri,false))
			if(instanceOfMap.contains(indivId, suburi))
				return true;
		return false;	
	}
	
	/**
	 * Checks whether an individual belongs to an ontology
	 * @param indivId: the uri of the individual to check
	 * @param o: the ontology to check
	 * @return whether indivId belongs to more classes in o
	 * than classes not in o
	 */
	public boolean belongsToOntology(String indivId, Ontology o)
	{
		if(!o.contains(indivId))
			return false;
		int trueCount = 0;
		int falseCount = 0;
		for(String classURI : getIndividualClassesTransitive(indivId))
		{
			if(o.contains(classURI))
				trueCount++;
			else
				falseCount++;
		}
		return (trueCount > 0 && trueCount > falseCount);
	}
	
	/**
	 * Resets the SemanticMap instance to a new empty SemanticMap
	 * @warning: use only if you want to start a new matching problem
	 */
	public static void clear()
	{
		sm = new SemanticMap();
	}

	/**
	 * @param uri: the uri to search in the EntityMap
	 * @return whether the EntityMap contains the uri
	 */
	public boolean contains(String uri)
	{
		return entityType.contains(uri);
	}

	/**
	 * @return the number of disjoint clauses
	 */
	public int disjointCount()
	{
		//The size is divided by 2 since the disjoint
		//clauses are stored in both directions
		return disjointMap.size()/2;
	}

	/**
	 * @return the number of entities registered in the EntityMap
	 */
	public int entityCount()
	{
		return entityType.keyCount();
	}

	/**
	 * @param propId: the id of the property to search in the map
	 * @return the Map2Set of individuals who are related through the given property
	 */
	public Map2Set<String, String> getActiveRelationIndividuals(String propId)
	{
		if(propertyInstances.contains(propId))
			return propertyInstances.get(propId);
		return new Map2Set<String, String>();
	}
	/**
	 * @return the set of classes with ancestors in the map
	 */
	public Set<String> getChildren()
	{
		if(ancestorClasses != null)
			return ancestorClasses.keySet();
		return new HashSet<String>();
	}
	
	/**
	 * @param uri: the id of the class to search in the map
	 * @return the set of expressions that the given class is equivalent to or a subclass of
	 */
	public Set<String> getClassExpressions(String uri)
	{
		HashSet<String> exp = new HashSet<String>();
		if(ancestorClasses.contains(uri))
		{
			exp.addAll(ancestorClasses.keySet(uri));
			exp.retainAll(typeEntity.get(EntityType.CLASS_EXPRESSION));
		}
		return exp;
	}

	/**
	 * @param uri: the id of the class to search in the map
	 * @param equiv: whether to get equivalent or subclass expressions
	 * @return the set of expressions related to the given class according to isEquiv
	 */
	public Set<String> getClassExpressions(String uri, boolean equiv)
	{
		HashSet<String> exp = new HashSet<String>();
		if(ancestorClasses.contains(uri))
		{
			for(String ancestor : ancestorClasses.keySet(uri))
				if(typeEntity.get(EntityType.CLASS_EXPRESSION).contains(ancestor) && 
						((equiv && ancestorClasses.get(uri, ancestor) == 0) ||
						(!equiv && ancestorClasses.get(uri, ancestor) > 0)))
					exp.add(ancestor);
		}
		return exp;
	}

	/**
	 * @param uri: the id of the class to search in the map
	 * @return the list of individuals that instantiate the given class
	 */
	public Set<String> getClassIndividuals(String uri)
	{
		if(hasInstanceMap.contains(uri))
			return hasInstanceMap.get(uri);
		return new HashSet<String>();
	}

	/**
	 * @param uri: the id of the class to search in the map
	 * @return the set of individuals that instantiate the given class
	 * or any of its subclasses
	 */
	public Set<String> getClassIndividualsTransitive(String uri)
	{
		HashSet<String> individuals = new HashSet<String>();
		if(hasInstanceMap.contains(uri))
			individuals.addAll(hasInstanceMap.get(uri));
		for(String s : getSubclasses(uri,false))
			if(hasInstanceMap.contains(s))
				individuals.addAll(hasInstanceMap.get(s));
		return individuals;
	}
	
	/**
	 * @param classes: the set the class to search in the map
	 * @return the list of direct subclasses shared by the set of classes
	 */
	public Set<String> getCommonSubClasses(Set<String> classes)
	{
		if(classes == null || classes.size() == 0)
			return null;
		Iterator<String> it = classes.iterator();
		Vector<String> subclasses = new Vector<String>(getSubclasses(it.next(),1,false));
		while(it.hasNext())
		{
			HashSet<String> s = new HashSet<String>(getSubclasses(it.next(),1,false));
			for(int i = 0; i < subclasses.size(); i++)
			{
				if(!s.contains(subclasses.get(i)))
				{
					subclasses.remove(i);
					i--;
				}
			}
		}
		for(int i = 0; i < subclasses.size()-1; i++)
		{
			for(int j = i+1; j < subclasses.size(); j++)
			{
				if(isSubclass(subclasses.get(i),subclasses.get(j)))
				{
					subclasses.remove(i);
					i--;
					j--;
				}
				if(isSubclass(subclasses.get(j),subclasses.get(i)))
				{
					subclasses.remove(j);
					j--;
				}
			}
		}
		return new HashSet<String>(subclasses);
	}

	/**
	 * @return the set of classes that have disjoint clauses
	 */
	public Set<String> getDisjoint()
	{
		return disjointMap.keySet();
	}

	/**
	 * @param uri: the id of the class to search in the map
	 * @return the list of classes disjoint with the given class
	 */
	public Set<String> getDisjoint(String uri)
	{
		if(disjointMap.contains(uri))
			return disjointMap.get(uri);
		return new HashSet<String>();
	}

	/**
	 * @param uri: the id of the class to search in the map
	 * @return the list of classes disjoint with the given class
	 * or any of its ancestors
	 */
	public Set<String> getDisjointTransitive(String uri)
	{
		//Get the disjoint clauses for the class/expression
		Set<String> disj = getDisjoint(uri);
		//Then get all superclasses of the class
		Set<String> ancestors = getSuperclasses(uri,true);
		//For each superclass
		for(String i : ancestors)
			//Add its disjoint clauses to the list
			disj.addAll(getDisjoint(i));
		return disj;
	}

	/**
	 * @param child: the uri of the child class
	 * @param parent: the uri of the parent class
	 * @return the minimal distance between the child and parent,
	 * or 0 if child==parent, or -1 if they aren't related
	 */
	public int getDistance(String child, String parent)
	{
		if(child.equals(parent))
			return 0;
		if(!ancestorClasses.contains(child, parent))
			return -1;
		return ancestorClasses.get(child,parent);
	}

	/**
	 * @param propId: the id of the property to search in the map
	 * @return the domain of the input property
	 */
	public String getDomain(String propId)
	{
		return domain.get(propId);
	}

	/**
	 * @param t: the EntityType to search in the map
	 * @return the set of entities of the given type
	 */
	public Set<String> getEntities(EntityType t)
	{
		return typeEntity.get(t);
	}

	/**
	 * @param uri: the id of the class to search in the map
	 * @param includeExpressions: whether to include class expressions
	 * @return the set of equivalences of the given class
	 */
	public Set<String> getEquivalences(String uri, boolean includeExpressions)
	{
		return getSubclasses(uri, 0, includeExpressions);
	}

	/**
	 * @param id: the string form of the Expression
	 * @return the Expression associated with the string form
	 */
	public Expression getExpression(String id)
	{
		return expressions.get(id);
	}
	
	/**
	 * @param uri: the id of the class to search in the map
	 * @return the set of high level ancestors of the given class
	 */
	public Set<String> getHighLevelAncestors(String uri)
	{
		Set<String> ancestors = getSuperclasses(uri,false);
		HashSet<String> highAncs = new HashSet<String>();
		for(String i : ancestors)
			if(highLevelClasses.contains(i))
				highAncs.add(i);
		return highAncs;
	}

	/**
	 * @return the set of high level classes in an ontology
	 */
	public Set<String> getHighLevelClasses()
	{
		return highLevelClasses;
	}

	/**
	 * @return the whole table of active relations
	 */
	public Map2Map2Set<String,String,String> getIndividualActiveRelations()
	{
		return activeRelation;
	}

	/**
	 * @param indivId: the id of the individual to search in the map
	 * @return the set of individuals to which the given individual is actively related
	 */
	public Set<String> getIndividualActiveRelations(String indivId)
	{
		if(activeRelation.contains(indivId)) 
			return activeRelation.keySet(indivId);
		return new HashSet<String>();
	}


	/**
	 * @param indivId: the id of the individual to search in the map
	 * @return the set of classes instanced by the given individual
	 */
	public Set<String> getIndividualClasses(String indivId)
	{
		if(instanceOfMap.contains(indivId))
			return instanceOfMap.get(indivId);
		return new HashSet<String>();
	}

	/**
	 * @param indivId: the id of the individual to search in the map
	 * @return the set of classes instanced by the given individual and its ancestors (i.e. with transitive closure)
	 */
	public Set<String> getIndividualClassesTransitive(String indivId)
	{
		Set<String> result = new HashSet<String>();
		if(instanceOfMap.contains(indivId)) 
		{
			for (String c: instanceOfMap.get(indivId)) 
			{
				result.add(c);
				result.addAll(getSuperclasses(c,false));
			}
			return result;
		}
		return new HashSet<String>();
	}

	/**
	 * @return the connectivity of individuals
	 */
	public double getIndividualConnectivity()
	{
		return Math.min(activeRelation.keySet().size(),passiveRelation.keySet().size()) * 1.0 /
				typeEntity.get(EntityType.INDIVIDUAL).size();
	}

	/**
	 * @param indivId: the id of the individual to search in the map
	 * @return the set of individuals that are passively related with of the given individual
	 */
	public Set<String> getIndividualPassiveRelations(String indivId)
	{
		if(passiveRelation.contains(indivId))
			return passiveRelation.keySet(indivId);
		return new HashSet<String>();
	}

	/**
	 * @param sourceInd: the id of the source individual in the relation
	 * @param targetInd: the id of the target individual in the relation
	 * @return the set of object properties actively relating sourceInd to targetInd
	 */
	public Set<String> getIndividualProperties(String sourceInd, String targetInd)
	{
		if(activeRelation.contains(sourceInd,targetInd))
			return activeRelation.get(sourceInd,targetInd);
		return new HashSet<String>();
	}

	/**
	 * @return the set of individuals with active relations
	 */
	public Set<String> getIndividualsWithActiveRelations()
	{
		return activeRelation.keySet();
	}

	/**
	 * @return the set of individuals with active relations
	 */
	public Set<String> getIndividualsWithPassiveRelations()
	{
		return passiveRelation.keySet();
	}

	/**
	 * @return the instance of the SemanticMap
	 */
	public static SemanticMap getInstance()
	{
		return sm;
	}
	
	/**
	 * @param indivId: the id of the individual to search in the map
	 * @return the set of classes instanced by the given individual
	 */
	public Set<String> getInstancedClasses()
	{
		return hasInstanceMap.keySet();
	}

	/**
	 * @param propId: the id of the property to search in the map
	 * @return the set of inverse properties of the input property
	 */
	public Set<String> getInverseProperties(String propId)
	{
		if(inverseProp.contains(propId))
			return new HashSet<String>(inverseProp.get(propId));
		else
			return new HashSet<String>();
	}


	/**
	 * @return the set of classes with ancestors in the map
	 */
	public Set<String> getParents()
	{
		if(descendantClasses != null)
			return descendantClasses.keySet();
		return new HashSet<String>();
	}

	/**
	 * @param indivId: the id of the individual to search in the map
	 * @param prop: the property relating the individuals
	 * @return the set of 'parent' relations of the given individual
	 */
	public Set<String> getParentIndividuals(String indivId, String prop)
	{
		if(activeRelation.contains(indivId,prop))
			return activeRelation.get(indivId,prop);
		return new HashSet<String>();
	}

	/**
	 * @param prop: the uri of the property to get
	 * @return the set of property chains that are a superpropery of the prop
	 */
	public Set<ObjectPropertyChain> getPropertyChain(String prop)
	{
		return propChain.get(prop);
	}
	
	/**
	 * @param chain: the property chain to get
	 * @return the set of properties that are a subproperty of the property chain
	 */
	public Set<String> getPropertyChain(ObjectPropertyChain chain)
	{
		return chainProp.get(chain);
	}

	/**
	 * @param propId: the id of the property to search in the map
	 * @return the range of the input property
	 */
	public String getRange(String propId)
	{
		return range.get(propId);
	}

	/**
	 * @return the set of individuals that have sameAs declarations
	 */
	public Set<String> getSameIndividuals()
	{
		return sameIndivAs.keySet();
	}
	
	/**
	 * @param uri: the id of the individual to search in the map
	 * @return the set of individuals that are the same as the given individual
	 */
	public Set<String> getSameIndividuals(String uri)
	{
		if(!sameIndivAs.contains(uri))
			return new HashSet<String>();
		return sameIndivAs.get(uri);
	}
	
	/**
	 * @param uri: the id of the class to search in the map
	 * @param prop: the relationship property between the class and its ancestors
	 * @return the set of strict siblings of the given class (through the subclass relation)
	 */
	public Set<String> getSiblings(String uri)
	{
		Set<String> parents = getSuperclasses(uri,1,true);
		HashSet<String> siblings = new HashSet<String>();
		for(String i : parents)
		{
			Set<String> children = getSubclasses(i,1,false);
			for(String j : children)
				if(!j.equals(uri))
					siblings.add(j);
		}
		return siblings;
	}

	/**
	 * @param uri: the id of the class to search in the map
	 * @param includeExpressions: whether to include class expressions
	 * @return the set of subclasses of the input class
	 */
	public Set<String> getSubclasses(String uri, boolean includeExpressions)
	{
		HashSet<String> sub = new HashSet<String>();
		if(descendantClasses.contains(uri))
		{
			for(String d : descendantClasses.keySet(uri))
				if(includeExpressions || isClass(d))
					sub.add(d);
		}
		return sub;
	}

	/**
	 * @param uri: the id of the class to search in the map
	 * @param distance: the distance between the class and its subclasses
	 * @param includeExpressions: whether to include class expressions
	 * @return the set of subclasses at the given distance from the input class
	 */
	public Set<String> getSubclasses(String uri, int distance, boolean includeExpressions)
	{
		HashSet<String> sub = new HashSet<String>();
		if(descendantClasses.contains(uri))
		{
			for(String d : descendantClasses.keySet(uri))
				if(descendantClasses.get(uri, d) == distance && (includeExpressions || isClass(d)))
					sub.add(d);
		}
		return sub;
	}


	/**
	 * @param propId: the id of the property to search in the map
	 * @return the set of sub-properties of the input property
	 */
	public Set<String> getSubproperties(String propId)
	{
		if(subProp.contains(propId))
			return new HashSet<String>(subProp.get(propId));
		else
			return new HashSet<String>();
	}

	/**
	 * @param uri: the id of the class to search in the map
	 * @param includeExpressions: whether to include class expressions
	 * @return the set of all superclasses of the given class
	 */
	public Set<String> getSuperclasses(String uri, boolean includeExpressions)
	{
		HashSet<String> sup = new HashSet<String>();
		if(ancestorClasses.contains(uri))
		{
			for(String a : ancestorClasses.keySet(uri))
				if(includeExpressions || isClass(a))
					sup.add(a);
		}
		return sup;
	}

	/**
	 * @param uri: the uri of the class to search in the map
	 * @param distance: the distance between the class and its superclasses
	 * @param includeExpressions: whether to include class expressions
	 * @return the set of superclasses at the given distance from the input class
	 */
	public Set<String> getSuperclasses(String uri, int distance, boolean includeExpressions)
	{
		HashSet<String> sup = new HashSet<String>();
		if(ancestorClasses.contains(uri))
		{
			for(String a : ancestorClasses.keySet(uri))
				if(ancestorClasses.get(uri,a) == distance && (includeExpressions || isClass(a)))
					sup.add(a);
		}
		return sup;
	}

	/**
	 * @param propId: the id of the property to search in the map
	 * @return the set of super-properties of the input property
	 */
	public Set<String> getSuperproperties(String propId)
	{
		if(superProp.contains(propId))
			return new HashSet<String>(superProp.get(propId));
		else
			return new HashSet<String>();
	}

	/**
	 * @return the set of transitive object properties
	 */
	public Set<String> getTransitiveProperties()
	{
		HashSet<String> transitive = new HashSet<String>();
		for(String p : typeEntity.get(EntityType.OBJECT_PROP))
			if(isTransitive(p))
				transitive.add(p);
		return transitive;
	}

	/**
	 * @param uri: the uri of the Ontology entity
	 * @return the EntityTypes of the entity
	 */
	public Set<EntityType> getTypes(String uri)
	{
		if(entityType.contains(uri))
			return entityType.get(uri);
		return new HashSet<EntityType>();
	}

	/**
	 * @return the URIs in the EntityMap
	 */
	public Set<String> getURIs()
	{
		return entityType.keySet();
	}
	/**
	 * @param class: the uri of the class to search in the map
	 * @return whether there is a disjoint clause associated with the class
	 */
	public boolean hasDisjoint(String uri)
	{
		return disjointMap.contains(uri);
	}

	/**
	 * @param uri: the uri of the class to search in the map
	 * @return whether there is a disjoint clause associated with the class
	 * or any of its 'is_a' ancestors
	 */
	public boolean hasDisjointTransitive(String uri)
	{
		//Get all superclasses of the class
		Set<String> ancestors = getSuperclasses(uri,true);
		//Plus the parent itself
		ancestors.add(uri);
		//Run through the set of superclasses
		for(String i : ancestors)
			//And check if any have disjoint clauses
			if(disjointMap.contains(i))
				return true;
		return false;
	}

	/**
	 * @param one: the first class to check for disjointness
	 * @param two: the second class to check for disjointness
	 * @return whether there is a disjoint clause between one and two
	 */
	public boolean hasDisjointClause(String one, String two)
	{
		return (disjointMap.contains(one) && disjointMap.contains(one,two));
	}

	/**
	 * @return the number of instantiations in the map
	 */
	public int individualRelationshipCount()
	{
		return activeRelation.size();
	}

	/**
	 * @return the number of instantiations in the map
	 */
	public int instanceCount()
	{
		return instanceOfMap.size();
	}

	/**
	 * @param prop: the uri of the property to check
	 * @return whether the property is asymmetric
	 */
	public boolean isAsymmetric(String prop)
	{
		return propProps.contains(prop) && propProps.get(prop).contains(PropertyProperty.ASYMMETRIC);
	}

	/**
	 * @param uri: the uri of the Ontology entity
	 * @return whether the entity is a Class
	 */
	public boolean isClass(String uri)
	{
		return entityType.contains(uri) && entityType.get(uri).contains(EntityType.CLASS);
	}

	/**
	 * @param uri: the uri of the Ontology entity
	 * @return whether the entity is a Data Property
	 */
	public boolean isDataProperty(String uri)
	{
		return entityType.contains(uri) && entityType.get(uri).contains(EntityType.DATA_PROP);
	}

	/**
	 * @param uri: the uri of the Ontology entity
	 * @return whether the entity is an Expression
	 */
	public boolean isExpression(String uri)
	{
		return expressions.containsKey(uri);
	}
	
	/**
	 * @param prop: the uri of the property to check
	 * @return whether the property is functional
	 */
	public boolean isFunctional(String prop)
	{
		return propProps.contains(prop) && propProps.get(prop).contains(PropertyProperty.FUNCTIONAL);
	}

	/**
	 * @param uri: the uri of the Ontology entity
	 * @return whether the entity is an Individual
	 */
	public boolean isIndividual(String uri)
	{
		return isNamedIndividual(uri) || isAnonymousIndividual(uri);
	}

	/**
	 * Checks if a class is contained in the domain of a property
	 * @param cURI: the class URI
	 * @param pURI: the property URI
	 * @return whether cURI is contained in the domain of pURI
	 */
	public boolean isInDomain(String cURI, String pURI)
	{
		//True if there is no range restriction (range is owl:thing)
		return !domain.containsKey(pURI) ||
				//If the range is the class
				domain.get(pURI).equals(cURI) ||
				//Or if the range is a superclass of the class
				getSubclasses(domain.get(pURI), false).contains(cURI);
	}
	
	/**
	 * Checks if a class (or datatype) is contained in the range of a property
	 * @param cURI: the class (or datatype) URI
	 * @param pURI: the property URI
	 * @return whether cURI is contained in the domain of pURI
	 */
	public boolean isInRange(String cURI, String pURI)
	{
		//True if there is no range restriction (range is owl:thing)
		return !range.containsKey(pURI) ||
				//If the range is the class / datatype
				range.get(pURI).equals(cURI) ||
				//Or if the range is a superclass of the class
				(isObjectProperty(pURI) && getSubclasses(range.get(pURI), false).contains(cURI));
	}

	/**
	 * @param prop: the uri of the property to check
	 * @return whether the property is inverseFunctional
	 */
	public boolean isInverseFunctional(String prop)
	{
		return propProps.contains(prop) && propProps.get(prop).contains(PropertyProperty.INVERSE_FUNCTIONAL);
	}

	/**
	 * @param uri: the uri of the Ontology entity
	 * @return whether the entity is a named Individual
	 */
	public boolean isNamedIndividual(String uri)
	{
		return entityType.contains(uri) && entityType.get(uri).contains(EntityType.INDIVIDUAL);
	}

	/**
	 * @param uri: the uri of the Ontology entity
	 * @return whether the entity is an anonymous Individual
	 */
	public boolean isAnonymousIndividual(String uri)
	{
		return entityType.contains(uri) && entityType.get(uri).contains(EntityType.ANON_INDIVIDUAL);
	}

	/**
	 * @param prop: the uri of the property to check
	 * @return whether the property is irreflexive
	 */
	public boolean isIrreflexive(String prop)
	{
		return propProps.contains(prop) && propProps.get(prop).contains(PropertyProperty.IRREFLEXIVE);
	}

	/**
	 * @param uri: the uri of the Ontology entity
	 * @return whether the entity is an Object Property
	 */
	public boolean isObjectProperty(String uri)
	{
		return entityType.contains(uri) && entityType.get(uri).contains(EntityType.OBJECT_PROP);
	}

	/**
	 * @param prop: the uri of the property to check
	 * @return whether the property is reflexive
	 */
	public boolean isReflexive(String prop)
	{
		return propProps.contains(prop) && propProps.get(prop).contains(PropertyProperty.REFLEXIVE);
	}

	/**
	 * @param child: the uri of the child class
	 * @param parent: the uri of the parent class
	 * @return whether the RelationshipMap contains a relationship between child and parent
	 */	
	public boolean isSubclass(String child, String parent)
	{
		return descendantClasses.contains(parent,child);
	}

	/**
	 * @param prop: the uri of the property to check
	 * @return whether the property is symmetric
	 */
	public boolean isSymmetric(String prop)
	{
		return propProps.contains(prop) && propProps.get(prop).contains(PropertyProperty.SYMMETRIC);
	}
	
	/**
	 * @param prop: the uri of the property to check
	 * @return whether the property is symmetric
	 */
	public boolean isTransitive(String prop)
	{
		return propProps.contains(prop) && propProps.get(prop).contains(PropertyProperty.TRANSITIVE);
	}

	/**
	 * @return the number of class relationships in the map
	 */
	public int relationshipCount()
	{
		return ancestorClasses.size();
	}

	/**
	 * Checks whether two individuals share a direct class assignment
	 * @param ind1Id: the first individual to check
	 * @param ind2Id: the second individual to check
	 * @return whether ind1Id and ind2Id have at least one class in common
	 * in their direct class assignments
	 */
	public boolean shareClass(String ind1Id, String ind2Id)
	{
		if(instanceOfMap.get(ind1Id) == null || instanceOfMap.get(ind2Id) == null)
			return false;
		for(String c : instanceOfMap.get(ind1Id))
			if(instanceOfMap.get(ind2Id).contains(c))
				return true;
		return false;
	}

	/**
	 * @return the set of high level classes in an ontology
	 */
	public void setHighLevelClasses(Ontology o)
	{

		//First get the very top classes
		HashSet<String> top = new HashSet<String>();
		Set<String> ancestors = descendantClasses.keySet();
		//Which are classes that have children but not parents
		for(String a : ancestors)
			if(getSuperclasses(a,1,false).size() == 0 && getSubclasses(a,1,false).size() > 0 && o.contains(a))
				top.add(a);
		//Now we go down the ontologies until we reach a significant branching
		while(top.size() < 3)
		{
			HashSet<String> newTop = new HashSet<String>();
			for(String a : top)
				newTop.addAll(getSubclasses(a,1,false));
			top = newTop;
		}
		highLevelClasses.addAll(top);
	}

	/**
	 * @param child: the child class in the relationship
	 * @param parent: the parent class in the relationship
	 * @return whether adding the relationship between child and parent
	 * to the RelationshipMap would violate a disjoint clause
	 */
	public boolean violatesDisjoint(String child, String parent)
	{
		//Get all descendants of the child
		Set<String> descendants = getSubclasses(child,false);
		//Plus the child itself
		descendants.add(child);
		//Then all ancestors of the parent
		Set<String> ancestors = getSuperclasses(parent,false);
		//Plus the parent itself
		ancestors.add(parent);

		//For each descendant
		for(String i : descendants)
			//And each ancestor
			for(String j : ancestors)
				//Check for disjointness
				if(areDisjoint(i,j))
					return true;
		return false;
	}
}