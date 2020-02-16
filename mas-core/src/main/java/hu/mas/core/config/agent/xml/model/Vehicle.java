package hu.mas.core.config.agent.xml.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Vehicle {

	private String id;

	private String typeId;

	private double speed;

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "typeId")
	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	@XmlAttribute(name = "speed")
	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public String toString() {
		return "Vehicle [id=" + id + ", typeId=" + typeId + ", speed=" + speed + "]";
	}

}
