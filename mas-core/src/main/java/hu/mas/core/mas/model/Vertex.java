package hu.mas.core.mas.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Vertex {

	private String id;

	private List<Edge> edges;

	public Vertex(String id) {
		if (id == null) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.edges = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	public List<Edge> getIncomingEdges() {
		return edges.stream().filter(e -> e.getTo().equals(this)).collect(Collectors.toList());
	}

	public List<Edge> getOutgoingEdges() {
		return edges.stream().filter(e -> e.getFrom().getId().equals(this.getId())).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Vertex [id=" + id + "]";
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
		if (!(obj instanceof Vertex)) {
			return false;
		}
		Vertex other = (Vertex) obj;
		return Objects.equals(id, other.id);
	}

}