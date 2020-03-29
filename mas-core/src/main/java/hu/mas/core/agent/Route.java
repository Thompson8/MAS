package hu.mas.core.agent;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Vertex;
import hu.mas.core.util.Pair;

public class Route {

	public static final AtomicInteger SEQUENCE = new AtomicInteger(1);

	private String id;

	private List<Vertex> nodes;

	private List<Edge> edges;

	private Map<Edge, Pair<Double, Double>> travelTimeForEdges;

	public Route(List<Vertex> nodes, List<Edge> edges, Map<Edge, Pair<Double, Double>> travelTimeForEdges) {
		this(generateId(), nodes, edges, travelTimeForEdges);
	}

	public Route(String id, List<Vertex> nodes, List<Edge> edges, Map<Edge, Pair<Double, Double>> travelTimeForEdges) {
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

	public List<Vertex> getNodes() {
		return nodes;
	}

	public void setNodes(List<Vertex> nodes) {
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
		return "Route [id=" + id + ", routeNodes=" + nodes + ", routeEdges=" + edges + ", travelTimeForEdges="
				+ travelTimeForEdges + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(edges, id, nodes, travelTimeForEdges);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Route)) {
			return false;
		}
		Route other = (Route) obj;
		return Objects.equals(edges, other.edges) && Objects.equals(id, other.id) && Objects.equals(nodes, other.nodes)
				&& Objects.equals(travelTimeForEdges, other.travelTimeForEdges);
	}

}
