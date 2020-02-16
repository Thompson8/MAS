package hu.mas.core.config.net.xml.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Connection {

	private String from;
	
	private String to;

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

	@Override
	public String toString() {
		return "Connection [from=" + from + ", to=" + to + "]";
	}
	
}
