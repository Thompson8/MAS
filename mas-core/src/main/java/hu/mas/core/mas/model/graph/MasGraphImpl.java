package hu.mas.core.mas.model.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class MasGraphImpl implements MasGraph {

	private final Map<String, Vertex> vertexes;

	private final Map<String, Edge> edges;

	private final Graph<String, DefaultWeightedEdge> graph;

	public MasGraphImpl() {
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.vertexes = new HashMap<>();
		this.edges = new HashMap<>();
	}

	@Override
	public boolean addVertex(String id) {
		boolean result = graph.addVertex(id);
		if (result) {
			Vertex vertex = new Vertex(id);
			vertexes.put(id, vertex);
		}

		return result;
	}

	@Override
	public boolean addEdge(String id, String from, String to, double speed, double length) {
		DefaultWeightedEdge edge = graph.addEdge(from, to);
		if (edge != null) {
			Vertex vertexFrom = vertexes.get(from);
			Vertex vertexTo = vertexes.get(to);
			Edge interalEdge = new Edge(id, vertexFrom, vertexTo, speed, length);
			vertexFrom.getEdges().add(interalEdge);
			vertexTo.getEdges().add(interalEdge);
			graph.setEdgeWeight(edge, interalEdge.getWeigth());
			edges.put(id, interalEdge);

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean containsVertex(String id) {
		return vertexes.containsKey(id);
	}

	@Override
	public Optional<Vertex> findVertex(String id) {
		return Optional.ofNullable(vertexes.get(id));
	}

	@Override
	public boolean containsEdge(String id) {
		return edges.containsKey(id);
	}

	@Override
	public boolean containsEdge(String from, String to) {
		return graph.containsEdge(from, to);
	}

	@Override
	public boolean updateEdgeWeight(String id, double weigth) {
		return updateEdgeWeight(edges.get(id), weigth);
	}

	@Override
	public boolean updateEdgeWeight(Edge edge, double weigth) {
		if (edge != null) {
			edge.setWeigth(weigth);
			graph.setEdgeWeight(edge.getFrom().getId(), edge.getTo().getId(), weigth);

			return true;
		} else {
			return false;
		}
	}

	@Override
	public Optional<Edge> findEdge(String id) {
		return Optional.ofNullable(edges.get(id));
	}

	@Override
	public Collection<Vertex> getVertexes() {
		return vertexes.values();
	}

	@Override
	public Collection<Edge> getEdges() {
		return edges.values();
	}

	public Graph<String, DefaultWeightedEdge> getGraph() {
		return graph;
	}

}
