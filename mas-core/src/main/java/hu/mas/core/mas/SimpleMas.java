package hu.mas.core.mas;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.model.Edge;
import hu.mas.core.mas.model.Graph;
import hu.mas.core.path.PathFinder;
import hu.mas.core.simulation.SimulationEdgeImpactCalculatorType;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleMas extends AbstractMas {

	public SimpleMas(Graph graph, SumoTraciConnection connection, PathFinder pathFinder,
			SimulationEdgeImpactCalculatorType edgeWeightCalculator) {
		super(graph, connection, pathFinder, edgeWeightCalculator);
	}

	@Override
	protected double getValueForTravelWeigthMatrixUpdate(int x, int y, Edge edge, int iteration) throws Exception {
		double result = 0.0;
		final double edgeWeigth = getEdgeWeigth(edge);
		switch (edgeWeightCalculator) {
		case CONSTANT:
			result = vehiclesData.getData().entrySet().stream()
					.filter(e -> e.getValue().getStatistics().getFinish() == null)
					.filter(e -> e.getValue().getRoute().getEdges().contains(edge))
					.filter(e -> e.getValue().getCurrentEdge() == null || e.getValue().getRoute().getEdges()
							.indexOf(edge) >= e.getValue().getRoute().getEdges().indexOf(e.getValue().getCurrentEdge()))
					.map(e -> e.getKey().caculateEdgeImpact(edge)).reduce(edgeWeigth, (a, b) -> a + b);
			break;
		case TRAVEL_TIME:
			List<Map.Entry<Vehicle, VehicleData>> onEdge = vehiclesData.getData().entrySet().stream()
					.filter(e -> e.getValue().getStatistics().getFinish() == null)
					.filter(e -> e.getValue().getRoute().getEdges().contains(edge))
					.filter(e -> e.getValue().getCurrentEdge() == null || e.getValue().getRoute().getEdges()
							.indexOf(edge) >= e.getValue().getRoute().getEdges().indexOf(e.getValue().getCurrentEdge()))
					.collect(Collectors.toList());
			Optional<Double> maxTravelTime = onEdge.stream().map(e -> e.getKey().caculateEdgeImpact(edge))
					.max((a, b) -> a.compareTo(b));
			if (maxTravelTime.isPresent()) {
				double vehiclesOnEdgeSumLength = onEdge.stream().map(e -> e.getKey().getLength()).reduce(0.0,
						(a, b) -> a + b);
				if (vehiclesOnEdgeSumLength > edge.getLength()) {
					result = maxTravelTime.get() * vehiclesOnEdgeSumLength / edge.getLength();
				} else {
					result = maxTravelTime.get();
				}
			} else {
				result = edgeWeigth;
			}
			break;
		default:
			break;
		}
		return result;
	}

}
