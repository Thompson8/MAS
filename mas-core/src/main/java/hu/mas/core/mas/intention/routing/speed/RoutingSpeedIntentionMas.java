package hu.mas.core.mas.intention.routing.speed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.mas.core.mas.intention.AbstractIntentionMas;
import hu.mas.core.mas.intention.Intention;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.model.graph.MasGraph;
import hu.mas.core.mas.path.AbstractPathFinder;
import hu.mas.core.mas.util.CalculatorUtil;
import it.polito.appeal.traci.SumoTraciConnection;

public class RoutingSpeedIntentionMas extends AbstractIntentionMas {

	private static final double ALPHA = 0.1;

	public RoutingSpeedIntentionMas(MasGraph graph, SumoTraciConnection connection, AbstractPathFinder pathFinder) {
		super(graph, connection, pathFinder);
	}

	@Override
	protected double getValueForWeigthUpdate(Edge edge, double currentTime) {
		return CalculatorUtil.calculateRoutingSpeed(edge, getVehiclesSpeedsCurrentlyOnEdge(edge));
	}

	@Override
	protected double calculateEdgeTravelTime(Edge edge, double time) {
		double edgeTravelTimeByRoutingSpeed = calculateEdgeTravelTimeBasedOnRoutingSpeed(edge, time);
		Optional<Double> lastVehicleFinishTimeOnEdge = getLastVehicleFinishTimeOnEdge(edge, time).map(e -> e - time);

		return lastVehicleFinishTimeOnEdge.isPresent()
				? Math.max(lastVehicleFinishTimeOnEdge.get(), edgeTravelTimeByRoutingSpeed)
				: edgeTravelTimeByRoutingSpeed;
	}

	protected double calculateEdgeTravelTimeBasedOnRoutingSpeed(Edge edge, double time) {
		List<Intention> intentionsOnEdge = getIntentionsOnEdge(edge, time);
		if (!intentionsOnEdge.isEmpty()) {
			List<Intention> sortedIntentions = intentionsOnEdge.stream()
					.sorted(Comparator.comparingDouble(Intention::getFinish)).collect(Collectors.toList());
			List<Double> estimatedSpeeds = new ArrayList<>();
			Iterator<Intention> iterator = sortedIntentions.iterator();
			Intention current = iterator.next();
			Intention previous = null;
			double previousEstimation = CalculatorUtil.calculateSpeedOnEdge(edge, current.getVehicle());
			estimatedSpeeds.add(previousEstimation);

			while (iterator.hasNext()) {
				previous = current;
				current = iterator.next();
				previousEstimation = estimateSpeedForVehicle(edge, current, previous, previousEstimation, time);
				estimatedSpeeds.add(previousEstimation);
			}

			return CalculatorUtil.calculateRoutingSpeed(edge, estimatedSpeeds);
		} else {
			return CalculatorUtil.calculateRoutingSpeed(edge, Collections.emptyList());
		}
	}

	protected double estimateSpeedForVehicle(Edge edge, Intention currentVehicleIntention,
			Intention beforeVehicleIntention, double previousEstimation, double time) {
		double currentVehicleBaseSpeed = CalculatorUtil.calculateSpeedOnEdge(edge,
				currentVehicleIntention.getVehicle());
		if (currentVehicleBaseSpeed < previousEstimation) {
			return currentVehicleBaseSpeed;
		} else {
			double beforeFinishTimeDiff = beforeVehicleIntention.getFinish() - time;
			double currentFinishTimeDiff = currentVehicleIntention.getFinish() - time;
			double finishTimeDiff = Math.abs(currentFinishTimeDiff - beforeFinishTimeDiff) / currentFinishTimeDiff;

			return currentVehicleBaseSpeed * finishTimeDiff * (1 - ALPHA)
					+ previousEstimation * (1 - finishTimeDiff) * ALPHA;
		}
	}

}
