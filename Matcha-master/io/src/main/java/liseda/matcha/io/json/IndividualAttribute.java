package liseda.matcha.io.json;

public class IndividualAttribute {
	String IRI;
	String baseIRI;
	String[] label = new String[2];
	String[] attributes = new String[1];
	String motherClass;
	Integer id;
	String type;
	
	
	public IndividualAttribute(String iRI) {
		IRI = iRI;
	}
	public String getIRI() {
		return IRI;
	}
	public void setIRI(String iRI) {
		IRI = iRI;
	}
	public String[] getLabel() {
		return label;
	}
	public void setLabel(String[] label) {
		this.label = label;
	}
	public String[] getAttributes() {
		return attributes;
	}
	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}
	public String getMotherClass() {
		return motherClass;
	}
	public void setMotherClass(String motherClass) {
		this.motherClass = motherClass;
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
	public void setBaseIRI(String baseIRI) {
		this.baseIRI = baseIRI;	
	}
	
}
