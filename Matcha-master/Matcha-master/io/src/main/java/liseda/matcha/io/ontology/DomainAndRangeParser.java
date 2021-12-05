/******************************************************************************
* Utility class for parsing property ranges and domains and adding them to    *
* the SemanticMap.                                                            *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.ontology;

import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataRange;

import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.semantics.owl.ClassExpression;
import liseda.matcha.semantics.owl.ClassIntersection;

public class DomainAndRangeParser
{
	public static void addDomain(String propUri, Set<OWLClassExpression> domains)
	{
		if(domains.size() == 0)
			return;
		SemanticMap sm = SemanticMap.getInstance();
		if(domains.size() == 1)
		{
			ClassExpression x = OWLExpressionParser.parse(domains.iterator().next());
			if(x == null)
				return;
			sm.addExpression(x);
			sm.addDomain(propUri, x.toString());
		}
		else
		{
			Vector<ClassExpression> v = new Vector<ClassExpression>();
			for(OWLClassExpression ce : domains)
			{
				ClassExpression x = OWLExpressionParser.parse(ce);
				if(x != null)
					v.add(x);
			}
			ClassIntersection ci = new ClassIntersection(v);
			sm.addExpression(ci);
			sm.addDomain(propUri, ci.toString());
		}
	}
	
	public static void addDataRange(String propUri, Set<OWLDataRange> ranges)
	{
		if(ranges.size() == 0)
			return;
		SemanticMap sm = SemanticMap.getInstance();
		if(ranges.size() == 1)
		{
			OWLDataRange dr = ranges.iterator().next();
			//TODO: handle datatypes defined in the ontology
			if(dr.isOWLDatatype())
				sm.addRange(propUri, dr.asOWLDatatype().toStringID());
		}
		else
		{
			//TODO: handle intersections of datatypes
		}
	}
	
	public static void addObjectRange(String propUri, Set<OWLClassExpression> ranges)
	{
		if(ranges.size() == 0)
			return;
		SemanticMap sm = SemanticMap.getInstance();
		if(ranges.size() == 1)
		{
			ClassExpression x = OWLExpressionParser.parse(ranges.iterator().next());
			if(x == null)
				return;
			sm.addExpression(x);
			sm.addRange(propUri, x.toString());
		}
		else
		{
			Vector<ClassExpression> v = new Vector<ClassExpression>();
			for(OWLClassExpression ce : ranges)
			{
				ClassExpression x = OWLExpressionParser.parse(ce);
				if(x != null)
					v.add(x);
			}
			ClassIntersection ci = new ClassIntersection(v);
			sm.addExpression(ci);
			sm.addRange(propUri, ci.toString());
		}
	}
}