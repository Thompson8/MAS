package hu.mas.core.mas.intention.prediction.detailed;

import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.intention.AbstractIntentionMas;
import hu.mas.core.mas.intention.model.Road;
import hu.mas.core.mas.intention.model.Route;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.path.AbstractPathFinder;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class AbstractDetailedPredictionIntentionMas extends AbstractIntentionMas {

	public AbstractDetailedPredictionIntentionMas(MasGraph graph, SumoTraciConnection connection,
			AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	// ALG 1
	@Override
	protected void propagateIntention(Vehicle vehicle, Route route, double currentTime) {
		double travelTime = 0;
		double roadTraveTime = 0;
		double time = currentTime;
		for (Road road : route.getRoads()) {
			roadTraveTime = predictRoadTravelTime(road, vehicle, time + travelTime);
			road.getArrivalList().add(new Pair<>(vehicle, new Pair<>(time + travelTime, roadTraveTime)));
			travelTime = travelTime + roadTraveTime;
		}
	}

	// ALG3
	@Override
	protected void updatePredictedTravelTimes(double currentTime) {
		routePredictionHolder.getRoutes().forEach(route -> {
			double travelTime = 0;
			double time = currentTime;
			for (Road road : route.getRoads()) {
				travelTime = travelTime + predictRoadTravelTime(road, time + travelTime);
			}

			route.setPredictedTravelTime(travelTime);
		});
	}

	@Override
	protected double predictRouteTravelTime(MasRoute route, Vehicle vehicle, double currentTime) {
		Route storedRoute = getRoute(route);
		double travelTime = 0;
		double time = currentTime;
		for (Road road : storedRoute.getRoads()) {
			travelTime = travelTime + predictRoadTravelTime(road, vehicle, time + travelTime);
		}

		return travelTime;
	}

	@Override
	protected double predictRoadTravelTime(Road road, Vehicle vehicle, double time) {
		double travelTimeAlready = getRemaningTravelTime(road, time);
		double travelTime = calculateTravelTimeFromRoadCharacteristics(road, vehicle, time);

		return Math.max(travelTime, travelTimeAlready);
	}

	protected abstract double calculateTravelTimeFromRoadCharacteristics(Road road, Vehicle vehicle, double time);

}
