package hu.mas.core.mas.intention.prediction.simple.routing.speed;

import java.util.List;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.calculations.RoutingSpeedIntentionUtil;
import hu.mas.core.mas.intention.prediction.simple.AbstractSimplePredictionIntentionMas;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.model.graph.Road;
import hu.mas.core.mas.pathfinder.AbstractPathFinder;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public class RoutingSpeedSimplePredictionIntentionMas extends AbstractSimplePredictionIntentionMas {

	private RoutingSpeedIntentionUtil routingSpeedIntentionUtil;

	public RoutingSpeedSimplePredictionIntentionMas(MasGraph graph, SumoTraciConnection connection,
			AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
		this.routingSpeedIntentionUtil = new RoutingSpeedIntentionUtil();
	}

	public RoutingSpeedSimplePredictionIntentionMas(MasGraph graph, SumoTraciConnection connection,
			AbstractPathFinder pathFinder, double alpha) {
		super(graph, connection, pathFinder);
		this.routingSpeedIntentionUtil = new RoutingSpeedIntentionUtil(alpha);
	}

	@Override
	protected double calculateTravelTimeFromRoadCharacteristics(Road road, Vehicle vehicle, double time) {
		List<Pair<Vehicle, Pair<Double, Double>>> onRoad = getVehiclesOnRoadByIntention(road, time);
		return routingSpeedIntentionUtil.estimaTravelTimeUsingRoutingSpeed(road, vehicle, onRoad, time);
	}

}
