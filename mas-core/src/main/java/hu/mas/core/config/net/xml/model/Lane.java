package hu.mas.core.config.net.xml.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Lane {

	private String id;

	private double speed;

	private double length;
	
	private String allow;
	
	private String disallow;

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "speed")
	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@XmlAttribute(name = "length")
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}
	
	@XmlAttribute(name = "allow")
	public String getAllow() {
		return allow;
	}

	public void setAllow(String allow) {
		this.allow = allow;
	}

	@XmlAttribute(name = "disallow")
	public String getDisallow() {
		return disallow;
	}

	public void setDisallow(String disallow) {
		this.disallow = disallow;
	}

	@Override
	public String toString() {
		return "Lane [id=" + id + ", speed=" + speed + ", length=" + length + ", allow=" + allow + ", disallow="
				+ disallow + "]";
	}

}
