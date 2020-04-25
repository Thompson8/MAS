package hu.mas.core.mas.model.graph;

import java.util.Objects;

public class Edge extends AbstractEdge {

	private Vertex from;

	private Vertex to;

	public Edge(String id, Vertex from, Vertex to, double speed, double length) {
		super(id, speed, length);
		if (from == null || to == null) {
			throw new IllegalArgumentException();
		}
		this.from = from;
		this.to = to;
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
