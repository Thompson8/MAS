package hu.mas.core.mas.intention.simple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.path.PathFinder;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleIntentionMas extends AbstractMas {

	protected final Intentions intentions;

	public SimpleIntentionMas(MasGraph graph, SumoTraciConnection connection, PathFinder pathFinder) {
		super(graph, connection, pathFinder);
		this.intentions = new Intentions();
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) throws Exception {
		return edge.getWeigth();
	}

	@Override
	protected void registerRouteOperations(Vehicle vehicle, Route route) {
		route.getTravelTimeForEdges().entrySet().stream().forEach(e -> intentions.add(e.getKey(),
				new Intention(vehicle, e.getValue().getLeft(), e.getValue().getRigth())));
	}

	@Override
	protected Map<Edge, Pair<Double, Double>> calculateTravelTimeForEdges(List<Edge> edges, Vehicle vehicle,
			double currentTime) {
		Map<Edge, Pair<Double, Double>> result = new HashMap<>();
		double startTime = currentTime;
		for (Edge edge : edges) {
			double baseTravelTime = calculateEdgeTravelTime(edge, startTime);
			double vehicleTravelTime = vehicle.calculateTravelTime(edge);

			double finishTime = startTime + (baseTravelTime > vehicleTravelTime ? baseTravelTime : vehicleTravelTime);
			Pair<Double, Double> calculatedTravelTime = new Pair<>(startTime, finishTime);
			startTime = finishTime;
			result.put(edge, calculatedTravelTime);
		}

		return result;
	}

	protected double calculateEdgeTravelTime(Edge edge, double time) {
		double edgeAdaptedAvgTravelTime = edge.calculateBaseTravelTime();
		Optional<Double> lastVehicleFinishTime;
		Optional<List<Intention>> intentionsForEdge = intentions.findIntentions(edge);
		if (intentionsForEdge.isPresent()) {
			lastVehicleFinishTime = intentionsForEdge.get().stream()
					.filter(e -> e.getStart() <= time && e.getFinish() >= time).map(e -> e.getFinish() - time)
					.max((a, b) -> a.compareTo(b));
		} else {
			lastVehicleFinishTime = Optional.empty();
		}

		if (lastVehicleFinishTime.isPresent()) {
			double vehiclesTravelTime = lastVehicleFinishTime.get();
			return vehiclesTravelTime > edgeAdaptedAvgTravelTime ? vehiclesTravelTime : edgeAdaptedAvgTravelTime;
		} else {
			return edgeAdaptedAvgTravelTime;
		}
	}

}
