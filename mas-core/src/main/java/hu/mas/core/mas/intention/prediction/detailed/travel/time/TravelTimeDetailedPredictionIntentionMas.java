package hu.mas.core.mas.intention.prediction.detailed.travel.time;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.calculations.TravelTimeUtil;
import hu.mas.core.mas.intention.prediction.detailed.AbstractDetailedPredictionIntentionMas;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.model.graph.Road;
import hu.mas.core.mas.pathfinder.AbstractPathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public class TravelTimeDetailedPredictionIntentionMas extends AbstractDetailedPredictionIntentionMas {

	private final TravelTimeUtil travelTimeUtil;

	public TravelTimeDetailedPredictionIntentionMas(MasGraph graph, SumoTraciConnection connection,
			AbstractPathFinder pathFinder, TravelTimeUtil travelTimeUtil) {
		super(graph, connection, pathFinder);
		this.travelTimeUtil = travelTimeUtil;
	}

	@Override
	protected double calculateTravelTimeFromRoadCharacteristics(Road road, Vehicle vehicle, double time) {
		return travelTimeUtil.calculateRoadTravelTimeByIntention(road, vehicle, time);
	}

}