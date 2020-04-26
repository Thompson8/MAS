package hu.mas.core.mas.model.graph;

import java.util.Collection;
import java.util.Optional;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public interface MasGraph {

	public boolean addVertex(String id);

	public boolean addEdge(String id, String from, String to, double speed, double length);

	public boolean containsVertex(String id);

	public Optional<Vertex> findVertex(String id);

	public boolean containsEdge(String id);

	public boolean containsEdge(String from, String to);

	public boolean updateEdgeWeight(String id, double weigth);

	public boolean updateEdgeWeight(Edge edge, double weigth);

	public Optional<Edge> findEdge(String id);

	public Collection<Vertex> getVertexes();

	public Collection<Edge> getEdges();

	public Graph<String, DefaultWeightedEdge> getGraph();

	public boolean containsInternalEdge(String id);

	public Optional<InternalEdge> findInternalEdge(String id);

	public boolean addInternalEdge(InternalEdge internalEdge);

}
