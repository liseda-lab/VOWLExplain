package liseda.matcha.io.json;

public class ClassAttribute {
String IRI;
String baseIRI;
Integer instances;
String[] label = new String[4];
String[] subclasses = new String[1];
String[] attributes = new String[1];
Integer id;
String type;


public ClassAttribute(String iRI) {

	IRI = iRI;
}
public String getIRI() {
	return IRI;
}
public void setIRI(String iRI) {
	IRI = iRI;
}
public String getBaseIRI() {
	return baseIRI;
}
public void setBaseIRI(String baseIRI) {
	this.baseIRI = baseIRI;
}
public Integer getInstances() {
	return instances;
}
public void setInstances(Integer instances) {
	this.instances = instances;
}
public String[] getLabel() {
	return label;
}

public void setLabel(String[] label) {
	this.label[0] = "IRI-based";
	this.label[1] = label[0];
	this.label[2]= label[1];
	this.label[3]= label[2];
}
public String[] getSubclasses() {
	return subclasses;
}
public void setSubclasses(String[] subclasses) {
	this.subclasses = subclasses;
}
public String[] getAttributes() {
	return attributes;
}
public void setAttributes(String[] attributes) {
	this.attributes = attributes;
}
public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}



}


