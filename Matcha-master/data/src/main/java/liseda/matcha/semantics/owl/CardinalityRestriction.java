/******************************************************************************
* An expression that restricts the cardinality of a given property.           *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import liseda.matcha.semantics.Expression;

public interface CardinalityRestriction extends Expression
{
	/**
	 * @return the cardinality of this restriction
	 */
	public int getCardinality();
	
	/**
	 * @return whether the restriction is qualified
	 */
	public boolean isQualified();
}