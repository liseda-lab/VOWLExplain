/******************************************************************************
* Filtering algorithm that removes/flags mappings involving obsolete classes. *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.filter.quality;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingStatus;
import liseda.matcha.filter.Filterer;
import liseda.matcha.filter.Flagger;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.semantics.SemanticMap;

public class ObsoleteFilterer implements Filterer, Flagger
{

//Constructors
	
	public ObsoleteFilterer(){}
	
//Public Methods
	
	@Override
	public Alignment filter(Alignment a)
	{
		System.out.println("Running Obsoletion Filter");
		long time = System.currentTimeMillis()/1000;
		Ontology source = a.getSourceOntology();
		Ontology target = a.getTargetOntology();
		SemanticMap sm = SemanticMap.getInstance();
		Alignment out = new Alignment(source, target);
		for(Mapping m : a)
		{
			boolean add = !m.getStatus().equals(MappingStatus.INCORRECT);
			if(add && !m.getStatus().equals(MappingStatus.CORRECT))
			{
				String s = m.getEntity1();
				String t = m.getEntity2();
				if(sm.isExpression(s))
				{
					for(String ex : sm.getExpression(s).getElements())
					{
						if(source.isDeprecated(ex))
						{
							add = false;
							break;
						}
					}
				}
				else
					add = !source.isDeprecated(s);
				if(add)
				{
					if(sm.isExpression(t))
					{
						for(String ex : sm.getExpression(t).getElements())
						{
							if(target.isDeprecated(ex))
							{
								add = false;
								break;
							}
						}
					}
					else
						add = !target.isDeprecated(t);
				}
			}
			if(add)
				out.add(m);
		}
		if(out.size() < a.size())
		{
			for(Mapping m : out)
				if(m.getStatus().equals(MappingStatus.FLAGGED))
					m.setStatus(MappingStatus.UNREVISED);
		}
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
		return out;
	}
	
	@Override
	public void flag(Alignment a)
	{
		System.out.println("Running Obsoletion Flagger");
		long time = System.currentTimeMillis()/1000;
		Ontology source = a.getSourceOntology();
		Ontology target = a.getTargetOntology();
		SemanticMap sm = SemanticMap.getInstance();
		for(Mapping m : a)
		{
			if(!m.getStatus().equals(MappingStatus.CORRECT) && !m.getStatus().equals(MappingStatus.INCORRECT))
			{
				String s = m.getEntity1();
				String t = m.getEntity2();
				if(sm.isExpression(s))
				{
					for(String ex : sm.getExpression(s).getElements())
					{
						if(source.isDeprecated(ex))
						{
							m.setStatus(MappingStatus.FLAGGED);
							break;
						}
					}
				}
				else if(source.isDeprecated(s))
					m.setStatus(MappingStatus.FLAGGED);
				if(!m.getStatus().equals(MappingStatus.FLAGGED))
				{
					if(sm.isExpression(t))
					{
						for(String ex : sm.getExpression(t).getElements())
						{
							if(target.isDeprecated(ex))
							{
								m.setStatus(MappingStatus.FLAGGED);
								break;
							}
						}
					}
					else if(target.isDeprecated(t))
						m.setStatus(MappingStatus.FLAGGED);
				}
			}
		}
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
	}
}