/******************************************************************************
* Abstract Matcher with parallel execution of match and rematch methods.      *
*                                                                             *
* @authors Daniel Faria                                                       *
******************************************************************************/
package liseda.matcha.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.data.Map2Set;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.InstanceMatchingCategory;
import liseda.matcha.settings.Settings;

public abstract class AbstractParallelMatcher extends AbstractAlignmentGenerator implements Extender, Matcher, Rematcher
{

//Attributes

	//The available CPU threads
	protected int threads;
	protected Ontology source;
	protected Ontology target;
	protected EntityType toMatch;
	protected int jobs;
	protected int complete;
	protected boolean showProgress;
	private int progress;

//Constructors
	
	/**
	 * Constructs a new AbstractParallelMatcher
	 */
	public AbstractParallelMatcher()
	{
		threads = Runtime.getRuntime().availableProcessors();
	}

//Public Methods
	
	@Override
	public Alignment extendAlignment(Alignment maps, EntityType e, double thresh)
	{
		showProgress = true;
		source = maps.getSourceOntology();
		target = maps.getTargetOntology();
		toMatch = e;
		Alignment a = new Alignment(source,target);
		if(!checkEntityType(e))
			return a;
		System.out.println("Running " + name  + " in alignment extension mode");
		long time = System.currentTimeMillis()/1000;
		Settings s = Settings.getInstance();
		//Get all unmapped entities of the two ontologies
		Set<String> sources = source.getEntities(e);
		sources.removeAll(maps.getSources());
		Set<String> targets = target.getEntities(e);
		targets.removeAll(maps.getTargets());
		//Setup the matching tasks
		Map2Set<String,String> toMap = new Map2Set<String,String>();
		for(String i : sources)
		{
			if(e.equals(EntityType.INDIVIDUAL) && !s.isToMatch(i))
				continue;
			for(String j : targets)
			{
				if(e.equals(EntityType.INDIVIDUAL) && (!s.isToMatch(j) || (s.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_CLASSES) &&
						!SemanticMap.getInstance().shareClass(i,j))))
					continue;
				toMap.add(i,j);
			}
		}
		a.addAll(mapInParallel(toMap,thresh));
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
		return a;
	}
	
	@Override
	public Alignment match(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		showProgress = true;
		source = o1;
		target = o2;
		toMatch = e;
		Alignment a = new Alignment(o1,o2);
		if(!checkEntityType(e))
			return a;
		System.out.println("Running " + name + " in match mode");
		long time = System.currentTimeMillis()/1000;
		Set<String> sources = o1.getEntities(e);
		Set<String> targets = o2.getEntities(e);
		Settings s = Settings.getInstance();
		Map2Set<String,String> toMap = new Map2Set<String,String>();
		for(String i : sources)
		{
			if(e.equals(EntityType.INDIVIDUAL) && !s.isToMatch(i))
				continue;
			for(String j : targets)
			{
				if(e.equals(EntityType.INDIVIDUAL) && (!s.isToMatch(j) ||
						(s.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_CLASSES) &&
						!SemanticMap.getInstance().shareClass(i,j))))
					continue;
				toMap.add(i,j);
			}
		}
		a.addAll(mapInParallel(toMap,thresh));
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
		return a;
	}
		
	@Override
	public Alignment rematch(Alignment a, EntityType e)
	{
		showProgress = true;
		source = a.getSourceOntology();
		target = a.getTargetOntology();
		Alignment maps = new Alignment(a.getSourceOntology(),a.getTargetOntology());
		if(!checkEntityType(e))
			return maps;
		System.out.println("Running " + name + " in rematch mode");
		long time = System.currentTimeMillis()/1000;
		Map2Set<String,String> toMap = new Map2Set<String,String>();
		for(Mapping m : a)
		{
			String source = m.getEntity1();
			String target = m.getEntity2();
			if(SemanticMap.getInstance().getTypes(source).contains(e) &&
					SemanticMap.getInstance().getTypes(target).contains(e))
				toMap.add(source,target);
		}
		maps.addAll(mapInParallel(toMap,0.0));
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
		return maps;
	}
	
//Protected Methods
	
	/**
	 * Maps a table of entities in parallel, using all available threads
	 * @param toMap: the map of entities to map
	 * @param thresh: the similarity threshold
	 * @return the resulting Alignment
	 */
	protected Alignment mapInParallel(Map2Set<String, String> toMap, double thresh)
	{
		jobs = toMap.size();
		complete = 0;
		progress = 0;
		Alignment maps = new Alignment(source,target);
		ArrayList<MappingTask> tasks = new ArrayList<MappingTask>();
		for(String i : toMap.keySet())
			for(String j : toMap.get(i))
				tasks.add(new MappingTask(i,j));
        List<Future<Mapping>> results;
		ExecutorService exec = Executors.newFixedThreadPool(threads);
		try
		{
			results = exec.invokeAll(tasks);
		}
		catch (InterruptedException e)
		{
			System.out.println();
			e.printStackTrace();
	        results = new ArrayList<Future<Mapping>>();
		}
		exec.shutdown();
		for(Future<Mapping> fm : results)
		{
			try
			{
				Mapping m = fm.get();
				if(m.getSimilarity() >= thresh)
					maps.add(m);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(showProgress)
			System.out.println();
		return maps;
	}

	/**
	 * Maps two entities
	 */
	protected abstract Mapping mapTwoEntities(String sId, String tId);
	
	/**
	 * Prints a progress bar step
	 */
	protected void printProgress()
	{
		String prog = "|";
		int times = 20*complete/jobs;
		if(times > progress)
		{
			progress = times;
			for(int i = 0; i < progress; i++)
				prog += "=";
			prog += ">";
			for(int i = progress+1; i < 20; i++)
				prog += " ";
			System.out.print(prog + "|\r");
		}
	}
	
	/**
	 * Callable class for mapping two entities
	 */
	protected class MappingTask implements Callable<Mapping>
	{
		private String source;
		private String target;
		
		MappingTask(String s, String t)
	    {
			source = s;
	        target = t;
	    }
	        
	    @Override
	    public Mapping call()
	    {
       		return mapTwoEntities(source,target);
        }
	}
}