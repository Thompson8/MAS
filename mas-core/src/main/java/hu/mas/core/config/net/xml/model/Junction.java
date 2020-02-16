package hu.mas.core.config.net.xml.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Junction {

	private String id;
	
	private String type;

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Junction [id=" + id + ", type=" + type + "]";
	}	
	
}
