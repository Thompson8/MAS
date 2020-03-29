package hu.mas.core.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestSimplePaths;
import org.jgrapht.graph.DefaultWeightedEdge;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.mas.model.Vertex;
import hu.mas.core.util.Pair;

public class KShortestSimplePathsFinder implements PathFinder {

	private static final int K = 100;

	@Override
	public List<Pair<Double, Route>> getShortestPaths(Vertex from, Vertex to, Vehicle vehicle, double currentTime,
			MasGraph masGraph) {
		KShortestSimplePaths<String, DefaultWeightedEdge> alg = new KShortestSimplePaths<>(masGraph.getGraph());
		List<GraphPath<String, DefaultWeightedEdge>> paths = alg.getPaths(from.getId(), to.getId(), K);
		return paths.stream().map(e -> toRoute(e, masGraph, vehicle, currentTime))
				.map(e -> new Pair<>(calculateCost(e), e)).sorted((a, b) -> a.getLeft().compareTo(b.getLeft()))
				.collect(Collectors.toList());
	}

	private Route toRoute(GraphPath<String, DefaultWeightedEdge> path, MasGraph masGraph, Vehicle vehicle,
			double currentTime) {
		List<Vertex> vertexes = path.getVertexList().stream().map(e -> masGraph.findVertex(e).orElseThrow())
				.collect(Collectors.toList());
		List<Edge> edges = new ArrayList<>();
		Iterator<Vertex> vertexIt = vertexes.iterator();
		Vertex current = vertexIt.next();
		Vertex next = null;
		while (vertexIt.hasNext()) {
			next = vertexIt.next();
			final String nextId = next.getId();
			edges.add(current.getOutgoingEdges().stream().filter(e -> e.getTo().getId().equals(nextId)).findFirst()
					.orElseThrow());
			current = next;
		}

		return new Route(vertexes, edges, calculateTravelTimeForEdges(edges, vehicle, currentTime));
	}

}
