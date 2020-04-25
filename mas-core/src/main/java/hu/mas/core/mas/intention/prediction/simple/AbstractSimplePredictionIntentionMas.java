package hu.mas.core.mas.intention.prediction.simple;

import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.intention.AbstractIntentionMas;
import hu.mas.core.mas.intention.model.Route;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.model.graph.Road;
import hu.mas.core.mas.pathfinder.AbstractPathFinder;
import hu.mas.core.mas.util.CalculatorUtil;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class AbstractSimplePredictionIntentionMas extends AbstractIntentionMas {

	public AbstractSimplePredictionIntentionMas(MasGraph graph, SumoTraciConnection connection,
			AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	// ALG 4
	@Override
	protected void propagateIntention(Vehicle vehicle, Route route, double currentTime) {
		double travelTime = 0;
		double roadTraveTime = 0;
		double time = currentTime;
		for (Road road : route.getRoads()) {
			roadTraveTime = predictRoadTravelTime(road, vehicle, time + travelTime);
			road.setPredictedTravelTime(roadTraveTime);
			road.getArrivalList().add(new Pair<>(vehicle, new Pair<>(time + travelTime, roadTraveTime)));
			travelTime = travelTime + roadTraveTime;
		}

		route.setPredictedTravelTime(travelTime);
	}

	// ALG 6
	@Override
	protected void updatePredictedTravelTimes(double currentTime) {
		routePredictionHolder.getRoutes().forEach(route -> {
			double travelTime = 0;
			for (Road road : route.getRoads()) {
				travelTime = travelTime + road.getPredictedTravelTime();
			}

			route.setPredictedTravelTime(travelTime);
		});
	}

	@Override
	protected double predictRouteTravelTime(MasRoute route, Vehicle vehicle, double time) {
		Route storedRoute = getRoute(route);
		double travelTime = 0;
		for (Road road : storedRoute.getRoads()) {
			travelTime = travelTime + Math.max(road.getPredictedTravelTime(),
					CalculatorUtil.calculateTravelTimeOnEdge(road.getEdge(), vehicle));
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

	@Override
	protected void beforeUpdateTravelWeigthMatrix(double previousTime, double currentTime) {
		updatePredictedRoadTravelTimes(previousTime, currentTime);
		updatePredictedTravelTimes(currentTime);
	}

	protected void updatePredictedRoadTravelTimes(double previousTime, double currentTime) {
		double timeDifference = currentTime - previousTime;
		for (Edge edge : graph.getEdges()) {
			double predictedTravelTime = edge.getRoad().getPredictedTravelTime() - timeDifference;
			double emptyTravelTime = CalculatorUtil.calculateTravelTimeOnEdge(edge);
			edge.getRoad().setPredictedTravelTime(Math.max(predictedTravelTime, emptyTravelTime));
		}
	}

}
