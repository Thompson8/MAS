package hu.mas.core.mas.nointention.sumo.delegate;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.exception.MasException;
import hu.mas.core.mas.model.exception.MasRuntimeException;
import hu.mas.core.mas.model.graph.AbstractEdge;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.nointention.AbstractNoIntentionMas;
import hu.mas.core.mas.pathfinder.AbstractPathFinder;
import hu.mas.core.mas.util.CalculatorUtil;
import it.polito.appeal.traci.SumoTraciConnection;

public class SumoDelegatedNoIntentionMas extends AbstractNoIntentionMas {

	public SumoDelegatedNoIntentionMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) throws MasException {
		try {
			return (Double) connection.do_job_get(de.tudresden.sumo.cmd.Edge.getTraveltime(edge.getId()));
		} catch (Exception e) {
			throw new MasException(e);
		}
	}

	@Override
	protected double calculateTravelTimeFromRoadCharacteristics(AbstractEdge edge, Vehicle vehicle, double time) {
		try {
			return Math.max((Double) connection.do_job_get(de.tudresden.sumo.cmd.Edge.getTraveltime(edge.getId())),
					CalculatorUtil.calculateTravelTimeOnEdge(edge, vehicle));
		} catch (Exception e) {
			throw new MasRuntimeException(e);
		}
	}

}
