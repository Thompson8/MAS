package hu.mas.core.mas.intention.prediction.simple.travel.time;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.intention.model.Road;
import hu.mas.core.mas.intention.prediction.simple.AbstractSimplePredictionIntentionMas;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.pathfinder.AbstractPathFinder;
import hu.mas.core.mas.util.CalculatorUtil;
import it.polito.appeal.traci.SumoTraciConnection;

public class TravelTimeSimplePredictionIntentionMas extends AbstractSimplePredictionIntentionMas {

	public TravelTimeSimplePredictionIntentionMas(MasGraph graph, SumoTraciConnection connection,
			AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double calculateTravelTimeFromRoadCharacteristics(Road road, Vehicle vehicle, double time) {
		return CalculatorUtil.calculateTravelTimeOnEdge(road.getEdge(), vehicle);
	}

}
