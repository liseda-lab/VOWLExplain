/******************************************************************************
* An alignment between two Ontologies, represented both as a list of Mappings *
* and as a table of mapped entities.                                          *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.alignment;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import liseda.matcha.data.Map2Map;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Formalism;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.Settings;

public class Alignment implements Collection<Mapping>
{

//Attributes

	//The level of the alignment (0 by default)
	protected int level;
	//The type of the alignment
	protected String type;
	//Links to the Ontologies mapped in this Alignment
	protected Ontology source;
	protected Ontology target;
	//Ontology info
	protected String sourceURI;
	protected String sourceLocation;
	protected Formalism sourceFormalism;
	protected String targetURI;
	protected String targetLocation;
	protected Formalism targetFormalism;
	//Mappings organized in list
	protected Vector<Mapping> maps;
	//Mappings organized by entity1
	protected Map2Map<String,String,Mapping> sourceMaps;
	//Mappings organized by entity2
	protected Map2Map<String,String,Mapping> targetMaps;
	//Whether entities with the same URI can be mapped
	protected boolean mapSameURIs = false;
	
//Constructors

	/**
	 * Creates a new empty Alignment
	 */
	private Alignment()
	{
		maps = new Vector<Mapping>();
		sourceMaps = new Map2Map<String,String,Mapping>();
		targetMaps = new Map2Map<String,String,Mapping>();
		this.level = 0;
	}
	
	/**
	 * Creates a new Alignment that is a copy of the given alignment
	 */
	public Alignment(Alignment a)
	{
		maps = new Vector<Mapping>(a.maps);
		sourceMaps = new Map2Map<String,String,Mapping>(a.sourceMaps);
		targetMaps = new Map2Map<String,String,Mapping>(a.targetMaps);
		this.source = a.source;
		this.target = a.target;
		this.level = a.level;
	}
	
	/**
	 * Creates a new empty Alignment between the source and target ontologies
	 * [Used for creating a new Alignment by matching algorithms] 
	 * @param source: the source ontology
	 * @param target: the target ontology
	 */
	public Alignment(Ontology source, Ontology target)
	{
		this();
		this.source = source;
		this.sourceURI = source.getURI();
		this.sourceLocation = source.getLocation();
		this.sourceFormalism = source.getFormalism();
		this.target = target;
		this.targetURI = target.getURI();
		this.targetLocation = target.getLocation();
		this.targetFormalism = target.getFormalism();
	}

	/**
	 * Creates a new empty Alignment with the specified information about the source and target ontologies
	 * [Used when reading an Alignment from a file without open ontologies]
	 * @param sourceURI: the URI of the source ontology
	 * @param sourceLocation: the location of the source ontology
	 * @param sourceFormalism: the formalism of the source ontology
	 * @param targetURI: the URI of the target ontology 
	 * @param targetLocation: the location of the target ontology
	 * @param targetFormalism: the formalism of the target ontology
	 */
	public Alignment(String sourceURI, String sourceLocation, Formalism sourceFormalism,
			String targetURI, String targetLocation, Formalism targetFormalism)
	{
		this();
		this.sourceURI = sourceURI;
		this.sourceLocation = sourceLocation;
		this.sourceFormalism = sourceFormalism;
		this.targetURI = targetURI;
		this.targetLocation = targetLocation;
		this.targetFormalism = targetFormalism;
	}
	
