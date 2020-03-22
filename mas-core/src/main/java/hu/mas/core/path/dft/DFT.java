package hu.mas.core.path.dft;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import hu.mas.core.path.PathFinder;
import hu.mas.core.util.Pair;

public class DFT implements PathFinder {

	@Override
	public List<Pair<Double, Route>> getShortestPaths(final Graph graph, Node nodeFrom, Node nodeTo,
			double[][] travelWeigthMatrix, Edge[][] edgeMatrix, Vehicle vehicle, double currentTime) {
		DFTGraph dftGraph = new DFTGraph(graph.getNodes().size());
		for (int i = 0; i < graph.getNodes().size(); i++) {
			Node node = toNode(graph, i);
			final int currenIndex = i;
			node.getIncomingEdges().stream().map(Edge::getFrom).forEach(e -> {
				int fromIndex = indexOfNode(graph, e);
				dftGraph.addEdge(fromIndex, currenIndex);
			});
			node.getOutgoingEdges().stream().map(Edge::getTo).forEach(e -> {
				int toIndex = indexOfNode(graph, e);
				dftGraph.addEdge(currenIndex, toIndex);
			});

		}

		List<Pair<List<Node>, List<Edge>>> routes = dftGraph
				.getAllPaths(indexOfNode(graph, nodeFrom), indexOfNode(graph, nodeTo)).stream()
				.map(e -> e.stream().map(a -> toNode(graph, a)).collect(Collectors.toList())).distinct()
				.map(e -> new Pair<>(e, toEdges(e))).collect(Collectors.toList());

		return routes.stream().map(e -> {
			Map<Edge, Pair<Double, Double>> travelTimeForEdges = calculateTravelTimeForEdges(e.getRigth(), vehicle,
					currentTime, travelWeigthMatrix, edgeMatrix);
			double actualTravelTime = travelTimeForEdges.values().stream().map(Pair::getRigth)
					.max((a, b) -> a.compareTo(b)).orElseThrow();
			return new Pair<>(actualTravelTime, new Route(e.getLeft(), e.getRigth(), travelTimeForEdges));
		}).collect(Collectors.toList());
	}

	private int indexOfNode(Graph graph, Node node) {
		return graph.getNodes().indexOf(node);
	}

	private Node toNode(Graph graph, int index) {
		return graph.getNodes().get(index);
	}

}
