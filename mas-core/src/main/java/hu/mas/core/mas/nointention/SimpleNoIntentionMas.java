package hu.mas.core.mas.nointention;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.path.PathFinder;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class SimpleNoIntentionMas extends AbstractMas {

	public SimpleNoIntentionMas(MasGraph graph, SumoTraciConnection connection, PathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}
	
	@Override
	protected void registerRouteOperations(Vehicle vehicle, Route route) {
		// Not needed for this Mas implementation
	}
	
	@Override
	protected Map<Edge, Pair<Double, Double>> calculateTravelTimeForEdges(List<Edge> edges, Vehicle vehicle,
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

}
