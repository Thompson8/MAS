package hu.mas.core.mas.nointention.base;

import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.nointention.AbstractNoIntentionMas;
import hu.mas.core.mas.path.AbstractPathFinder;
import hu.mas.core.mas.util.CalculatorUtil;
import it.polito.appeal.traci.SumoTraciConnection;

public class NoIntentionTravelTimeMas extends AbstractNoIntentionMas {

	public NoIntentionTravelTimeMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) {
		return CalculatorUtil.calculateTravelTimeOnEdge(edge);
	}

}
