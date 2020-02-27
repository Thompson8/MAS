package hu.mas.core.agent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToDoubleBiFunction;

import hu.mas.core.mas.model.Edge;

public class Vehicle {

	public static final AtomicInteger SEQUENCE = new AtomicInteger(0);

	private String id;

	private String typeId;

	private double speed;

	private ToDoubleBiFunction<Edge, Vehicle> caculateEdgeImpact;

	private ToDoubleBiFunction<Double, Double> impactEnhancer;

	public Vehicle(String typeId, double speed) {
		this(generateId(), typeId, speed);
	}

	public Vehicle(String id, String typeId, double speed) {
		this.id = id;
		this.typeId = typeId;
		this.speed = speed;
	}

	public static String generateId() {
		return "Vehicle_" + SEQUENCE.getAndIncrement();
	}

	public double caculateEdgeImpact(Edge edge, double edgeWeigth) {
		return impactEnhancer.applyAsDouble(caculateEdgeImpact.applyAsDouble(edge, this), edgeWeigth);
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

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public ToDoubleBiFunction<Edge, Vehicle> getCaculateEdgeImpact() {
		return caculateEdgeImpact;
	}

	public void setCaculateEdgeImpact(ToDoubleBiFunction<Edge, Vehicle> caculateEdgeImpact) {
		this.caculateEdgeImpact = caculateEdgeImpact;
	}

	public ToDoubleBiFunction<Double, Double> getImpactEnhancer() {
		return impactEnhancer;
	}

	public void setImpactEnhancer(ToDoubleBiFunction<Double, Double> impactEnhancer) {
		this.impactEnhancer = impactEnhancer;
	}

	@Override
	public String toString() {
		return "Vehicle [id=" + id + ", typeId=" + typeId + ", speed=" + speed + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(speed);
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
		if (Double.doubleToLongBits(speed) != Double.doubleToLongBits(other.speed))
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
