package hu.mas.core.mas.model;

public class Edge {

	private String id;

	private Node from;

	private Node to;

	private double speed;

	private double length;

	public Edge(String id, Node from, Node to, double speed, double length) {
		if (id == null || from == null || to == null || speed <= 0 || length <= 0) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.from = from;
		this.to = to;
		this.speed = speed;
		this.length = length;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Node getFrom() {
		return from;
	}

	public void setFrom(Node from) {
		this.from = from;
	}

	public Node getTo() {
		return to;
	}

	public void setTo(Node to) {
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

	@Override
	public String toString() {
		return "Edge [id=" + id + ", from=" + from.getId() + ", to=" + to.getId() + ", speed=" + speed + ", length="
				+ length + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Edge other = (Edge) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
