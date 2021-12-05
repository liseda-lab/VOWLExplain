/******************************************************************************
* Map of extended relationships of classes involved in disjoint clauses with  *
* mappings from a given Alignment, which supports repair of that Alignment.   *
*                                                                             *
* @authors Daniel Faria & Emanuel Santos                                      *
******************************************************************************/
package liseda.matcha.logic;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.data.Map2Map2List;
import liseda.matcha.data.Map2Map2Set;
import liseda.matcha.data.Map2Set;
import liseda.matcha.semantics.Reasoner;
import liseda.matcha.semantics.SemanticMap;

public class RepairMap implements Iterable<Integer>
{
	
//Attributes
	
	private SemanticMap rels;
	private Alignment a;
	//The list of classes that are relevant for coherence checking
	private HashSet<String> classList;
	//The list of classes that must be checked for coherence
	private HashSet<String> checkList;
	//The minimal map of ancestor relations of checkList classes
	//(checkList class Id, classList class Id, Path)
	private Map2Map2List<String,String,Path<Integer>> ancestorMap;
	//The length of ancestral paths to facilitate transitive closure
	//(checklist class Id, Path length, classList class Id)
	private Map2Map2Set<String,Integer,String> pathLengths;
	//The number of paths to disjoint classes
	private int pathCount;
	//The list of conflict sets
	private Vector<Path<Integer>> conflictSets;
	//The table of conflicts per mapping
	private Map2Set<Integer,Integer> conflictMappings;
	private Map2Set<Integer,Integer> mappingConflicts;
	//The available CPU threads
	private int threads;
	
//Constructors
	
	/**
	 * Constructs a new RepairMap
	 */
	public RepairMap(Alignment a)
	{
		threads = Runtime.getRuntime().availableProcessors();
		System.out.println("Building Repair Map");
		//Start with processing the semantic disjoints
		Reasoner.processSemanticDisjoints();
		rels = SemanticMap.getInstance();
		this.a = a;
		//Remove the FLAGGED status from all mappings that have it
		for(Mapping m : this.a)
			if(m.getStatus().equals(MappingStatus.FLAGGED))
				m.setStatus(MappingStatus.UNREVISED);
		long globalTime = System.currentTimeMillis()/1000;
		//Initialize the data structures
		classList = new HashSet<String>();
		checkList = new HashSet<String>();
		ancestorMap = new Map2Map2List<String,String,Path<Integer>>();
		pathLengths = new Map2Map2Set<String,Integer,String>();
		conflictSets = new Vector<Path<Integer>>();
		
		//Build the classList, starting with the classes
		//involved in disjoint clauses
		classList.addAll(rels.getDisjoint());
		//If there aren't any, there is nothing else to do
		if(classList.size() == 0)
		{
			System.out.println("Nothing to repair!");
			return;
		}
		//Otherwise, add all classes involved in mappings
		for(String i : a.getSources())
			if(rels.isClass(i))
				classList.add(i);
		for(String i : a.getTargets())
			if(rels.isClass(i))
				classList.add(i);
		
		//Then build the checkList
		long localTime = System.currentTimeMillis()/1000;
		buildCheckList();
		System.out.println("Computed check list in " + 
				(System.currentTimeMillis()/1000-localTime) + " seconds");
		HashSet<String> t = new HashSet<String>(classList);
		t.addAll(checkList);
		System.out.println("Core fragments: " + t.size() + " classes");
		t.clear();
		System.out.println("Check list: " + checkList.size() + " classes to check");
		
		//Build the ancestorMap with transitive closure
		localTime = System.currentTimeMillis()/1000;
		buildAncestorMap();
		System.out.println("Computed ancestral paths in " + 
				(System.currentTimeMillis()/1000-localTime) + " seconds");
		System.out.println("Paths to process: " + pathCount);
		
		//And finally, get the list of conflict sets
		localTime = System.currentTimeMillis()/1000;
		buildConflictSets();
		System.out.println("Computed minimal conflict sets in " + 
				(System.currentTimeMillis()/1000-localTime) + " seconds");
		System.out.println("Sets of conflicting mappings: " + conflictSets.size());
		System.out.println("Repair Map finished in " +
				(System.currentTimeMillis()/1000-globalTime) + " seconds");
	}
	
//Public Methods
	
