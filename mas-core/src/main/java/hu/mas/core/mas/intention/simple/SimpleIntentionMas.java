package hu.mas.core.mas.intention.simple;

import java.util.Optional;

import hu.mas.core.mas.intention.AbstractIntentionMas;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.path.AbstractPathFinder;
import hu.mas.core.mas.util.CalculatorUtil;
import it.polito.appeal.traci.SumoTraciConnection;

public class SimpleIntentionMas extends AbstractIntentionMas {

	public SimpleIntentionMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) {
		return edge.getWeigth();
	}

	@Override
	protected double calculateEdgeTravelTime(Edge edge, double time) {
		double edgeTravelTimeByRoutingSpeed = CalculatorUtil.calculateTravelTimeOnEdge(edge);
		Optional<Double> lastVehicleFinishTimeOnEdge = getLastVehicleFinishTimeOnEdge(edge, time).map(e -> e - time);

		return lastVehicleFinishTimeOnEdge.isPresent()
				? Math.max(lastVehicleFinishTimeOnEdge.get(), edgeTravelTimeByRoutingSpeed)
				: edgeTravelTimeByRoutingSpeed;
	}

}