//Public Methods

	@Override
	public boolean add(Mapping m)
	{
		if(m.getEntity1().equals(m.getEntity2()) && !Settings.getInstance().matchSameURI())
			return false;
		boolean isNew = !this.contains(m);
		if(isNew)
		{
			if(level == 0 && (source.isExpression(m.getEntity1()) || target.isExpression(m.getEntity2()) ||
					m instanceof LinkKeyMapping || m instanceof TransformationMapping))
				level = 2;
				
			maps.add(m);
			sourceMaps.add(m.getEntity1(), m.getEntity2(), m);
			targetMaps.add(m.getEntity2(), m.getEntity1(), m);
		}
		else
		{
			Mapping n = sourceMaps.get(m.getEntity1(),m.getEntity2());
			if(m.getSimilarity() <= n.getSimilarity())
				return false;
			n.setSimilarity(m.getSimilarity());
			isNew = true;
			if(!m.getRelationship().equals(n.getRelationship()))
			{
				m.setRelationship(n.getRelationship());
				isNew = true;
			}
			if(!m.getStatus().equals(n.getStatus()))
			{
				m.setStatus(n.getStatus());
				isNew = true;
			}
		}
		return isNew;
	}

	@Override
	public boolean addAll(Collection<? extends Mapping> a)
	{
		boolean check = false;
		for(Mapping m : a)
			check = add(m) || check;
		return check;
	}
	
	/**
	 * Adds all Mappings in a to this Alignment as long as
	 * they don't conflict with any Mapping already present
	 * in this Alignment. In case of any conflict, the inferior
	 * Mapping is removed.
	 * @param a: the collection of Mappings to add to this Alignment
	 */
	public void addAllImprovements(Collection<? extends Mapping> a)
	{
		Vector<Mapping> improvements = new Vector<Mapping>();
		for(Mapping m : a)
			if(!this.containsConflict(m))
				if(!this.containsBetterMapping(m)) {
					this.removeAll(this.getConflicts(m));
					improvements.add(m);
				}
		addAll(improvements);
	}
	
	/**
	 * Adds all Mappings in a to this Alignment as long as
	 * they don't conflict with any Mapping already present
	 * in this Alignment
	 * @param a: the collection of Mappings to add to this Alignment
	 */
	public void addAllNonConflicting(Collection<? extends Mapping> a)
	{
		Vector<Mapping> nonConflicting = new Vector<Mapping>();
		for(Mapping m : a)
			if(!this.containsConflict(m))
				nonConflicting.add(m);
		addAll(nonConflicting);
	}
	
	/**
	 * Adds all Mappings in a to this Alignment in descending
	 * order of similarity, as long as they don't conflict with
	 * any Mapping already present or previously added to this
	 * Alignment
	 * @param a: the Alignment to add to this Alignment
	 */
	public void addAllOneToOne(Alignment a)
	{
		a.sortDescending();
		for(Mapping m : a.maps)
			if(!this.containsConflict(m))
				add(m);
	}
	
	/**
	 * @return the average cardinality of this Alignment
	 */
	public double cardinality()
	{
		return (this.sourceCount()*0.5+this.targetCount()*0.5)/this.size();
	}
	
	/**
	 * @param uri: the uri of the entity to check in the Alignment
	 * @return the cardinality of the entity in the Alignment
	 */
	public int cardinality(String uri)
	{
		if(sourceMaps.contains(uri))
			return sourceMaps.get(uri).size();
		if(targetMaps.contains(uri))
			return targetMaps.get(uri).size();
		return 0;
	}
	
	@Override
	public void clear()
	{
		maps = new Vector<Mapping>(0,1);
		sourceMaps = new Map2Map<String,String,Mapping>();
		targetMaps = new Map2Map<String,String,Mapping>();		
	}
	
	@Override
	public boolean contains(Object o)
	{
		if(o instanceof Mapping)
		{
			Mapping m = (Mapping)o;
			return sourceMaps.contains(m.getEntity1()) &&
					sourceMaps.get(m.getEntity1()).containsKey(m.getEntity2()) &&
					sourceMaps.get(m.getEntity1(), m.getEntity2()).equals(m);
		}
		return false;
	}
	
	/**
	 * @param src: the source entity
	 * @param tgt: the target entity
	 * @return whether the alignment contains a Mapping between the two entities
	 */
	public boolean contains(String src, String tgt)
	{
		return sourceMaps.contains(src) &&
				sourceMaps.get(src).containsKey(tgt);
	}
	
	/**
	 * @param m: the Mapping to search in the Alignment
	 * @return whether the Alignment contains a mapping equivalent to m but with related relation
	 */
	public boolean containsRelated(Mapping m)
	{
		Mapping n = sourceMaps.get(m.getEntity1(),m.getEntity2());
		return n.equals(m) && n.getRelationship().equals(MappingRelation.RELATED);
	}
	
	@Override
	public boolean containsAll(Collection<?> c)
	{
		for(Object o : c)
			if(!contains(o))
				return false;
		return true;
	}
	
	/**
 	 * @param m: the Mapping to check in the Alignment 
	 * @return whether the Alignment contains a Mapping that conflicts with the given
	 * Mapping and has a higher similarity
	 */
	public boolean containsBetterMapping(Mapping m)
	{
		if(sourceMaps.contains(m.getEntity1()))
		{
			for(String t : sourceMaps.keySet(m.getEntity1()))
				if(sourceMaps.get(m.getEntity1(), t).getSimilarity() > m.getSimilarity())
					return true;
		}
		if(targetMaps.contains(m.getEntity2()))
		{
			for(String s : targetMaps.keySet(m.getEntity2()))
				if(targetMaps.get(m.getEntity2(), s).getSimilarity() > m.getSimilarity())
					return true;
		}
		return false;
	}
	
	/**
 	 * @param m: the Mapping to check in the Alignment 
	 * @return whether the Alignment contains another Mapping involving either entity in m
	 */
	public boolean containsConflict(Mapping m)
	{
		if(sourceMaps.contains(m.getEntity1()))
		{
			for(String t : sourceMaps.keySet(m.getEntity1()))
				if(!sourceMaps.get(m.getEntity1(), t).equals(m))
					return true;
		}
		if(targetMaps.contains(m.getEntity2()))
		{
			for(String s : targetMaps.keySet(m.getEntity2()))
				if(!targetMaps.get(m.getEntity2(), s).equals(m))
					return true;
		}
		return false;
	}
	
	/**
 	 * @param entity: the entity to check in the Alignment 
	 * @return whether the Alignment contains a Mapping with that entity
	 * (either as entity1 or entity2)
	 */
	public boolean containsEntity(String entity)
	{
		return containsSource(entity) || containsTarget(entity);
	}
	
	/**
	 * @param s: the element of the source Ontology to check in the Alignment
 	 * @return whether the Alignment contains a Mapping for s
	 */
	public boolean containsSource(String s)
	{
		return sourceMaps.contains(s);
	}

	/**
	 * @param t: the element of the target Ontology to check in the Alignment
 	 * @return whether the Alignment contains a Mapping for t
	 */
	public boolean containsTarget(String t)
	{
		return targetMaps.contains(t);
	}
	
	/**
 	 * @return the number of conflict mappings in this alignment
	 */
	public int countConflicts() //TODO: Revise this
	{
		int count = 0;
		for(Mapping m : maps)
			if(!m.getRelationship().equals(MappingRelation.RELATED))
				count++;
		return count;
	}
	
	/**
	 * Removes all mappings in the given Alignment from this Alignment
	 * @param a: the Alignment to subtract from this Alignment
	 */
	public boolean difference(Alignment a)
	{
		boolean check = false;
		for(Mapping m : a.maps)
			check = check || this.maps.remove(m);
		return check;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Alignment && containsAll((Alignment)o);
	}

	/**
	 * @param a: the base Alignment to which this Alignment will be compared 
	 * @return the gain (i.e. the fraction of new Mappings) of this Alignment
	 * in comparison with the base Alignment
	 */
	public double gain(Alignment a)
	{
		double gain = 0.0;
		for(Mapping m : maps)
			if(!a.contains(m))
				gain++;
		gain /= a.size();
		return gain;
	}
	
	/**
	 * @param a: the base Alignment to which this Alignment will be compared 
	 * @return the gain (i.e. the fraction of new Mappings) of this Alignment
	 * in comparison with the base Alignment
	 */
	public double gainOneToOne(Alignment a)
	{
		double sourceGain = 0.0;
		for(String i : this.getSources())
			if(!a.containsSource(i))
				sourceGain++;
		sourceGain /= a.sourceCount();
		double targetGain = 0.0;
		for(String i : this.getTargets())
			if(!a.containsTarget(i))
				targetGain++;
		targetGain /= a.targetCount();
		return Math.min(sourceGain, targetGain);
	}
	
	/**
	 * @param index: the index of the Mapping to return in the list of Mappings
 	 * @return the Mapping at the input index (note that the index will change
 	 * during sorting) or null if the uri falls outside the list
	 */
	public Mapping get(int index)
	{
		if(index < 0 || index >= maps.size())
			return null;
		return maps.get(index);
	}
	
	/**
	 * @param entity1: the entity1 to check in the Alignment
	 * @param entity2: the entity2 to check in the Alignment
 	 * @return the Mapping between the entity1 and entity2 classes or null if no
 	 * such Mapping exists
	 */
	public Mapping get(String entity1, String entity2)
	{
		return sourceMaps.get(entity1,entity2);
	}
	
	/**
	 * @param m: the Mapping to check on the Alignment
	 * @return the list of all Mappings that have a cardinality conflict with the given Mapping
	 */
	public Vector<Mapping> getConflicts(Mapping m)
	{
		Vector<Mapping> conflicts = new Vector<Mapping>();
		if(sourceMaps.contains(m.getEntity1()))
		{
			for(String t : sourceMaps.keySet(m.getEntity1()))
				if(!sourceMaps.get(m.getEntity1(), t).equals(m) && !conflicts.contains(m))
					conflicts.add(sourceMaps.get(m.getEntity1(), t));
		}
		if(targetMaps.contains(m.getEntity2()))
		{
			for(String s : targetMaps.keySet(m.getEntity2()))
				if(!targetMaps.get(m.getEntity2(), s).equals(m) && !conflicts.contains(m))
					conflicts.add(targetMaps.get(m.getEntity2(), s));
		}
		return conflicts;
	}
	
	/**
	 * @return the EntityTypes of all entities mapped in this Alignment
	 */
	public Set<EntityType> getEntityTypes()
	{
		HashSet<EntityType> types = new HashSet<EntityType>();
		for(Mapping m : maps)
		{
			types.addAll(source.getTypes(m.getEntity1()));
			types.addAll(target.getTypes(m.getEntity2()));
		}
		return types;
	}
	
	/**
	 * @return the high level Alignment induced from this Alignment
	 * (the similarity between high level classes is given by the
	 * fraction of classes in this Alignment that are their descendents)
	 */
	public Alignment getHighLevelAlignment()
	{
		SemanticMap rels = SemanticMap.getInstance();
		Alignment a = new Alignment();
		int total = maps.size();
		for(Mapping m : maps)
		{
			Set<String> sourceAncestors, targetAncestors;
			if(rels.isClass(m.getEntity1()))
				sourceAncestors = rels.getHighLevelAncestors(m.getEntity1());
			else if(rels.isExpression(m.getEntity1()))
			{
				sourceAncestors = new HashSet<String>();
				for(String s : rels.getExpression(m.getEntity1()).getElements())
					if(rels.isClass(s))
						sourceAncestors.addAll(rels.getHighLevelAncestors(s));
			}
			else
				continue;
			if(rels.isClass(m.getEntity2()))
				targetAncestors = rels.getHighLevelAncestors(m.getEntity2());
			else if(rels.isExpression(m.getEntity2()))
			{
				targetAncestors = new HashSet<String>();
				for(String s : rels.getExpression(m.getEntity2()).getElements())
					if(rels.isClass(s))
						targetAncestors.addAll(rels.getHighLevelAncestors(s));
			}
			else
				continue;
			for(String i : sourceAncestors)
			{
				for(String j : targetAncestors)
				{
					double sim = a.getSimilarity(i, j) + 1.0 / total;
					a.add(new Mapping(i,j,sim,MappingRelation.RELATED));
				}
			}
		}
		Alignment b = new Alignment();
		for(Mapping m : a)
			if(m.getSimilarity() >= 0.01)
				b.add(m);
		return b;
	}
	/**
	 * @return the level of this Alignment (0, 1 or 2)
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * @param uri: the URI of the entity to search in the Alignment
	 * @return all entities mapped to that entity
	 */
	public Set<String> getMappingsBidirectional(String uri)
	{
		HashSet<String> bi = new HashSet<String>();
		if(sourceMaps.contains(uri))
			bi.addAll(sourceMaps.keySet(uri));
		if(targetMaps.contains(uri))
			bi.addAll(targetMaps.keySet(uri));
		return bi;
	}
	
	/**
	 * @param entity1: the entity1 in the Alignment
	 * @param entity2: the entity2 in the Alignment
	 * @return the similarity between entity1 and entity2
	 */
	public double getSimilarity(String entity1, String entity2)
	{
		if(sourceMaps.contains(entity1,entity2))
			return get(entity1,entity2).getSimilarity();
		return 0.0;
	}
	
	/**
	 * @param entity1: the entity1 in the Alignment
	 * @param entity2: the entity2 in the Alignment
	 * @return the similarity between entity1 and entity2 in percentage
	 */
	public String getSimilarityPercent(String entity1, String entity2)
	{
		if(sourceMaps.contains(entity1,entity2))
			return get(entity1,entity2).getSimilarityPercent();
		return "0.0%";
	}
	
	/**
	 * @param m: the Mapping to check on the Alignment
	 * @return the list of all Mappings that have a cardinality conflict with the given Mapping's source expression
	 */
	public Vector<Mapping> getSourceConflicts(Mapping m)
	{
		Vector<Mapping> conflicts = new Vector<Mapping>();
		if(sourceMaps.contains(m.getEntity1()))
		{
			for(String t : sourceMaps.keySet(m.getEntity1()))
				if(!sourceMaps.get(m.getEntity1(), t).equals(m) && !conflicts.contains(m))
					conflicts.add(sourceMaps.get(m.getEntity1(), t));
		}
		return conflicts;
	}
	
	/**
	 * @return the formalism of the source ontology
	 */
	public Formalism getSourceFormalism()
	{
		return sourceFormalism;
	}

	/**
	 * @return the location of the source ontology
	 */
	public String getSourceLocation()
	{
		return sourceLocation;
	}
	
	/**
	 * @return the mappings in this alignment pertaining entity1 
	 */
	public Vector<Mapping> getSourceMappings(String entity1)
	{
		Vector<Mapping> v = new Vector<Mapping>();
		for(String t : sourceMaps.keySet(entity1))
			v.add(sourceMaps.get(entity1,t));
		return v;
	}
	
	/**
	 * @return the source ontology of this alignment
	 */
	public Ontology getSourceOntology()
	{
		return source;
	}
	
	/**
	 * @return the source entities mapped in this alignment
	 */
	public Set<String> getSources()
	{
		return sourceMaps.keySet();
	}
	
	/**
	 * @return the URI of the source ontology
	 */
	public String getSourceURI()
	{
		return sourceURI;
	}
	
	/**
	 * @param m: the Mapping to check on the Alignment
	 * @return the list of all Mappings that have a cardinality conflict with the given Mapping's target expression
	 */
	public Vector<Mapping> getTargetConflicts(Mapping m)
	{
		Vector<Mapping> conflicts = new Vector<Mapping>();
		if(targetMaps.contains(m.getEntity2()))
		{
			for(String s : targetMaps.keySet(m.getEntity2()))
				if(!targetMaps.get(m.getEntity2(), s).equals(m) && !conflicts.contains(m))
					conflicts.add(targetMaps.get(m.getEntity2(), s));
		}
		return conflicts;
	}
	
	/**
	 * @return the formalism of the target ontology
	 */
	public Formalism getTargetFormalism()
	{
		return targetFormalism;
	}

	/**
	 * @return the location of the target ontology
	 */
	public String getTargetLocation()
	{
		return targetLocation;
	}
	
	/**
	 * @return the mappings in this alignment pertaining entity2
	 */
	public Vector<Mapping> getTargetMappings(String entity2)
	{
		Vector<Mapping> v = new Vector<Mapping>();
		for(String s : targetMaps.keySet(entity2))
			v.add(targetMaps.get(entity2,s));
		return v;

	}
	
	/**
	 * @return the target ontology of this alignment
	 */
	public Ontology getTargetOntology()
	{
		return target;
	}
	
	/**
	 * @return the target entities mapped in this alignment
	 */
	public Set<String> getTargets()
	{
		return targetMaps.keySet();
	}
	
	/**
	 * @return the URI of the target ontology
	 */
	public String getTargetURI()
	{
		return targetURI;
	}
	
	/**
	 * @return the type of this Alignment, which is a two-character string,
	 * either provided or computed automatically with the following notation:
	 * "1" for injective and total
	 * "?" for injective
	 * "+" for total
	 * "*" for neither injective nor total
	 */
	public String getType()
	{
		if(type == null)
		{
			type = "";
			double sourceCard = maps.size() * 1.0 / sourceCount();
			double sourceCov = sourceCoverage();
			if(sourceCard <= 1.1)
			{
				if(sourceCov >= 0.9)
					type += "1";
				else
					type += "?";
			}
			else if(sourceCov >= 0.9)
				type += "+";
			else
				type += "*";
			double targetCard = maps.size() * 1.0 / targetCount();
			double targetCov = targetCoverage();
			if(targetCard <= 1.1)
			{
				if(targetCov >= 0.9)
					type += "1";
				else
					type += "?";
			}
			else if(targetCov >= 0.9)
				type += "+";
			else
				type += "*";

		}
		return type;
		
	}

	@Override
	public int hashCode()
	{
		return maps.hashCode();
	}
	
	/**
	 * @param m: the Mapping to search in the Alignment
	 * @return the index of the Mapping
	 */
	public int indexOf(Mapping m)
	{
		return maps.indexOf(m);
	}
	
	/**
	 * @param uri1: the URI of the first entity to search in the Alignment
	 * @param uri2: the URI of the second entity to search in the Alignment
	 * @return the index of any Mapping involving the two entities, in either direction,
	 * or -1 if no such Mapping exists in the Alignment
	 */
	public int indexOfBidirectional(String uri1, String uri2)
	{
		if(sourceMaps.contains(uri1, uri2))
			return indexOf(sourceMaps.get(uri1, uri2));
		if(targetMaps.contains(uri1, uri2))
			return indexOf(targetMaps.get(uri1, uri2));
		return -1;
	}
	
	/**
	 * Intersects this Alignment with a given Aligmment, retaining only
	 * the common mappings
	 * @param a: the Alignment to intersect with this Alignment 
	 */
	public boolean intersection(Alignment a)
	{
		HashSet<Mapping> toRemove = new HashSet<Mapping>();
		for(Mapping m : a)
			if(!this.contains(m))
				toRemove.add(m);
		for(Mapping m : this)
			if(!a.contains(m))
				toRemove.add(m);
		return this.removeAll(toRemove);
	}
	
	@Override
	public boolean isEmpty()
	{
		return maps.isEmpty();
	}
	
	@Override
	public Iterator<Mapping> iterator()
	{
		return maps.iterator();
	}
	
	/**
	 * @return the maximum cardinality of this Alignment
	 */
	public double maxCardinality()
	{
		double cardinality;
		double max = 0.0;
		Set<String> sources = sourceMaps.keySet();
		for(String i : sources)
		{
			cardinality = sourceMaps.get(i).size();
			if(cardinality > max)
				max = cardinality;
		}
		Set<String> targets = targetMaps.keySet();
		for(String i : targets)
		{
			cardinality = targetMaps.get(i).size();
			if(cardinality > max)
				max = cardinality;
		}
		return max;
	}
	
	@Override
	public boolean remove(Object o)
	{
		if(o instanceof Mapping)
		{			
			Mapping m = (Mapping)o;
			sourceMaps.remove(m.getEntity1(), m.getEntity2());
			targetMaps.remove(m.getEntity2(), m.getEntity2());
			return maps.remove(o);
		}
		return false;
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean check = false;
		for(Object o : c)
			check = remove(o) || check;
		return check;
	}
	
	/**
     * Removes all incorrect mappings from this Alignment
	 * @return true if any mapping was removed
	 */
	public boolean removeAllIncorrect()
	{
		HashSet<Mapping> inc = new HashSet<Mapping>();
		for(Mapping m : maps)
			if(m.getStatus().equals(MappingStatus.INCORRECT))
				inc.add(m);
		return removeAll(inc);
	}
	
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		boolean check = false;
		for(Mapping m : this)
			if(!c.contains(m))
				check = remove(m) || check;
		return check;
	}

	/**
	 * Sets the option of mapping entities with the same URIs
	 * @param map: whether the option is on or off
	 */
	public void setMapSameURIs(boolean map)
	{
		mapSameURIs = map;
	}
	
	/**
	 * Sets the type of the alignment
	 * @param type: the alignment type, a two-character string
	 * recommended to use the following notation:
	 * "1" for injective and total
	 * "?" for injective
	 * "+" for total
	 * "*" for neither injective nor total
	 * Alternatively "1m", "n1", and "nm" may also be used 
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	@Override
	public int size()
	{
		return maps.size();
	}

	/**
	 * Sorts the Alignment ascendingly
	 */
	public void sortAscending()
	{
		Collections.sort(maps);
	}
	
	/**
	 * Sorts the Alignment descendingly
	 */
	public void sortDescending()
	{
		Collections.sort(maps,new Comparator<Mapping>()
        {
			//Sorting in descending order can be done simply by
			//reversing the order of the elements in the comparison
            public int compare(Mapping m1, Mapping m2)
            {
        		return m2.compareTo(m1);
            }
        } );
	}
	
	/**
	 * @return the number of entity1 mapped in this Alignment
	 */
	public int sourceCount()
	{
		return getSources().size();
	}
	
	/**
	 * @return the fraction of entities from the source ontology
	 * mapped in this Alignment (counting only entity types that
	 * are mapped)
	 */
	public double sourceCoverage()
	{
		if(source == null)
			return 0;
		int count = 0;
		for(EntityType e : this.getEntityTypes())
			count += source.count(e);
		return sourceCount()*1.0/count;
	}
	
	/**
	 * @return the number of entity2 mapped in this Alignment
	 */
	public int targetCount()
	{
		return getTargets().size();
	}
	
	/**
	 * @return the fraction of entities from the target ontology
	 * mapped in this Alignment (counting only entity types that
	 * are mapped)
	 */
	public double targetCoverage()
	{
		if(target == null)
			return 0;
		int count = 0;
		for(EntityType e : this.getEntityTypes())
			count += target.count(e);
		return targetCount()*1.0/count;
	}
	
	@Override
	public Object[] toArray()
	{
		return maps.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
		return maps.toArray(a);
	}
}