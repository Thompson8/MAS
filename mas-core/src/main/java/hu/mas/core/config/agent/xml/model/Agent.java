package hu.mas.core.config.agent.xml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Agent {

	private String id;

	private Vehicle vehicle;

	private String from;

	private String to;

	private Integer sleepTime;

	private Integer agentStartInterval;

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "vehicle")
	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
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

	@XmlAttribute(name = "sleepTime")
	public Integer getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(Integer sleepTime) {
		this.sleepTime = sleepTime;
	}

	@XmlAttribute(name = "agentStartInterval")
	public Integer getAgentStartInterval() {
		return agentStartInterval;
	}

	public void setAgentStartInterval(Integer agentStartInterval) {
		this.agentStartInterval = agentStartInterval;
	}

	@Override
	public String toString() {
		return "Agent [id=" + id + ", vehicle=" + vehicle + ", from=" + from + ", to=" + to + ", sleepTime=" + sleepTime
				+ ", agentStartInterval=" + agentStartInterval + "]";
	}

}
