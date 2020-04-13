package hu.mas.core.mas.intention.simple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.intention.AbstractIntentionMas;
import hu.mas.core.mas.intention.Intention;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.path.AbstractPathFinder;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleIntentionMas extends AbstractIntentionMas {

	public SimpleIntentionMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) {
		return edge.getWeigth();
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
