/******************************************************************************
* Lists the types of ontology entities.                                       *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.semantics;

public enum EntityType
{
    ANNOTATION_PROP		("Annotation Property"),
	ANON_INDIVIDUAL     ("Anonymous Individual"),
	CLASS				("Class"),
	CLASS_EXPRESSION	("Class Expression"),
    DATA_EXPRESSION		("Data Property Expression"),
    DATA_PROP			("Data Property"),
	DATATYPE			("Datatype"),
	INDIVIDUAL			("Individual"),
	LITERAL				("Literal"),
    OBJECT_PROP			("Object Property"),
    OBJECT_EXPRESSION	("Object Property Expression");
    
    String label;
    
    EntityType(String s)
    {
    	label = s;
    }
    
    public String toString()
    {
    	return label;
    }
}