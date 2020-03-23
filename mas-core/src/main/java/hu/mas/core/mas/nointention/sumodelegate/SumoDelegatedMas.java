package hu.mas.core.mas.nointention.sumodelegate;

import hu.mas.core.agent.Route;
import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.AbstractMas;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.path.PathFinder;
import hu.mas.core.simulation.SimulationEdgeImpactCalculatorType;
import it.polito.appeal.traci.SumoTraciConnection;

public class SumoDelegatedMas extends AbstractMas  {

	public SumoDelegatedMas(Graph graph, SumoTraciConnection connection, PathFinder pathFinder,
			SimulationEdgeImpactCalculatorType edgeWeightCalculator) {
		super(graph, connection, pathFinder, edgeWeightCalculator);
	}

	@Override
	protected double getValueForTravelWeigthMatrixUpdate(int x, int y, Edge edge, double currentTime) throws Exception {
		return (Double) connection.do_job_get(de.tudresden.sumo.cmd.Edge.getTraveltime(edge.getId()));
	}

	@Override
	protected void registerRouteOperations(Vehicle vehicle, Route route) {
		//Not needed for this Mas implementation
	}

}
