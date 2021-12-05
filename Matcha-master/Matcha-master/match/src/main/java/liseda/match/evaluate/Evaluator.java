package liseda.match.evaluate;

import liseda.matcha.alignment.Alignment;

public abstract class Evaluator
{	
	
//Attributes
	
	protected int correct;
	protected int expected;
	protected int found;
	protected int incorrect;
	protected double fmeasure;
	protected double precision;
	protected double recall;

// Public Methods

	/**
	 * @param alg: the Alignment to evaluate
	 * @param ref: the reference Alignment
	 */
	public abstract void evaluate(Alignment algn, Alignment ref);

	public int getCorrect()
	{
		return correct;
	}
	
	public int getExpected()
	{
		return incorrect;
	}

	public double getFmeasure()
	{
		return fmeasure;
	}

	public int getFound()
	{
		return found;
	}

	public int getIncorrect()
	{
		return incorrect;
	}
	
	public double getPrecision()
	{
		return precision;
	}
	
	public double getRecall()
	{
		return recall;
	}
	
	public String toString()
	{
		String prc = Math.round(precision*1000)/10.0 + "%";
		String rec = Math.round(recall*1000)/10.0 + "%";
		String fms = Math.round(fmeasure*1000)/10.0 + "%";
		return "Precision\tRecall\tF-measure\tFound\tCorrect\tReference\n" + prc +
					"\t" + rec + "\t" + fms + "\t" + found + "\t" + correct + "\t" + expected;
	}
}
