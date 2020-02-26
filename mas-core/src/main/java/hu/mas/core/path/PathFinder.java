package hu.mas.core.path;

import java.util.ArrayList;
import java.util.List;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;

public interface PathFinder {

	public List<Pair<Double, Pair<List<Node>, List<Edge>>>> getShortestPaths(Graph graph, Node nodeFrom, Node nodeTo,
			double[][] travelWeigthMatrix, Edge[][] edgeMatrix);

	default Pair<Integer, Integer> find(Edge edge, Edge[][] edgeMatrix) {
		for (int i = 0; i < edgeMatrix.length; i++) {
			for (int j = 0; j < edgeMatrix[i].length; j++) {
				Edge e = edgeMatrix[i][j];
				if (e != null && e.equals(edge)) {
					return new Pair<>(i, j);
				}
			}
		}
		return null;
	}
	
	default List<Edge> toEdges(List<Node> nodes) {
		List<Edge> result = new ArrayList<>();
		for (int i = 0; i < nodes.size() - 1; i++) {
			Node from = nodes.get(i);
			Node to = nodes.get(i + 1);
			result.add(from.getOutgoingEdges().stream().filter(e -> e.getTo().equals(to)).findFirst().orElseThrow());
		}
		return result;
	}

}
