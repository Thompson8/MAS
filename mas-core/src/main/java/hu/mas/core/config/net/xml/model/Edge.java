package hu.mas.core.config.net.xml.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Edge {

	private String id;

	private String from;
	
	private String to;
	
	private String function;
	
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@XmlAttribute(name = "from")
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@XmlAttribute(name = "to")
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@XmlAttribute(name = "function")
	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	@Override
	public String toString() {
		return "Edge [id=" + id + ", from=" + from + ", to=" + to + ", function=" + function + "]";
	}

}
