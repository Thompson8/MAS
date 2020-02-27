package hu.mas.core.mas;

import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.path.PathFinder;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleMas extends AbstractMas {

	public SimpleMas(Graph graph, SumoTraciConnection connection, PathFinder pathFinder,
			EdgeWeightCalculatorType edgeWeightCalculator) {
		super(graph, connection, pathFinder, edgeWeightCalculator);
	}

	@Override
	protected double getValueForTravelWeigthMatrixUpdate(int x, int y, Edge edge, int iteration) throws Exception {
		final double edgeWeigth = getEdgeWeigth(edge);

		return vehiclesData.getData().entrySet().stream().filter(e -> e.getValue().getStatistics().getFinish() == null)
				.filter(e -> e.getValue().getRoute().getEdges().contains(edge))
				.filter(e -> e.getValue().getCurrentEdge() == null || e.getValue().getRoute().getEdges()
						.indexOf(edge) >= e.getValue().getRoute().getEdges().indexOf(e.getValue().getCurrentEdge()))
				.map(e -> e.getKey().caculateEdgeImpact(edge, edgeWeigth)).reduce(edgeWeigth, (a, b) -> a + b);
	}

}
