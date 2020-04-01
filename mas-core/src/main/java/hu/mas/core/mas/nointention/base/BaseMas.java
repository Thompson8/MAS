package hu.mas.core.mas.nointention.base;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.MasGraph;
import hu.mas.core.mas.nointention.SimpleNoIntentionMas;
import hu.mas.core.path.PathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public class BaseMas extends SimpleNoIntentionMas {

	public BaseMas(MasGraph graph, SumoTraciConnection connection, PathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) throws Exception {
		return edge.calculateBaseTravelTime();
	}

}
