package hu.mas.core.agent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToDoubleBiFunction;

import hu.mas.core.mas.model.Edge;

public class Vehicle {

	public static final AtomicInteger SEQUENCE = new AtomicInteger(0);

	private String id;

	private String typeId;

	private double maxSpeed;

	private double length;

	private ToDoubleBiFunction<Edge, Vehicle> caculateEdgeImpact;

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

	public double caculateEdgeImpact(Edge edge) {
		return caculateEdgeImpact.applyAsDouble(edge, this);
	}

	public double calculateTravelTime(Edge edge) {
		double speedOnEdge = edge.getSpeed() >= this.maxSpeed ? this.maxSpeed : edge.getSpeed();
		return edge.getLength() / speedOnEdge;
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

	public ToDoubleBiFunction<Edge, Vehicle> getCaculateEdgeImpact() {
		return caculateEdgeImpact;
	}

	public void setCaculateEdgeImpact(ToDoubleBiFunction<Edge, Vehicle> caculateEdgeImpact) {
		this.caculateEdgeImpact = caculateEdgeImpact;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	@Override
	public String toString() {
		return "Vehicle [id=" + id + ", typeId=" + typeId + ", maxSpeed=" + maxSpeed + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(maxSpeed);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehicle other = (Vehicle) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (Double.doubleToLongBits(maxSpeed) != Double.doubleToLongBits(other.maxSpeed))
			return false;
		if (typeId == null) {
			if (other.typeId != null)
				return false;
		} else if (!typeId.equals(other.typeId)) {
			return false;
		}
		return true;
	}

}
