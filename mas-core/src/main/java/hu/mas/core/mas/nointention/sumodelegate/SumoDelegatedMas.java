package hu.mas.core.mas.nointention.sumodelegate;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.mas.nointention.SimpleNoIntentionMas;
import hu.mas.core.path.AbstractPathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public class SumoDelegatedMas extends SimpleNoIntentionMas {

	public SumoDelegatedMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) throws Exception {
		return (Double) connection.do_job_get(de.tudresden.sumo.cmd.Edge.getTraveltime(edge.getId()));
	}

}