	/**
	 * @param index: the index of the Mapping to get
	 * @return the conflict sets that contain the given Mapping index
	 */
	public Set<Integer> getConflicts(int index)
	{
		return mappingConflicts.get(index);
	}
	
	/**
	 * @param m: the Mapping to get
	 * @return the list of Mappings in conflict with this Mapping
	 */
	public Set<Mapping> getConflictMappings(Mapping m)
	{
		int index = a.indexOf(m);
		HashSet<Mapping> confs = new HashSet<Mapping>();
		if(!mappingConflicts.contains(index))
			return confs;
		for(Integer i : mappingConflicts.get(index))
		{
			for(Integer j : conflictMappings.get(i))
			{
				if(j == index)
					continue;
				confs.add(a.get(j));
			}
		}
		return confs;
	}
	
	/**
	 * @return the list of conflict sets of mappings
	 * in the form of indexes (as per the alignment
	 * to repair)
	 */
	public Vector<Path<Integer>> getConflictSets()
	{
		return conflictSets;
	}
	
	/**
	 * @param m: the Mapping to search in the RepairMap
	 * @return the index of the Mapping in the RepairMap
	 */
	public int getIndex(Mapping m)
	{
		return a.indexOf(m);
	}
	
	/**
	 * @param index: the index of the Mapping to get
	 * @return the Mapping at the given index
	 */
	public Mapping getMapping(int index)
	{
		return new Mapping(a.get(index));
	}
	
	/**
	 * @return the index of the worst Mapping in the Alignment
	 */
	public int getWorstMapping()
	{
		int worstMapping = -1;
		int maxCard = 0;
		
		for(Integer i : this)
		{
			int card = getConflicts(i).size();
			Mapping m = getMapping(i);
			if((card > maxCard || (card == maxCard &&
				m.getSimilarity() < getMapping(worstMapping).getSimilarity())) &&
				!m.getStatus().equals(MappingStatus.CORRECT))
			{
				maxCard = card;
				worstMapping = i;
			}
		}
		return worstMapping;
	}
	
	/**
	 * @return whether the alignment is coherent
	 */
	public boolean isCoherent()
	{
		return conflictSets == null || conflictSets.size() == 0;
	}
	
	@Override
	public Iterator<Integer> iterator()
	{
		return mappingConflicts.keySet().iterator();
	}
	
