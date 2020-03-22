package hu.mas.core.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.mas.model.Node;
import hu.mas.core.util.Pair;

public interface PathFinder {

	public List<Pair<Double, Route>> getShortestPaths(Graph graph, Node nodeFrom, Node nodeTo,
			double[][] travelWeigthMatrix, Edge[][] edgeMatrix, Vehicle vehicle, double currentTime);

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

	default Map<Edge, Pair<Double, Double>> calculateTravelTimeForEdges(List<Edge> routes, Vehicle vehicle,
			double currentTime, double[][] travelWeigthMatrix, Edge[][] edgeMatrix) {
		Map<Edge, Pair<Double, Double>> result = new HashMap<>();

		double startTime = currentTime;
		for (Edge edge : routes) {
			Pair<Integer, Integer> index = find(edge, edgeMatrix);
			double baseTravelTime = travelWeigthMatrix[index.getLeft()][index.getRigth()];
			double vehicleTravelTime = vehicle.calculateTravelTime(edge);

			double finishTime = startTime + (baseTravelTime > vehicleTravelTime ? baseTravelTime : vehicleTravelTime);
			Pair<Double, Double> calculatedTravelTime = new Pair<>(startTime, finishTime);
			startTime = finishTime;
			result.put(edge, calculatedTravelTime);
		}

		return result;
	}

}
