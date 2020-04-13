package hu.mas.core.path;

import java.util.List;
import java.util.Map;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.mas.model.Vertex;
import hu.mas.core.util.Pair;
import hu.mas.core.util.TriFunction;

public interface AbstractPathFinder {

	public default List<Pair<Double, Route>> getShortestPaths(String from, String to, Vehicle vehicle,
			double currentTime, MasGraph masGraph, TriFunction<List<Edge>, Vehicle, Double, Map<Edge, Pair<Double, Double>>> calculateTravelTimeForEdges) {
		return getShortestPaths(masGraph.findVertex(from).orElseThrow(), masGraph.findVertex(to).orElseThrow(), vehicle,
				currentTime, masGraph, calculateTravelTimeForEdges);
	}

	public List<Pair<Double, Route>> getShortestPaths(Vertex from, Vertex to, Vehicle vehicle, double currentTime,
			MasGraph masGraph, TriFunction<List<Edge>, Vehicle, Double, Map<Edge, Pair<Double, Double>>> calculateTravelTimeForEdges);

	default double calculateCost(Route route) {
		return route.getTravelTimeForEdges().values().stream().map(Pair::getRigth).max((a, b) -> a.compareTo(b))
				.orElseThrow();
	}

}
