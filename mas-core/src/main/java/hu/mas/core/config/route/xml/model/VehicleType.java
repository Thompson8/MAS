package hu.mas.core.config.route.xml.model;

import javax.xml.bind.annotation.XmlAttribute;

public class VehicleType {

	private String id;
	
	private double maxSpeed;
	
	private double length;

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "maxSpeed")
	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	@XmlAttribute(name = "length")
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}
	
}
