package hu.mas.core.mas.intention.simple;

import java.util.List;
import java.util.Optional;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.path.PathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleIntentionMas extends AbstractMas {

	protected final Intentions intentions;

	public SimpleIntentionMas(MasGraph graph, SumoTraciConnection connection, PathFinder pathFinder) {
		super(graph, connection, pathFinder);
		this.intentions = new Intentions();
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) throws Exception {
		double edgeAdaptedAvgTravelTime = edge.calculateAvgTravelTime();
		Optional<Double> lastVehicleFinishTime;
		Optional<List<Intention>> intentionsForEdge = intentions.findIntentions(edge);
		if (intentionsForEdge.isPresent()) {
			lastVehicleFinishTime = intentionsForEdge.get().stream().filter(e -> e.getStart() > currentTime)
					.map(e -> e.getFinish() - e.getStart()).max((a, b) -> a.compareTo(b));
		} else {
			lastVehicleFinishTime = Optional.empty();
		}

		if (lastVehicleFinishTime.isPresent()) {
			double vehiclesTravelTime = lastVehicleFinishTime.get() - currentTime;
			return vehiclesTravelTime > edgeAdaptedAvgTravelTime ? vehiclesTravelTime : edgeAdaptedAvgTravelTime;
		} else {
			return edgeAdaptedAvgTravelTime;
		}
	}

	@Override
	protected void registerRouteOperations(Vehicle vehicle, Route route) {
		route.getTravelTimeForEdges().entrySet().stream().forEach(e -> intentions.add(e.getKey(),
				new Intention(vehicle, e.getValue().getLeft(), e.getValue().getRigth())));
	}

}
