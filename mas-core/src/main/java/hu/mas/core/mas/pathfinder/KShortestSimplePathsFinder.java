package hu.mas.core.mas.pathfinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestSimplePaths;
import org.jgrapht.graph.DefaultWeightedEdge;

import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.model.graph.Vertex;
import hu.mas.core.util.Pair;
import hu.mas.core.util.TriFunction;

public class KShortestSimplePathsFinder implements AbstractPathFinder {

	private static final int K = 100;

	@Override
	public List<Pair<Double, MasRoute>> getShortestPaths(Vertex from, Vertex to, Vehicle vehicle, double time,
			MasGraph masGraph, TriFunction<MasRoute, Vehicle, Double, Double> calculateRouteCost) {
		KShortestSimplePaths<String, DefaultWeightedEdge> algorithm = new KShortestSimplePaths<>(masGraph.getGraph());
		List<GraphPath<String, DefaultWeightedEdge>> paths = algorithm.getPaths(from.getId(), to.getId(), K);

		return paths.stream().map(e -> toRoute(e, masGraph, from, to))
				.map(e -> new Pair<>(calculateRouteCost.apply(e, vehicle, time), e))
				.sorted((a, b) -> a.getLeft().compareTo(b.getLeft())).collect(Collectors.toList());
	}

	private MasRoute toRoute(GraphPath<String, DefaultWeightedEdge> path, MasGraph masGraph, Vertex from, Vertex to) {
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

		return new MasRoute(from, to, vertexes, edges);
	}

}
