/******************************************************************************
* Customizable ensemble of problem filtering / flagging algorithms.           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.filter;

import java.util.List;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.filter.cardinality.CardinalityFilterer;
import liseda.matcha.filter.logic.Repairer;
import liseda.matcha.filter.quality.ObsoleteFilterer;
import liseda.matcha.filter.quality.QualityFlagger;
import liseda.matcha.settings.Problem;
import liseda.matcha.settings.Settings;

public class FilterEnsemble implements Filterer, Flagger
{
	
//Attributes
	
	private List<Problem> steps;
	
//Constructors	
	
	public FilterEnsemble(List<Problem> steps)
	{
		this.steps = steps;
	}
	
//Public Methods

	@Override
	public Alignment filter(Alignment a)
	{
		Alignment out = new Alignment(a);
		Settings set = Settings.getInstance();
		for(Problem p : steps)
		{
			if(p.equals(Problem.OBSOLETE))
			{
				ObsoleteFilterer o = new ObsoleteFilterer();
				out = o.filter(out);
			}
			else if(p.equals(Problem.CARDINALITY))
			{
				CardinalityFilterer s = new CardinalityFilterer(set.getSelectionType(), set.getThreshold());
				out = s.filter(out);
			}
			else if(p.equals(Problem.COHERENCE))
			{
				Repairer r = new Repairer();
				out = r.filter(out);
			}
		}
		return out;
	}
	
	@Override
	public void flag(Alignment a)
	{
		//For flagging, the order of the steps doesn't matter
		if(steps.contains(Problem.CARDINALITY))
		{
			CardinalityFilterer s = new CardinalityFilterer(Settings.getInstance().getSelectionType(), 0.0);
			s.flag(a);
		}
		if(steps.contains(Problem.COHERENCE))
		{
			Repairer r = new Repairer();
			r.flag(a);
		}
		if(steps.contains(Problem.OBSOLETE))
		{
			ObsoleteFilterer o = new ObsoleteFilterer();
			o.flag(a);
		}
		if(steps.contains(Problem.QUALITY))
		{
			QualityFlagger q = new QualityFlagger();
			q.flag(a);
		}
	}
}