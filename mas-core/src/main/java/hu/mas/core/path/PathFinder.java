package hu.mas.core.path;

import java.util.List;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;

public interface PathFinder {

	public List<Pair<Double, List<Node>>> getShortestPaths(Graph graph, Node nodeFrom, Node nodeTo,
			double[][] travelWeigthMatrix, Edge[][] edgeMatrix);

	public default Pair<Integer, Integer> find(Edge edge, Edge[][] edgeMatrix) {
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

}
