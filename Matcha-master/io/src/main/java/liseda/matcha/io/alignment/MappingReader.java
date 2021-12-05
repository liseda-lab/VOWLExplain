/******************************************************************************
* Interface for reading mappings in an alignment file.                        *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io.alignment;

import liseda.matcha.io.EncodingException;

public interface MappingReader<E>
{
	public void readMapping(E e) throws EncodingException;
}