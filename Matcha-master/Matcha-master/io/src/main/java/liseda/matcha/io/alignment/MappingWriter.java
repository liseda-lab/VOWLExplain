

package liseda.matcha.io.alignment;

import liseda.matcha.alignment.Mapping;
import liseda.matcha.io.EncodingException;

public interface MappingWriter
{
	public void writeMapping(Mapping m) throws EncodingException;
}