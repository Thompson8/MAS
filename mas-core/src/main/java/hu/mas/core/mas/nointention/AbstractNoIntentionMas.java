package hu.mas.core.mas.nointention;

import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.graph.AbstractEdge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.pathfinder.AbstractPathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public abstract class AbstractNoIntentionMas extends AbstractMas {

	public AbstractNoIntentionMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected void registerRouteOperations(Vehicle vehicle, MasRoute route, double currentTime) {
		// Not needed for this Mas implementation
	}

	@Override
	protected double calculateTravelTime(MasRoute route, Vehicle vehicle, double time) {
		double travelTime = 0;
		for (AbstractEdge edge : getEdgesWihtInternalEdgesIncluded(route)) {
			travelTime += calculateTravelTimeFromRoadCharacteristics(edge, vehicle, time);
		}

		return travelTime;
	}

	protected abstract double calculateTravelTimeFromRoadCharacteristics(AbstractEdge edge, Vehicle vehicle,
			double time);

}
