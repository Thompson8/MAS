package hu.mas.core.mas.model.graph;

import hu.mas.core.mas.util.CalculatorUtil;

public abstract class AbstractEdge {

	protected String id;

	protected double speed;

	protected double length;

	protected double weigth;

	protected final Road road;

	public AbstractEdge(String id, double speed, double length) {
		if (id == null || speed <= 0 || length <= 0) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.speed = speed;
		this.length = length;
		this.weigth = calculateEmptyEdgeTravelTime();
		this.road = new Road(this);
	}

	public double calculateEmptyEdgeTravelTime() {
		return CalculatorUtil.calculateTravelTimeOnEdge(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getWeigth() {
		return weigth;
	}

	public void setWeigth(double weigth) {
		this.weigth = weigth;
	}

	public Road getRoad() {
		return road;
	}

}
