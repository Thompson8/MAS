package hu.mas.core.mas.nointention.sumodelegate;

import hu.mas.core.mas.model.exception.MasException;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.nointention.AbstractNoIntentionMas;
import hu.mas.core.mas.path.AbstractPathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public class SumoDelegatedMas extends AbstractNoIntentionMas {

	public SumoDelegatedMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
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

}
