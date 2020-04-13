package hu.mas.core.mas.intention;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hu.mas.core.agent.model.route.Route;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.path.AbstractPathFinder;
import hu.mas.core.mas.util.CalculatorUtil;
import hu.mas.core.util.Pair;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class AbstractIntentionMas extends AbstractMas {

	protected final Intentions intentions;

	public AbstractIntentionMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
		this.intentions = new Intentions();
	}

	@Override
	protected void registerRouteOperations(Vehicle vehicle, Route route) {
		route.getTravelTimeForEdges().entrySet().stream().forEach(e -> intentions.add(e.getKey(),
				new Intention(vehicle, e.getValue().getLeft(), e.getValue().getRigth())));
	}

	protected Map<Edge, Pair<Double, Double>> calculateTravelTimeForEdges(List<Edge> edges, Vehicle vehicle,
			double currentTime) {
		Map<Edge, Pair<Double, Double>> result = new HashMap<>();
		double startTime = currentTime;
		for (Edge edge : edges) {
			double finishTime = startTime + (Math.max(calculateEdgeTravelTime(edge, startTime),
					CalculatorUtil.calculateTravelTimeOnEdge(edge, vehicle)));
			Pair<Double, Double> calculatedTravelTime = new Pair<>(startTime, finishTime);
			startTime = finishTime;
			result.put(edge, calculatedTravelTime);
		}

		return result;
	}

	protected abstract double calculateEdgeTravelTime(Edge edge, double time);

	protected List<Vehicle> getVehiclesOnEdgeByIntention(Edge edge, double time) {
		return getIntentionsStreamOnEdge(edge, time).map(e -> e.map(Intention::getVehicle).collect(Collectors.toList()))
				.orElse(Collections.emptyList());
	}

	protected List<Intention> getIntentionsOnEdge(Edge edge, double time) {
		return getIntentionsStreamOnEdge(edge, time).map(e -> e.collect(Collectors.toList()))
				.orElse(Collections.emptyList());
	}

	protected Optional<Stream<Intention>> getIntentionsStreamOnEdge(Edge edge, double time) {
		return intentions.findIntentions(edge)
				.map(e -> e.stream().filter(a -> a.getStart() < time && a.getFinish() > time));

	}

	protected Optional<Double> getLastVehicleFinishTimeOnEdge(Edge edge, double time) {
		Optional<Stream<Intention>> intentionsOnEdge = getIntentionsStreamOnEdge(edge, time);
		return intentionsOnEdge.isPresent()
				? intentionsOnEdge.get().map(Intention::getFinish).max((a, b) -> a.compareTo(b))
				: Optional.empty();
	}

}
