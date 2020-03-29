package hu.mas.core.mas.nointention.avg;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.path.PathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public class AvgMas extends AbstractMas {

	public AvgMas(MasGraph graph, SumoTraciConnection connection, PathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) throws Exception {
		return edge.calculateAvgTravelTime();
	}

	@Override
	protected void registerRouteOperations(Vehicle vehicle, Route route) {
		// No operation to do here
	}

}
