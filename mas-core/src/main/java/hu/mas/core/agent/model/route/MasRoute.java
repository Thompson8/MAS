package hu.mas.core.agent.model.route;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.Vertex;

public class MasRoute {

	public static final AtomicInteger SEQUENCE = new AtomicInteger(1);

	private String id;

	private Vertex from;

	private Vertex to;

	private List<Vertex> nodes;

	private List<Edge> edges;
	
	public MasRoute(Vertex from, Vertex to, List<Vertex> nodes, List<Edge> edges) {
		this(generateId(), from, to, nodes, edges);
	}

	public MasRoute(String id, Vertex from, Vertex to, List<Vertex> nodes, List<Edge> edges) {
		this.id = id;
		this.from = from;
		this.to = to;
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
		return "Route [id=" + id + ", routeNodes=" + nodes + ", routeEdges=" + edges + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(edges, id, nodes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MasRoute)) {
			return false;
		}
		MasRoute other = (MasRoute) obj;
		return Objects.equals(edges, other.edges) && Objects.equals(id, other.id) && Objects.equals(nodes, other.nodes);
	}

}