	/**
	 * Sets a Mapping as INCORRECT and removes its conflicts from the RepairMap
	 * (but does not actually remove the Mapping from the Alignment)
	 * @param index: the index of the Mapping to remove
	 */
	public void remove(int index)
	{
		HashSet<Integer> conflicts = new HashSet<Integer>(mappingConflicts.get(index));
		for(Integer i : conflicts)
		{
			for(Integer j : conflictMappings.get(i))
				mappingConflicts.remove(j,i);
			conflictMappings.remove(i);
		}
		mappingConflicts.remove(index);
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.INCORRECT);
	}
	
	/**
	 * "Removes" the worst Mapping from the Alignment by setting
	 * its status to INCORRECT
	 * @return whether a Mapping was removed
	 */
	public boolean removeWorstMapping()
	{
		int worstMapping = getWorstMapping();
		if(worstMapping != -1)
		{
			remove(worstMapping);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Saves the list of minimal conflict sets to a text file
	 * @param file: the path to the file where to save
	 * @throws FileNotFoundException if unable to create/open file
	 */
	public void saveConflictSets(String file) throws FileNotFoundException
	{
		PrintWriter outStream = new PrintWriter(new FileOutputStream(file));
		int id = 1;
		for(Path<Integer> p : conflictSets)
		{
			outStream.println("Conflict Set " + id++ + ":");
			for(Integer i : p)
				outStream.println(a.get(i).toString());
		}
		outStream.close();
	}
	
//Private Methods
	
	//Computes the list of classes that must be checked for coherence
	private void buildCheckList()
	{
		//Start with the descendants of classList classes that have
		//either 2+ parents with a classList class in their ancestral
		//line or are involved in a disjoint class and have 1+ parents
		HashSet<String> descList = new HashSet<String>();
		for(String i: classList)
		{
			//Get the subclasses of classList classes
			for(String j : rels.getSubclasses(i,false))
			{
				//Count their parents
				Set<String> pars = rels.getSuperclasses(j, 1, true);
				//Check if they have a disjoint clause
				int hasDisjoint = 0;
				if(rels.hasDisjoint(j))
					hasDisjoint = 1;
				//Exclude those that don't have at least two parents
				//or a parent and a disjoint clause
				if(pars.size() + hasDisjoint < 2)
					continue;
				//Count the classList classes in the ancestral
				//line of each parent (or until two parents with
				//classList ancestors are found)
				int count = hasDisjoint;
				for(String k : pars)
				{
					if(classList.contains(k))
						count++;
					else
					{
						for(String l : rels.getSuperclasses(k, true))
						{
							if(classList.contains(l))
							{
								count++;
								break;
							}
						}
					}
					if(count > 1)
						break;
				}
				//Add those that have at least 2 classList
				//classes in their ancestral line
				if(count > 1)
					descList.add(j);
			}
		}
		//Filter out classes that have a descendant in the descList
		//or a mapped descendant
		HashSet<String> toRemove = new HashSet<String>();
		for(String i : descList)
		{
			for(String j : rels.getSubclasses(i, false))
			{
				if(descList.contains(j) || a.containsEntity(j))
				{
					toRemove.add(i);
					break;
				}
			}
		}
		descList.removeAll(toRemove);
		//And those that have the same set or a subset of
		//classList classes in their ancestor line
		toRemove = new HashSet<String>();
		Vector<String> desc = new Vector<String>();
		Vector<Path<String>> paths = new Vector<Path<String>>();
		for(String i : descList)
		{
			//Put the classList ancestors in a path
			Path<String> p = new Path<String>();
			for(String j : rels.getSuperclasses(i, true))
				if(classList.contains(j))
					p.add(j);
			//Put the class itself in the path if it
			//is also in classList
			if(classList.contains(i))
				p.add(i);			
			
			boolean add = true;
			//Check if any of the selected classes
			for(int j = 0; j < desc.size() && add; j++)
			{
				//subsumes this class (if so, skip it)
				if(paths.get(j).contains(p))
					add = false;
				//is subsumed by this class (if so,
				//remove the latter and proceed)
				else if(p.contains(paths.get(j)))
				{
					desc.remove(j);
					paths.remove(j);
					j--;
				}
			}
			//If no redundancy was found, add the class
			//to the list of selected classes
			if(add)
			{
				desc.add(i);
				paths.add(p);
			}
		}
		//Add all selected classes to the checkList
		checkList.addAll(desc);
		//Now get the list of all mapped classes that are
		//involved in two mappings or have an ancestral
		//path to a mapped class, from only one side
		HashSet<String> mapList = new HashSet<String>();
		for(Mapping m : a)
		{
			String source = m.getEntity1();
			String target = m.getEntity2();
			if(!rels.isClass(source) || !rels.isClass(target))
				continue;
			//Check if there is no descendant in the checkList
			boolean isRedundant = false;
			HashSet<String> descendants = new HashSet<String>(rels.getSubclasses(source, false));
			descendants.addAll(rels.getSubclasses(target, false));
			for(String i : descendants)
			{
				if(checkList.contains(i))
				{
					isRedundant = true;
					break;
				}
			}
			if(isRedundant)
				continue;
			//Count the mappings of both source and target classes
			int sourceCount = a.getSourceMappings(source).size();
			int targetCount = a.getTargetMappings(target).size();
			//If the target class has more mappings than the source
			//class (which implies it has at least 2 mappings) add it
			if(targetCount > sourceCount)
				mapList.add(target);
			//If the opposite is true, add the target
			else if(sourceCount > targetCount || sourceCount > 1)
				mapList.add(source);
			//Otherwise, check for mapped ancestors on both sides
			else
			{
				for(String j : rels.getSuperclasses(source, true))
					if(a.containsSource(j))
						sourceCount++;
				for(String j : rels.getSuperclasses(target, true))
					if(a.containsTarget(j))
						targetCount++;
				if(sourceCount > 1 && targetCount < sourceCount)
					mapList.add(source);
				else if(targetCount > 1)
					mapList.add(target);
			}
		}
		toRemove = new HashSet<String>();
		for(String i : mapList)
		{
			for(String j : rels.getSubclasses(i, false))
			{
				if(mapList.contains(j))
				{
					toRemove.add(i);
					break;
				}
			}
		}
		mapList.removeAll(toRemove);
		//Finally, add the mapList to the checkList
		checkList.addAll(mapList);
	}

	//Builds the map of ancestral relations between all classes
	//in the checkList and all classes in the classList, with
	//(breadth first) transitive closure
	private void buildAncestorMap()
	{
		//First get the "direct" relations between checkList
		//and classList classes, which are present in the
		//RelationshipMap, plus the relations through direct
		//mappings of checkList classes
		for(String i : checkList)
		{
			//Direct relations
			Set<String> ancs = rels.getSuperclasses(i, true);
			for(String j : ancs)
				if(classList.contains(j))
					addRelation(i, j, new Path<Integer>());
			//Mappings
			Set<String> maps = a.getMappingsBidirectional(i);
			for(String j : maps)
			{
				//Get both the mapping and its ancestors
				int index = a.indexOfBidirectional(i, j);
				HashSet<String> newAncestors = new HashSet<String>(rels.getSuperclasses(j, true));
				newAncestors.add(j);
				//And add them
				for(String m : newAncestors)
					if(classList.contains(m))
						addRelation(i,m,new Path<Integer>(index));
			}
		}
		//Then add paths iteratively by extending paths with new
		//mappings, stopping when the ancestorMap stops growing
		int size = 0;
		for(int i = 0; size < ancestorMap.size(); i++)
		{
			size = ancestorMap.size();
			//For each class in the checkList
			for(String j : checkList)
			{
				//If it has ancestors through paths with i mappings
				if(!pathLengths.contains(j, i))
					continue;
				//We get those ancestors
				HashSet<String> ancestors = new HashSet<String>(pathLengths.get(j,i));
				//For each such ancestor
				for(String k : ancestors)
				{
					//Cycle check 1 (make sure ancestor != self)
					if(k.equals(j))
						continue;
					//Get the paths between the class and its ancestor
					HashSet<Path<Integer>> paths = new HashSet<Path<Integer>>();
					for(Path<Integer> p : ancestorMap.get(j, k))
						if(p.size() == i)
							paths.add(p);
					//Get the ancestor's mappings
					Set<String> maps = a.getMappingsBidirectional(k);
					//And for each mapping
					for(String l : maps)
					{
						//Cycle check 2 (make sure mapping != self)
						if(l.equals(j))
							continue;
						//We get its ancestors
						int index = a.indexOfBidirectional(k, l);
						HashSet<String> newAncestors = new HashSet<String>(rels.getSuperclasses(l, true));
						//Plus the mapping itself
						newAncestors.add(l);
						//Now we must increment all paths between j and k
						for(Path<Integer> p : paths)
						{
							//Cycle check 3 (make sure we don't go through the
							//same mapping twice)
							if(p.contains(index))
								continue;
							//We increment the path by adding the new mapping
							Path<Integer> q = new Path<Integer>(p);
							q.add(index);
							//And add a relationship between j and each descendant of
							//the new mapping (including the mapping itself) that is
							//on the checkList
							for(String m : newAncestors)
								//Cycle check 4 (make sure mapping descendant != self)
								if(classList.contains(m) && !m.equals(j))
									addRelation(j,m,q);
						}
					}
				}
			}
		}
		//Finally add relations between checkList classes and
		//themselves when they are involved in disjoint clauses
		//(to support the buildClassConflicts method)
		for(String i : checkList)
			if(rels.hasDisjoint(i))
				ancestorMap.add(i, i, new Path<Integer>());
	}
	
	//Adds a relation to the ancestorMap (and pathLengths)
	private void addRelation(String child, String parent, Path<Integer> p)
	{
		if(ancestorMap.contains(child,parent))
		{
			Vector<Path<Integer>> paths = ancestorMap.get(child,parent);
			for(Path<Integer> q : paths)
				if(p.contains(q))
					return;
		}
		ancestorMap.add(child,parent,p);
		pathLengths.add(child, p.size(), parent);
		if(rels.hasDisjoint(parent))
			pathCount++;
	}
	
	//Builds the global minimal conflict sets for all checkList classes
	private void buildConflictSets()
	{
		//If there is only one CPU thread available, then process in series
		if(threads == 1)
		{
			//For each checkList class
			for(String i : checkList)
			{
				//Get its minimized conflicts
				Vector<Path<Integer>> classConflicts = buildClassConflicts(i);
				//And add them to the conflictSets, minimizing upon addition
				for(Path<Integer> p : classConflicts)
					addConflict(p,conflictSets);
			}
		}
		//Otherwise process in parallel
		else
		{
			//Create a task for each checkList class
			ArrayList<ClassConflicts> tasks = new ArrayList<ClassConflicts>();
			for(String i : checkList)
				tasks.add(new ClassConflicts(i));
			//Then execute all tasks using the available threads
	        List<Future<Vector<Path<Integer>>>> results;
			ExecutorService exec = Executors.newFixedThreadPool(threads);
			try
			{
				results = exec.invokeAll(tasks);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
		        results = new ArrayList<Future<Vector<Path<Integer>>>>();
			}
			exec.shutdown();
			//Finally, combine all minimal class conflict sets
			Vector<Path<Integer>> allConflicts = new Vector<Path<Integer>>();
			for(Future<Vector<Path<Integer>>> conf : results)
			{
				try
				{
					for(Path<Integer> p : conf.get())
						allConflicts.add(p);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			//Sort them
			Collections.sort(allConflicts);
			//And turn them into the final minimal list of conflict sets
			for(Path<Integer> p : allConflicts)
				addConflict(p,conflictSets);
		}
		//Now go through the conflict sets and link them to the mappings
		conflictMappings = new Map2Set<Integer,Integer>();
		mappingConflicts = new Map2Set<Integer,Integer>();
		for(int i = 0; i < conflictSets.size(); i++)
		{
			for(Integer j : conflictSets.get(i))
			{
				conflictMappings.add(i,j);
				mappingConflicts.add(j,i);
			}
		}
	}
	
	//Builds the minimal conflict sets for a given checkList class
	private Vector<Path<Integer>> buildClassConflicts(String classId)
	{
		//First get all ancestors involved in disjoint clauses
		HashMap<String,Integer> disj = new HashMap<String,Integer>();
		int index = 0;
		for(String i : ancestorMap.keySet(classId))
			if(rels.hasDisjoint(i))
				disj.put(i,index++);
		
		//Plus the class itself, if it has a disjoint clause
		if(rels.hasDisjoint(classId))
			disj.put(classId,index++);	
		
		//Then test each pair of ancestors for disjointness
		Vector<Path<Integer>> classConflicts = new Vector<Path<Integer>>();
		for(String i : disj.keySet())
		{
			for(String j : rels.getDisjoint(i))
			{
				if(!disj.containsKey(j) || disj.get(i) > disj.get(j))
					continue;
				for(Path<Integer> p : ancestorMap.get(classId, i))
				{
					for(Path<Integer> q : ancestorMap.get(classId, j))
					{
						Path<Integer> merged = new Path<Integer>(p);
						merged.merge(q);
						//Adding the merged path to the list of classConflicts
						classConflicts.add(merged);
					}
				}
			}
		}
		//Then sort that list
		Collections.sort(classConflicts);
		//And turn it into a minimal list
		Vector<Path<Integer>> minimalConflicts = new Vector<Path<Integer>>();
		for(Path<Integer> p : classConflicts)
			addConflict(p, minimalConflicts);
		return minimalConflicts;
	}
	
	//Adds a path to a list of conflict sets if it is a minimal path
	//(this only results in a minimal list of paths if paths are added
	//in order, after sorting)
	private void addConflict(Path<Integer> p, Vector<Path<Integer>> paths)
	{
		for(Path<Integer> q : paths)
			if(p.contains(q))
				return;
		paths.add(p);
	}
	
	//Callable class for computing minimal conflict sets
	private class ClassConflicts implements Callable<Vector<Path<Integer>>>
	{
		private String term;
		
		ClassConflicts(String t)
	    {
	        term = t;
	    }
	        
	    @Override
	    public Vector<Path<Integer>> call()
	    {
       		return buildClassConflicts(term);
        }
	}
}