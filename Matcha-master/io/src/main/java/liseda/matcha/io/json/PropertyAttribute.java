package liseda.matcha.io.json;

public class PropertyAttribute {
	String IRI;
	String baseIRI;
	String[] label = new String[2];
	String[] attributes = new String[1];
	String id;
	String type;
	String domain;
	String range;
	
	
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	
	
}



