package hu.mas.core.mas.intention;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.path.AbstractPathFinder;
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

	protected List<Vehicle> findVehiclesOnEdge(Edge edge, double time) {
		Optional<List<Intention>> intentionsForEdge = intentions.findIntentions(edge);
		if (intentionsForEdge.isPresent()) {
			return intentionsForEdge.get().stream().filter(e -> e.getStart() < time && e.getFinish() > time)
					.map(Intention::getVehicle).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	protected List<Intention> findIntentionsOnEdge(Edge edge, double time) {
		Optional<List<Intention>> intentionsForEdge = intentions.findIntentions(edge);
		if (intentionsForEdge.isPresent()) {
			return intentionsForEdge.get().stream().filter(e -> e.getStart() < time && e.getFinish() > time)
					.collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

}
