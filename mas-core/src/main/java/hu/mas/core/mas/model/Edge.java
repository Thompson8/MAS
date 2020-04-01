package hu.mas.core.mas.model;

import java.util.Objects;

public class Edge {

	private String id;

	private Vertex from;

	private Vertex to;

	private double speed;

	private double length;
	
	private double weigth;

	public Edge(String id, Vertex from, Vertex to, double speed, double length) {
		if (id == null || from == null || to == null || speed <= 0 || length <= 0) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.from = from;
		this.to = to;
		this.speed = speed;
		this.length = length;
		this.weigth = calculateBaseTravelTime();
	}

	public double calculateBaseTravelTime() {
		return length / speed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vertex getFrom() {
		return from;
	}

	public void setFrom(Vertex from) {
		this.from = from;
	}

	public Vertex getTo() {
		return to;
	}

	public void setTo(Vertex to) {
		this.to = to;
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

	@Override
	public String toString() {
		return "Edge [id=" + id + ", from=" + from + ", to=" + to + ", speed=" + speed + ", length=" + length
				+ ", weigth=" + weigth + "]";
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
		if (!(obj instanceof Edge)) {
			return false;
		}
		Edge other = (Edge) obj;
		return Objects.equals(id, other.id);
	}


}
