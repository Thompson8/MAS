package hu.mas.core.agent;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import hu.mas.core.mas.model.Edge;

public class Vehicle {

	public static final AtomicInteger SEQUENCE = new AtomicInteger(1);

	private String id;

	private String typeId;

	private double maxSpeed;

	private double length;

	public Vehicle(String typeId, double maxSpeed, double length) {
		this(generateId(), typeId, maxSpeed, length);
	}

	public Vehicle(String id, String typeId, double maxSpeed, double length) {
		this.id = id;
		this.typeId = typeId;
		this.maxSpeed = maxSpeed;
		this.length = length;
	}

	public static String generateId() {
		return "Vehicle_" + SEQUENCE.getAndIncrement();
	}

	public double calculateSpeed(Edge edge) {
		return edge.getSpeed() >= this.maxSpeed ? this.maxSpeed : edge.getSpeed();
	}

	public double calculateTravelTime(Edge edge) {
		return edge.getLength() / calculateSpeed(edge);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double speed) {
		this.maxSpeed = speed;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	@Override
	public String toString() {
		return "Vehicle [id=" + id + ", typeId=" + typeId + ", maxSpeed=" + maxSpeed + ", length=" + length + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Vehicle)) {
			return false;
		}
		Vehicle other = (Vehicle) obj;
		return Objects.equals(id, other.id);
	}

}
