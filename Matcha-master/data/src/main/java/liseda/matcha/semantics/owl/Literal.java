/******************************************************************************
* A Literal.                                                                  *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics.owl;

import java.util.List;

import liseda.matcha.semantics.AbstractExpression;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.Expression;

public class Literal extends AbstractExpression implements ValueExpression
{

//Attributes
	
	protected String type;
	protected String lang;
	protected String value;
	protected String stringForm;
	
//Constructor

	/**
	 * Constructs a new Literal of the given type, language and value
	 * @param type: the type of the Literal (use null for rdfs:PlainLiteral)
	 * @param lang: the language of the Literal (use null if none is declared)
	 * @param value: the value of the Literal (must not be null)
	 */
	public Literal(String value, String type, String lang)
	{
		super();
		if(value == null || value.equals(""))
			throw new IllegalArgumentException("The value of a literal cannot be null or an empty string");
		this.value = value;
		if(type == null)
			this.type = "";
		else
			this.type = type;
		if(lang == null)
			this.lang = "";
		else
			this.lang = lang;
		stringForm = "\"" + value + (lang.equals("") ? "" : "@" + lang) + "\"" +
				(type.equals("") ? "" :"^^" + type);
	}

//Public Methods
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Literal)
		{
			Literal l = (Literal)o;
			return l.type.equals(this.type) &&
				l.value.equals(this.value) &&
				((l.lang == null && this.lang == null) || l.lang.equals(this.lang));
		}
		else
			return false;
	}
	
	@Override
	public List<Expression> getComponents()
	{
		return null;
	}
	
	@Override
	public EntityType getEntityType()
	{
		return EntityType.LITERAL;
	}

	/**
	 * @return the language of this Literal
	 */
	public String getLanguage()
	{
		return lang;
	}
	
	/**
	 * @return the type of this Literal
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * @return the value of this Literal
	 */
	public String getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return stringForm;
	}
}