package hu.mas.core.config.net.xml.model;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;

public class Connection {

	private String from;
	
	private String to;

	private Integer fromLane;
	
	private Integer toLane;
	
	private String via;
	
	private String dir;
	
	private String state;
	
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

	@XmlAttribute(name = "fromLane")
	public Integer getFromLane() {
		return fromLane;
	}

	public void setFromLane(Integer fromLane) {
		this.fromLane = fromLane;
	}

	@XmlAttribute(name = "toLane")
	public Integer getToLane() {
		return toLane;
	}

	public void setToLane(Integer toLane) {
		this.toLane = toLane;
	}

	@XmlAttribute(name = "via")
	public String getVia() {
		return via;
	}

	public void setVia(String via) {
		this.via = via;
	}

	@XmlAttribute(name = "dir")
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	@XmlAttribute(name = "state")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Connection [from=" + from + ", to=" + to + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Connection)) {
			return false;
		}
		Connection other = (Connection) obj;
		return Objects.equals(from, other.from) && Objects.equals(to, other.to);
	}
	
}
