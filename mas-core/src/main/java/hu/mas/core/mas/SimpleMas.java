package hu.mas.core.mas;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.path.PathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleMas extends AbstractMas {

	public SimpleMas(Graph graph, SumoTraciConnection connection, PathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForTravelWeigthMatrixUpdate(int x, int y, Edge edge, int iteration) throws Exception {
		
		return vehiclesData.getData().entrySet().stream()
				.filter(e -> e.getValue().getStatistics().getFinish() == null)
				.filter(e -> e.getValue().getRoute().getEdges().contains(edge))
				.filter(e -> e.getValue().getCurrentEdge() == null || e.getValue().getRoute().getEdges()
						.indexOf(edge) >= e.getValue().getRoute().getEdges().indexOf(e.getValue().getCurrentEdge()))
				.map(e -> e.getKey().caculateEdgeImpact(edge)).reduce(1.0, (a, b) -> a + b);
	}

}
