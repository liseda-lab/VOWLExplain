package liseda.match.evaluate;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.alignment.MappingStatus;

public class SimpleEvaluator extends Evaluator
{
	
//Constructors
	
	public SimpleEvaluator(){}
	
//Public Methods
	
	@Override
	public void evaluate(Alignment algn, Alignment ref)
	{
		expected = ref.size();
		found = algn.size();
		correct = 0;
		incorrect = 0;
		
		//Ignore "related" mappings in the reference
		for(Mapping m : ref)
			if(m.getRelationship().equals(MappingRelation.RELATED))
				expected--;
		
		for(Mapping m : algn)
		{
			if(ref.contains(m))
			{
				Mapping n = ref.get(m.getEntity1(), m.getEntity2());
				//Ignore "related" mappings in the reference
				if(n.getRelationship().equals(MappingRelation.RELATED))
					found--;
				else if(m.getRelationship().equals(n.getRelationship()))
				{
					correct++;
					m.setStatus(MappingStatus.CORRECT);
				}
				else
				{
					incorrect++;
					m.setStatus(MappingStatus.INCORRECT);
				}
			}
			else
			{
				incorrect++;
				m.setStatus(MappingStatus.INCORRECT);
			}
		}
		precision = 1.0*correct/found;
		recall = 1.0*correct/expected;
		fmeasure = 2*precision*recall/(precision+recall);
	}
}
