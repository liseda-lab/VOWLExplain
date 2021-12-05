/******************************************************************************
* The map of data property and annotation property values of individuals in   *
* an ontology.                                                                *
* Adapted from https://github.com/AgreementMakerLight/AML-Project             *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.ontology;

import java.util.HashSet;
import java.util.Set;

import liseda.matcha.data.Map2Map2Map;
import liseda.matcha.data.Map2Map2Set;

public class AttributeMap
{

	//Attributes	

	//The table of values per individual <individual, property, value, datatype>
	private Map2Map2Map<String,String,String,String> individuals;
	//The inverted table of values <value, property, individual>
	private Map2Map2Set<String,String,String> values;
	//The table of values per property <property, value, individual>
	private Map2Map2Set<String,String,String> properties;
	
	//Constructors

	/**
	 * Constructs a new empty ValueMap
	 */
	public AttributeMap()
	{
		individuals = new Map2Map2Map<String,String,String,String>();
		properties = new Map2Map2Set<String,String,String>();
		values = new Map2Map2Set<String,String,String>();
	}

	//Public Methods

	/**
	 * Adds a new entry to the ValueMap
	 * @param indiv: the uri of the individual with the value
	 * @param prop: the uri of the data or annotation property for which the individual has the value
	 * @param value: the value of the individual for the property
	 * @param type: the datatype of the value
	 */
	public void add(String indiv, String prop, String value, String type)
	{
		individuals.add(indiv, prop, value, type);
		properties.add(prop, value, indiv);
		values.add(value, prop, indiv);
	}
	
	/**
	 * @param prop: the uri of the property to search in the ValueMap
	 * @param value: the value of that property to search in the ValueMap
	 * @return the set of Individuals that have the given value for the given property
	 */
	public String getDataType(String indiv, String prop, String value)
	{
		return individuals.get(indiv,prop,value);
	}

	/**
	 * @param prop: the uri of the property to search in the ValueMap
	 * @param value: the value of that property to search in the ValueMap
	 * @return the set of datatypes of the given value for the given property
	 */
	public Set<String> getDataTypes(String prop, String value)
	{
		Set<String> types = new HashSet<String>();
		if(values.contains(value,prop)) 
		{
			for(String indiv: values.get(value,prop))
				types.add(individuals.get(indiv,prop,value));
		}	
		return types;
	}
	
	/**
	 * @return the set of individuals with values in the ValueMap
	 */
	public Set<String> getIndividuals()
	{
		return individuals.keySet();
	}
	
	/**
	 * @param propId: the index of the property to search in the ValueMap
	 * @return the set of Individuals that have the given property
	 */
	public Set<String> getIndividuals(String propId)
	{
		Set<String> indivs = new HashSet<String>();
		if(properties.contains(propId)) 
		{
			Set<String> values =  getValues(propId);
			for (String v: values)
				indivs.addAll(getIndividuals(propId,v));	
		}	
		return indivs;
	}

	/**
	 * @param prop: the uri of the property to search in the ValueMap
	 * @param value: the value of that property to search in the ValueMap
	 * @return the set of Individuals that have the given value for the given property
	 */
	public Set<String> getIndividuals(String prop, String value)
	{
		if(!properties.contains(prop,value))
			return new HashSet<String>();	
		return properties.get(prop,value);
	}

	/**
	 * @return the set of data and annotation properties with values in the ValueMap
	 */
	public Set<String> getProperties()
	{
		return properties.keySet();
	}

	/**
	 * @param indiv: the uri of the individual to search in the ValueMap
	 * @return the set of data and annotation properties with values for the given individual
	 */
	public Set<String> getProperties(String indiv)
	{
		if(individuals.contains(indiv))
			return individuals.keySet(indiv);
		return new HashSet<String>();
	}

	/**
	 * @return the set of values in the ValueMap
	 */
	public Set<String> getValues()
	{
		return values.keySet();
	}
	
	/**
	 * @param prop: the uri of the property to search in the ValueMap
	 * @return the set of values for that property in the ValueMap
	 */
	public Set<String> getValues(String prop)
	{
		if(properties.contains(prop))
			return properties.keySet(prop);
		return new HashSet<String>();
	}

	/**
	 * @param indiv: the uri of the individual to search in the ValueMap
	 * @param prop: the uri of the property to search in the ValueMap
	 * @return the set of values for the individual and property pair
	 */
	public Set<String> getValues(String indiv, String prop)
	{
		if(individuals.contains(indiv,prop))
			return individuals.get(indiv,prop).keySet();
		return new HashSet<String>();
	}

	/**
	 * @return the size of the ValueMap
	 */
	public int size()
	{
		return individuals.size();
	}
}
