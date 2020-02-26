package hu.mas.core.agent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Node;

public class Route {

	public static final AtomicInteger SEQUENCE = new AtomicInteger(0);

	private String id;

	private List<Node> nodes;

	private List<Edge> edges;

	public Route(List<Node> nodes, List<Edge> edges) {
		this(generateId(), nodes, edges);
	}

	public Route(String id, List<Node> nodes, List<Edge> edges) {
		this.id = id;
		this.nodes = nodes;
		this.edges = edges;
	}

	public static String generateId() {
		return "Route_" + SEQUENCE.getAndIncrement();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	@Override
	public String toString() {
		return "Route [id=" + id + ", nodes=" + nodes + ", edges=" + edges + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
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
		Route other = (Route) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges)) {
			return false;
		}
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		return true;
	}

}
