package hu.mas.core.mas.util;

import java.util.List;
import java.util.stream.DoubleStream;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.AbstractEdge;

public class CalculatorUtil {

	public static double calculateSpeedOnEdge(AbstractEdge edge, Vehicle vehicle) {
		return Math.min(edge.getSpeed(), vehicle.getMaxSpeed());
	}

	public static double calculateTravelTimeOnEdge(AbstractEdge edge) {
		return edge.getLength() / edge.getSpeed();
	}

	public static double calculateTravelTimeOnEdge(AbstractEdge edge, Vehicle vehicle) {
		return vehicle != null ? edge.getLength() / calculateSpeedOnEdge(edge, vehicle)
				: calculateTravelTimeOnEdge(edge);
	}

	public static double calculateRoutingSpeed(List<Double> vehiclesOnEdgeSpeeds) {
		return calculateRoutingSpeed(
				vehiclesOnEdgeSpeeds.isEmpty() ? null : vehiclesOnEdgeSpeeds.stream().mapToDouble(Double::doubleValue));
	}

	private static double calculateRoutingSpeed(DoubleStream vehiclesOnEdgeSpeeds) {
		return vehiclesOnEdgeSpeeds == null ? 0.0 : vehiclesOnEdgeSpeeds.average().getAsDouble();
	}

	public static double calculateNettoOccupancy(AbstractEdge edge, List<Vehicle> vehicles) {
		return vehicles.isEmpty() ? 0.0
				: calculateOccupancy(edge, vehicles.stream().map(Vehicle::getLength).reduce(0.0, (a, b) -> a + b));
	}

	public static double calculateBruttoOccupancy(AbstractEdge edge, List<Vehicle> vehicles) {
		return vehicles.isEmpty() ? 0.0
				: calculateOccupancy(edge,
						vehicles.stream().map(e -> e.getLength() + e.getMinGap()).reduce(0.0, (a, b) -> a + b));
	}

	private static double calculateOccupancy(AbstractEdge edge, double vehiclesOnEdgeLengthSum) {
		return vehiclesOnEdgeLengthSum / edge.getLength() * 100;
	}

	private CalculatorUtil() {
	}

}
