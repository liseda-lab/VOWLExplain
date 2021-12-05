/******************************************************************************
* Abstract Matcher with hash-based global matching.                           *
*                                                                             *
* @authors Daniel Faria                                                       *
******************************************************************************/
package liseda.matcha.match;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.EntityType;

public abstract class AbstractHashMatcher extends AbstractAlignmentGenerator implements Matcher
{

//Constructors
	
	/**
	 * Constructs a new AbstractParallelMatcher
	 */
	public AbstractHashMatcher(){}

//Public Methods
	
	@Override
	public Alignment match(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		Alignment a = new Alignment(o1,o2);
		if(!checkEntityType(e))
			return a;
		System.out.println("Running " + name + " in match mode");
		long time = System.currentTimeMillis()/1000;
		a.addAll(hashMatch(o1,o2,e,thresh));
		time = System.currentTimeMillis()/1000 - time;
		System.out.println("Finished in " + time + " seconds");
		return a;
	}
		
//Protected Methods
	
	protected abstract Alignment hashMatch(Ontology o1, Ontology o2, EntityType e, double thresh);	
}