package hu.mas.core.mas.nointention.travel.time;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.calculations.TravelTimeUtil;
import hu.mas.core.mas.model.graph.AbstractEdge;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.nointention.AbstractNoIntentionMas;
import hu.mas.core.mas.pathfinder.AbstractPathFinder;
import hu.mas.core.mas.util.CalculatorUtil;
import it.polito.appeal.traci.SumoTraciConnection;

public class TravelTimeNoIntentionMas extends AbstractNoIntentionMas {

	private final TravelTimeUtil travelTimeUtil;

	public TravelTimeNoIntentionMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder,
			TravelTimeUtil travelTimeUtil) {
		super(graph, connection, pathFinder);
		this.travelTimeUtil = travelTimeUtil;
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) {
		return CalculatorUtil.calculateTravelTimeOnEdge(edge);
	}

	@Override
	protected double calculateTravelTimeFromRoadCharacteristics(AbstractEdge edge, Vehicle vehicle, double time) {
		return travelTimeUtil.calculateRoadTravelTimeByCurrentRoadConditions(edge.getRoad(), vehicle, time);
	}

}
