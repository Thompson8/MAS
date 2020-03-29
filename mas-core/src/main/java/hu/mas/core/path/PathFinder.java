package hu.mas.core.path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.mas.model.Vertex;
import hu.mas.core.util.Pair;

public interface PathFinder {

	public default List<Pair<Double, Route>> getShortestPaths(String from, String to, Vehicle vehicle,
			double currentTime, MasGraph masGraph) {
		return getShortestPaths(masGraph.findVertex(from).orElseThrow(), masGraph.findVertex(to).orElseThrow(), vehicle,
				currentTime, masGraph);
	}

	public List<Pair<Double, Route>> getShortestPaths(Vertex from, Vertex to, Vehicle vehicle, double currentTime,
			MasGraph masGraph);

	default Map<Edge, Pair<Double, Double>> calculateTravelTimeForEdges(List<Edge> edges, Vehicle vehicle,
			double currentTime) {
		Map<Edge, Pair<Double, Double>> result = new HashMap<>();
		double startTime = currentTime;
		for (Edge edge : edges) {
			double baseTravelTime = edge.getWeigth();
			double vehicleTravelTime = vehicle.calculateTravelTime(edge);

			double finishTime = startTime + (baseTravelTime > vehicleTravelTime ? baseTravelTime : vehicleTravelTime);
			Pair<Double, Double> calculatedTravelTime = new Pair<>(startTime, finishTime);
			startTime = finishTime;
			result.put(edge, calculatedTravelTime);
		}
		
		return result;
	}

	default double calculateCost(Route route) {
		return route.getTravelTimeForEdges().values().stream().map(Pair::getRigth).max((a, b) -> a.compareTo(b))
				.orElseThrow();
	}

}
