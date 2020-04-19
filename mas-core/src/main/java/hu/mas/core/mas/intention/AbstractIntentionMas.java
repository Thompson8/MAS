package hu.mas.core.mas.intention;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.intention.model.Road;
import hu.mas.core.mas.intention.model.Route;
import hu.mas.core.mas.intention.model.RoutePredictionHolder;
import hu.mas.core.mas.intention.model.RoutePredictionHolderImpl;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.path.AbstractPathFinder;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class AbstractIntentionMas extends AbstractMas {

	protected final RoutePredictionHolder routePredictionHolder;

	public AbstractIntentionMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
		this.routePredictionHolder = new RoutePredictionHolderImpl();
	}

	@Override
	protected void registerRouteOperations(Vehicle vehicle, MasRoute route, double currentTime) {
		propagateIntention(vehicle, getRoute(route), currentTime);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) {
		return edge.getRoad().getPredictedTravelTime();
	}

	// ALG 1
	protected abstract void propagateIntention(Vehicle vehicle, Route route, double currentTime);

	protected double predictRoadTravelTime(Road road, double time) {
		return predictRoadTravelTime(road, null, time);
	}

	// ALG 2
	protected abstract double predictRoadTravelTime(Road road, Vehicle vehicle, double time);

	// ALG3
	protected abstract void updatePredictedTravelTimes(double currentTime);

	protected abstract double predictRouteTravelTime(MasRoute route, Vehicle vehicle, double time);

	@Override
	protected double calculateTravelTime(MasRoute route, Vehicle vehicle, double time) {
		return predictRouteTravelTime(route, vehicle, time);
	}

	@Override
	protected void beforeUpdateTravelWeigthMatrix(double previousTime, double currentTime) {
		updatePredictedTravelTimes(previousTime);
	}

	protected Route getRoute(MasRoute route) {
		Optional<List<Route>> routes = routePredictionHolder.getRoutes(route.getFrom(), route.getTo());
		List<Road> roads = route.getEdges().stream().map(Edge::getRoad).collect(Collectors.toList());

		if (routes.isPresent()) {
			Optional<Route> existingRoute = routes.get().stream().filter(e -> e.getRoads().equals(roads)).findAny();
			if (existingRoute.isPresent()) {
				return existingRoute.get();
			} else {
				Route newRoute = new Route(route.getFrom(), route.getTo(), roads);
				routePredictionHolder.addRoute(route.getFrom(), route.getTo(), newRoute);
				return newRoute;
			}
		} else {
			Route newRoute = new Route(route.getFrom(), route.getTo(), roads);
			routePredictionHolder.addRoute(route.getFrom(), route.getTo(), newRoute);
			return newRoute;
		}
	}

	protected double getRemaningTravelTime(Road road, double time) {
		return getVehiclesOnRoadByIntention(road, time).stream()
				.map(e -> (e.getRigth().getLeft() + e.getRigth().getRigth()) - time).max((a, b) -> a.compareTo(b))
				.orElse(0.0);
	}

	protected List<Pair<Vehicle, Pair<Double, Double>>> getVehiclesOnRoadByIntention(Road road, double time) {
		return road.getArrivalList().stream()
				.filter(e -> e.getRigth().getLeft() < time && (e.getRigth().getLeft() + e.getRigth().getRigth()) > time)
				.collect(Collectors.toList());
	}

}
