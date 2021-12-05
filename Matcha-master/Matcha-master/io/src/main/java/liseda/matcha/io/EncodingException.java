/******************************************************************************
* Exception for OWL<->EDOAL and similar encoding problems.                    *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.io;

import java.io.IOException;

public class EncodingException extends IOException
{
	private static final long serialVersionUID = -5860074144162980836L;

	public EncodingException()
	{
		super();
	}

	public EncodingException(String message)
	{
		super(message);
	}

	public EncodingException(Throwable cause)
	{
		super(cause);
	}

	public EncodingException(String message, Throwable cause)
	{
		super(message, cause);
	}
}