package hu.mas.core.config.sumo.xml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "route-files")
public class RouteFiles {

	private String value;

	@XmlAttribute(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "RouteFiles [value=" + value + "]";
	}	

}
