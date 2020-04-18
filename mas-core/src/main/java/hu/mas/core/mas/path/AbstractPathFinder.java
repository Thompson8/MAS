package hu.mas.core.mas.path;

import java.util.List;

import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.model.graph.Vertex;
import hu.mas.core.util.Pair;
import hu.mas.core.util.TriFunction;

public interface AbstractPathFinder {

	public default List<Pair<Double, MasRoute>> getShortestPaths(String from, String to, Vehicle vehicle,
			double time, MasGraph masGraph, TriFunction<MasRoute, Vehicle, Double, Double> calculateRouteCost) {
		return getShortestPaths(masGraph.findVertex(from).orElseThrow(), masGraph.findVertex(to).orElseThrow(), vehicle,
				time, masGraph, calculateRouteCost);
	}

	public List<Pair<Double, MasRoute>> getShortestPaths(Vertex from, Vertex to, Vehicle vehicle, double time,
			MasGraph masGraph, TriFunction<MasRoute, Vehicle, Double, Double> calculateRouteCost);

}
