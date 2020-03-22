package hu.mas.core.agent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;

public class Route {

	public static final AtomicInteger SEQUENCE = new AtomicInteger(0);

	private String id;

	private List<Node> nodes;

	private List<Edge> edges;

	private Map<Edge, Pair<Double, Double>> travelTimeForEdges;

	public Route(List<Node> nodes, List<Edge> edges, Map<Edge, Pair<Double, Double>> travelTimeForEdges) {
		this(generateId(), nodes, edges, travelTimeForEdges);
	}

	public Route(String id, List<Node> nodes, List<Edge> edges, Map<Edge, Pair<Double, Double>> travelTimeForEdges) {
		this.id = id;
		this.nodes = nodes;
		this.edges = edges;
		this.travelTimeForEdges = travelTimeForEdges;
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

	public Map<Edge, Pair<Double, Double>> getTravelTimeForEdges() {
		return travelTimeForEdges;
	}

	public void setTravelTimeForEdges(Map<Edge, Pair<Double, Double>> travelTimeForEdges) {
		this.travelTimeForEdges = travelTimeForEdges;
	}

	@Override
	public String toString() {
		return "Route [id=" + id + ", nodes=" + nodes + ", edges=" + edges + ", travelTimeForEdges="
				+ travelTimeForEdges + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + ((travelTimeForEdges == null) ? 0 : travelTimeForEdges.hashCode());
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
		if (travelTimeForEdges == null) {
			if (other.travelTimeForEdges != null) {
				return false;
			}
		} else if (!travelTimeForEdges.equals(other.travelTimeForEdges)) {
			return false;
		}
		return true;
	}

}
