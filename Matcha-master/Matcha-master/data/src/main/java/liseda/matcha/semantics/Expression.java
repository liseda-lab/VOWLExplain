/******************************************************************************
* An expression in an ontology or mapping.                                    *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics;

import java.util.List;
import java.util.Set;

public interface Expression
{
	
//Public Methods
	
	@Override
	public boolean equals(Object o);
	
	/**
	 * @return the ontology entities listed in this Expression
	 */
	public Set<String> getElements();
	
	/**
	 * @return the ordered Expressions that compose this Expression
	 */
	public <E extends Expression> List<E> getComponents();
	
	/**
	 * @return the EntityType to which this Expression corresponds
	 */
	public EntityType getEntityType();
	
	@Override
	public int hashCode();

	@Override
	public String toString();
}