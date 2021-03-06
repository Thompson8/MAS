package hu.mas.core.mas.calculations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.AbstractEdge;
import hu.mas.core.mas.model.graph.Road;
import hu.mas.core.mas.util.CalculatorUtil;
import hu.mas.core.util.Pair;

public class RoutingSpeedIntentionUtil {

	private static final double DEFAULT_ALPHA = 0.1;

	private final double alpha;

	public RoutingSpeedIntentionUtil() {
		this(DEFAULT_ALPHA);
	}

	public RoutingSpeedIntentionUtil(double alpha) {
		this.alpha = alpha;
	}

	public double estimaTravelTimeUsingRoutingSpeed(Road road, Vehicle vehicle,
			List<Pair<Vehicle, Pair<Double, Double>>> onRoad, double time) {
		AbstractEdge edge = road.getEdge();
		if (!onRoad.isEmpty()) {
			List<Pair<Vehicle, Pair<Double, Double>>> sortedIntentions = onRoad.stream()
					.sorted(Comparator.comparingDouble(e -> e.getRigth().getRigth())).collect(Collectors.toList());

			List<Double> estimatedSpeeds = new ArrayList<>();
			Iterator<Pair<Vehicle, Pair<Double, Double>>> iterator = sortedIntentions.iterator();
			Pair<Vehicle, Pair<Double, Double>> current = iterator.next();
			Pair<Vehicle, Pair<Double, Double>> previous = null;
			double previousEstimation = CalculatorUtil.calculateSpeedOnEdge(edge, current.getLeft());
			estimatedSpeeds.add(previousEstimation);

			while (iterator.hasNext()) {
				previous = current;
				current = iterator.next();
				previousEstimation = estimateSpeedForVehicle(edge, current, previous, previousEstimation, time);
				estimatedSpeeds.add(previousEstimation);
			}

			return Math.max(CalculatorUtil.calculateTravelTimeOnEdge(edge, vehicle),
					edge.getLength() / CalculatorUtil.calculateRoutingSpeed(estimatedSpeeds));
		} else {
			return CalculatorUtil.calculateTravelTimeOnEdge(edge, vehicle);
		}
	}

	private double estimateSpeedForVehicle(AbstractEdge edge,
			Pair<Vehicle, Pair<Double, Double>> currentVehicleIntention,
			Pair<Vehicle, Pair<Double, Double>> beforeVehicleIntention, double previousEstimation, double time) {
		double currentVehicleBaseSpeed = CalculatorUtil.calculateSpeedOnEdge(edge, currentVehicleIntention.getLeft());
		if (currentVehicleBaseSpeed < previousEstimation) {
			return currentVehicleBaseSpeed;
		} else {
			double beforeFinishTimeDiff = beforeVehicleIntention.getRigth().getRigth() - time;
			double currentFinishTimeDiff = currentVehicleIntention.getRigth().getRigth() - time;
			double finishTimeDiff = Math.abs(currentFinishTimeDiff - beforeFinishTimeDiff) / currentFinishTimeDiff;

			double estimation = currentVehicleBaseSpeed * (1 - finishTimeDiff) * (1 - alpha)
					+ previousEstimation * finishTimeDiff * alpha;

			return Math.max(estimation, previousEstimation);
		}
	}

}
