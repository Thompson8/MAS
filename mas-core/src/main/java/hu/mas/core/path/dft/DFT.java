package hu.mas.core.path.dft;

import java.util.List;
import java.util.stream.Collectors;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import hu.mas.core.path.PathFinder;
import hu.mas.core.util.Pair;

public class DFT implements PathFinder {

	@Override
	public List<Pair<Double, Pair<List<Node>, List<Edge>>>> getShortestPaths(final Graph graph, Node nodeFrom,
			Node nodeTo, double[][] travelWeigthMatrix, Edge[][] edgeMatrix) {
		DFTGraph dftGraph = new DFTGraph(graph.getNodes().size());
		for (int i = 0; i < graph.getNodes().size(); i++) {
			Node node = toNode(graph, i);
			final int currenIndex = i;
			node.getIncomingEdges().stream().map(Edge::getFrom).forEach(e -> {
				int fromIndex = fromNode(graph, e);
				dftGraph.addEdge(fromIndex, currenIndex);
			});
			node.getOutgoingEdges().stream().map(Edge::getTo).forEach(e -> {
				int toIndex = fromNode(graph, e);
				dftGraph.addEdge(currenIndex, toIndex);
			});

		}

		return dftGraph.getAllPaths(fromNode(graph, nodeFrom), fromNode(graph, nodeTo)).stream()
				.map(e -> e.stream().map(a -> toNode(graph, a)).collect(Collectors.toList())).distinct()
				.map(e -> new Pair<>(calculateRouteCost(travelWeigthMatrix, edgeMatrix, e), new Pair<>(e, toEdges(e))))
				.collect(Collectors.toList());
	}

	private double calculateRouteCost(double[][] travelWeigthMatrix, Edge[][] edgeMatrix, List<Node> route) {
		double result = 0;
		for (int i = 0; i < route.size() - 1; i++) {
			Node from = route.get(i);
			Node to = route.get(i + 1);
			Edge edge = from.getOutgoingEdges().stream().filter(e -> e.getTo().equals(to)).findFirst().orElseThrow();
			Pair<Integer, Integer> indexes = find(edge, edgeMatrix);
			result += travelWeigthMatrix[indexes.getLeft()][indexes.getRigth()];
		}

		return result;
	}

	private int fromNode(Graph graph, Node node) {
		return graph.getNodes().indexOf(node);
	}

	private Node toNode(Graph graph, int index) {
		return graph.getNodes().get(index);
	}

}
