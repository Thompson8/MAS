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

	protected static final double DEFAULT_DECAY_CONSTANT = 0.001;

	protected final double decayConstant;

	public AbstractSimplePredictionIntentionMas(MasGraph graph, SumoTraciConnection connection,
			AbstractPathFinder pathFinder) {
		this(graph, connection, pathFinder, DEFAULT_DECAY_CONSTANT);
	}

	public AbstractSimplePredictionIntentionMas(MasGraph graph, SumoTraciConnection connection,
			AbstractPathFinder pathFinder, double decayConstant) {
		super(graph, connection, pathFinder);
		this.decayConstant = decayConstant;

	}

	protected abstract double calculateTravelTimeFromRoadCharacteristics(Road road, Vehicle vehicle, double time);

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
			travelTime += roadTraveTime;
		}

		route.setPredictedTravelTime(travelTime);
	}

	// ALG 5
	protected void updatePredictedRoadTravelTimes(double previousTime, double currentTime) {
		// double timeDifference = currentTime - previousTime;
		for (Edge edge : graph.getEdges()) {
			// TODO difference calculations
			double predictedTravelTime = edge.getRoad().getPredictedTravelTime() * (1 - decayConstant);
			double emptyTravelTime = CalculatorUtil.calculateTravelTimeOnEdge(edge);
			edge.getRoad().setPredictedTravelTime(Math.max(predictedTravelTime, emptyTravelTime));
		}
	}

	// ALG 6
	protected void updateTravelTimeCalculationsForAllRoutes() {
		for (Route route : routePredictionHolder.getRoutes()) {
			updateTravelTimeCalculationsForRoute(route);
		}
	}

	protected void updateTravelTimeCalculationsForRoute(Route route) {
		double travelTime = 0;
		for (Road road : route.getRoads()) {
			travelTime += road.getPredictedTravelTime();
		}
		route.setPredictedTravelTime(travelTime);
	}

	@Override
	protected double predictRouteTravelTime(MasRoute route, Vehicle vehicle, double time) {
		Route storedRoute = getRoute(route);
		updateTravelTimeCalculationsForRoute(storedRoute);
		return storedRoute.getPredictedTravelTime();
	}

	@Override
	protected void updatePredictedTravelTimes(double currentTime) {
		routePredictionHolder.getRoutes().forEach(this::updateTravelTimeCalculationsForRoute);
	}

	@Override
	protected double predictRoadTravelTime(Road road, Vehicle vehicle, double time) {
		double travelTimeAlready = getRemaningTravelTime(road, time);
		double travelTime = calculateTravelTimeFromRoadCharacteristics(road, vehicle, time);

		return Math.max(travelTime, travelTimeAlready);
	}

	@Override
	protected void beforeUpdateTravelWeigthMatrix(double previousTime, double currentTime) {
		updatePredictedRoadTravelTimes(previousTime, currentTime);
	}

}
