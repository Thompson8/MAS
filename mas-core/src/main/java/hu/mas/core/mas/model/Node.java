package hu.mas.core.mas.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Node {

	private String id;

	private String type;

	private List<Edge> edges;

	public Node(String id, String type) {
		if (id == null || type == null) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.type = type;
		this.edges = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
		return edges.stream().filter(e -> e.getFrom().equals(this)).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", type=" + type + ", edges="
				+ edges.stream().map(Edge::getId).collect(Collectors.toList()) + "]";
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
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}