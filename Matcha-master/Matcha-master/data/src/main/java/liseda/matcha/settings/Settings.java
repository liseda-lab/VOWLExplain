/******************************************************************************
* Stores and enables access to the matching settings.                         *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.settings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;

public class Settings
{

//Attributes
	
	//Singleton pattern: unique instance
	private static Settings settings = new Settings();
	//Matching modes and general settings
	private boolean complexMatch;
	private boolean matchClasses;
	private boolean matchIndividuals;
	private boolean matchProperties;
	private Set<String> individualsToMatch; //The set of individuals to be matched
	private double threshold = 0.6;
	private boolean matchSameURI = false;
	//Matching profiling
	private SizeCategory size;
	private InstanceMatchingCategory inst;
	private LanguageSetting lang;
    private SelectionType sType;
	private boolean hierarchic = true;
	//Matching detailed configuration
    private boolean primaryStringMatcher; //Whether to use the String Matcher globally (TRUE) or locally (FALSE)
    private StringSimMeasure ssm;
	private WordMatchStrategy wms;
	private NeighborSimilarityStrategy nss;
    private boolean directNeighbors = false;
    private boolean structuralSelection = false;
    private boolean repair = true;
    private boolean matchAuto = true;
    private List<MatchStep> steps;
    
//Constructors
	
	private Settings(){}

//Public Methods

	public boolean complexMatch()
	{
		return complexMatch;
	}
	
	public void defaultConfig(Ontology source, Ontology target)
	{
		matchClasses = source.count(EntityType.CLASS) > 1 && target.count(EntityType.CLASS) > 1;
		double sourceRatio = (source.count(EntityType.DATA_PROP) + source.count(EntityType.OBJECT_PROP)) * 1.0 / source.count(EntityType.CLASS);
		double targetRatio = (target.count(EntityType.DATA_PROP) + target.count(EntityType.OBJECT_PROP)) * 1.0 / target.count(EntityType.CLASS);
		matchProperties = sourceRatio >= 0.05 && targetRatio >= 0.05;
		sourceRatio = source.count(EntityType.INDIVIDUAL) * 1.0 / source.count(EntityType.CLASS);
		targetRatio = target.count(EntityType.INDIVIDUAL) * 1.0 / target.count(EntityType.CLASS);
		matchIndividuals = sourceRatio >= 0.25 && targetRatio >= 0.25;
		if(matchIndividuals)
		{
			inst = InstanceMatchingCategory.DIFFERENT_ONTOLOGIES;
			Set<String> sc = new HashSet<String>(source.getEntities(EntityType.CLASS));
			Set<String> tc = new HashSet<String>(target.getEntities(EntityType.CLASS));
			double share = sc.size() + tc.size();
			sc.retainAll(target.getEntities(EntityType.CLASS));
			share = sc.size() / (share - sc.size());
			if(share >= 0.5)
			{
				inst = InstanceMatchingCategory.SAME_ONTOLOGY;
				matchClasses = false;
				matchProperties = false;
			}
			else if(sourceRatio > 1 && targetRatio > 1)
			{
				matchClasses = false;
				matchProperties = false;
			}
			individualsToMatch = new HashSet<String>(source.getEntities(EntityType.INDIVIDUAL));
			individualsToMatch.addAll(target.getEntities(EntityType.INDIVIDUAL));
		}
		if(size == null)
			size = SizeCategory.getSizeCategory(source,target);
    	if(size.equals(SizeCategory.HUGE))
    	{
    		threshold = 0.7;
    		structuralSelection = true;
    	}
    	if(lang == null)
    	{
    		if(matchClasses)
    			lang = LanguageSetting.getLanguageSetting(source,target,EntityType.CLASS);
    		else if(matchIndividuals)
    			lang = LanguageSetting.getLanguageSetting(source,target,EntityType.INDIVIDUAL);
    		else
    			lang = LanguageSetting.SINGLE;
    	}
    	if(wms == null)
    		wms = WordMatchStrategy.AVERAGE;
		if(ssm == null)
			ssm = StringSimMeasure.ISUB;
		if(nss == null)
			nss = NeighborSimilarityStrategy.DESCENDANTS;
		sType = SelectionType.getSelectionType(size);
	}
	
    /**
     * @return whether to use direct neighbors only in the NeighborSimilarityMatcher
     */
	public boolean directNeighbors()
	{
		return directNeighbors;
	}
	
	/**
	 * @return the set of individuals to match
	 */
	public Set<String> getIndividualsToMatch()
	{
		return individualsToMatch;
	}
	
    /**
     * @return the instance of his class
     */
	public static Settings getInstance()
	{
		return settings;
	}
	
	/**
     * @return the active LanguageSetting
     */
	public LanguageSetting getLanguageSetting()
	{
		return lang;
	}
	
	/**
	 * @return the list of MatchSteps
	 */
	public List<MatchStep> getMatchSteps()
	{
		return steps;
	}
	
	/**
     * @return the active NeighborSimilarityStrategy 
     */
    public NeighborSimilarityStrategy getNeighborSimilarityStrategy()
    {
		return nss;
	}

	/**
	 * @return the active SelectionType
	 */
	public SelectionType getSelectionType()
	{
		return sType;
	}
	
	/**
	 * @return the SizeCategory of the current ontology pair
	 */
	public SizeCategory getSizeCategory()
	{
		return size;
	}
	
	/**
	 * @return the active StringSimMeasure
	 */
	public StringSimMeasure getStringSimMeasure()
	{
		return ssm;
	}

	/**
	 * @return the active similarity threshold
	 */
	public double getThreshold()
    {
    	return threshold;
    }
    
	/**
	 * @return the active WordMatchStrategy
	 */
    public WordMatchStrategy getWordMatchStrategy()
    {
		return wms;
	}
    
    
    public boolean isHierarchic()
    {
		return hierarchic;
	}
    
    public boolean isToMatch(String uri)
    {
    	return individualsToMatch.contains(uri);
	}

    /**
     * @return whether to use the AutomaticMatcher or the ManualMatcher 
     */
    public boolean matchAuto()
    {
    	return matchAuto;
    }
    
    /**
     * Sets the matchAuto parameter
     * @param auto: whether to use the AutomaticMatcher or the ManualMatcher
     */
    public void matchAuto(boolean auto)
    {
    	matchAuto = auto;
    }
    
    /**
     * @return whether class matching is on
     */
    public boolean matchClasses()
    {
    	return matchClasses;
    }
    
    /**
     * Sets the matchClasses parameter that determines
     * whether ontology classes will be matched
     * @param match: whether to match classes
     */
    public void matchClasses(boolean match)
    {
    	matchClasses = match;
    }
    
    /**
     * @return whether individual matching is on
     */
    public boolean matchIndividuals()
    {
    	return matchIndividuals;
    }
    
    /**
     * Sets the matchIndividuals parameter that determines
     * whether ontology individuals will be matched
     * @param match: whether to match individuals
     */
    public void matchIndividuals(boolean match)
    {
    	matchIndividuals = match;
    }

    /**
     * @return whether property matching is on
     */
    public boolean matchProperties()
    {
    	return matchProperties;
    }
    
    /**
     * Sets the matchProperties parameter that determines
     * whether ontology properties will be matched
     * @param match: whether to match properties
     */
    public void matchProperties(boolean match)
    {
    	matchProperties = match;
    }
    
    /**
     * @return whether same URI matching is on
     */
    public boolean matchSameURI()
    {
    	return matchSameURI;
    }
    
    /**
     * Sets the matchSameURI parameter that determines
     * whether entities with the same URI will be matched
     * @param match: whether to match entities with the same URI
     */
    public void matchSameURI(boolean match)
    {
    	matchSameURI = match;
    }
    
    /**
     * @return whether to perform global string matching
     */
    public boolean primaryStringMatcher()
    {
		return primaryStringMatcher;
	}
    
    /**
     * @return the instance matching category
     */
	public InstanceMatchingCategory getInstanceMatchingCategory()
	{
		return inst;
	}
    
    /**
     * @return whether repair is on
     */
    public boolean repair()
    {
    	return repair;
    }
    
    /**
     * Sets the repair parameter
     * @param repair: whether to perform repair or not
     */
    public void repair(boolean repair)
    {
    	this.repair = repair;
    }

    /**
     * Sets the complexMatch parameter
     * @param c: whether to perform complex match or simple match
     */
	public void setComplexMatch(boolean c)
	{
		complexMatch = c;
	}

	/**
	 * Sets the directNeighbors parameter
	 * @param directNeighbors: whether to use direct neighbors only when performing structural matching
	 */
 	public void setDirectNeighbors(boolean directNeighbors)
	{
		this.directNeighbors = directNeighbors;
	}

 	/**
 	 * 
 	 * @param hierarchic
 	 */
	public void setHierarchic(boolean hierarchic)
	{
		this.hierarchic = hierarchic;
	}
	
	public void setLanguageSetting(LanguageSetting s)
	{
		lang = s;
	}
	
	public void setMatchSteps(List<MatchStep> steps)
	{
		this.steps = steps;
	}
	
	public void setNeighborSimilarityStrategy(NeighborSimilarityStrategy nss)
	{
		this.nss = nss;
	}

	public void setPrimaryStringMatcher(boolean primary)
	{
		primaryStringMatcher = primary;
	}
	
	public void setInstanceMatchingCategory(InstanceMatchingCategory cat)
	{
		inst = cat;
	}
	
	public void setSelectionType(SelectionType s)
	{
		if(s == null)
			sType = SelectionType.getSelectionType(size);
		else
			sType = s;
	}
	
	/**
	 * Sets the set of classes whose individuals are to be matched
	 * @param classesToMatch: the set of classes to match
	 */
	public void setClassesToMatch(Set<String> classesToMatch)
	{
		individualsToMatch = new HashSet<String>();
		for(String s : classesToMatch)
			individualsToMatch.addAll(SemanticMap.getInstance().getClassIndividualsTransitive(s));
	}
	
	public void setStringSimMeasure(StringSimMeasure ssm)
	{
		this.ssm = ssm;
	}
	
	public void setStructuralSelection(boolean structuralSelection)
	{
		this.structuralSelection = structuralSelection;
	}

	public void setThreshold(double thresh)
	{
		threshold = thresh;
	}
	
	public void setWordMatchStrategy(WordMatchStrategy wms)
	{
		this.wms = wms;
	}

	public boolean structuralSelection()
	{
		return structuralSelection;
	}
}