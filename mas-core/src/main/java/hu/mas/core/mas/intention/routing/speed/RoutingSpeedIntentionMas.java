package hu.mas.core.mas.intention.routing.speed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.intention.AbstractIntentionMas;
import hu.mas.core.mas.intention.Intention;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.path.AbstractPathFinder;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public class RoutingSpeedIntentionMas extends AbstractIntentionMas {

	public RoutingSpeedIntentionMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) throws Exception {
		List<Vehicle> vehicles = vehiclesCurrentlyOnEdge(edge);
		if (!vehicles.isEmpty()) {
			return Math.max(
					edge.getLength()
							/ vehicles.stream().mapToDouble(e -> e.calculateSpeed(edge)).average().getAsDouble(),
					edge.calculateBaseTravelTime());
		} else {
			return edge.calculateBaseTravelTime();
		}
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
		double edgeAdaptedAvgTravelTime = calculateEdgeTravelTimeBasedOnRoutingSpeed(edge, time);
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

	protected double calculateEdgeTravelTimeBasedOnRoutingSpeed(Edge edge, double time) {
		double baseTravelTime = edge.calculateBaseTravelTime();
		List<Vehicle> vehicles = findVehiclesOnEdge(edge, time);
		if (!vehicles.isEmpty()) {
			return Math.max(
					edge.getLength()
							/ vehicles.stream().mapToDouble(e -> e.calculateSpeed(edge)).average().getAsDouble(),
					baseTravelTime);
		} else {
			return baseTravelTime;
		}
	}

}
