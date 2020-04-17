package hu.mas.core.mas.intention.prediction.detailed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.intention.AbstractIntentionMas;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.path.AbstractPathFinder;
import hu.mas.core.mas.util.CalculatorUtil;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class AbstractDetailedPredictionIntentionMas extends AbstractIntentionMas {

	public AbstractDetailedPredictionIntentionMas(MasGraph graph, SumoTraciConnection connection,
			AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected Map<Edge, Pair<Double, Double>> calculateTravelTime(List<Edge> edges, Vehicle vehicle,
			double currentTime) {
		Map<Edge, Pair<Double, Double>> result = new HashMap<>();
		double travelTime = 0;
		double roadTravelTime = 0;
		double time = currentTime;

		for (Edge edge : edges) {
			roadTravelTime = predictRoadTravelTime(edge, time, vehicle);
			result.put(edge, new Pair<>(time + travelTime, time + travelTime + roadTravelTime));
			travelTime = travelTime + roadTravelTime;
		}

		return result;
	}

	protected double predictRoadTravelTime(Edge edge, double time, Vehicle vehicle) {
		return Math.max(calculateEdgeTravelTime(edge, time), CalculatorUtil.calculateTravelTimeOnEdge(edge, vehicle));
	}

}
