package hu.mas.core.mas.util;

import java.util.List;
import java.util.stream.DoubleStream;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.Edge;

public class CalculatorUtil {

	public static double calculateSpeedOnEdge(Edge edge, Vehicle vehicle) {
		return Math.min(edge.getSpeed(), vehicle.getMaxSpeed());
	}

	public static double calculateTravelTimeOnEdge(Edge edge) {
		return edge.getLength() / edge.getSpeed();
	}

	public static double calculateTravelTimeOnEdge(Edge edge, Vehicle vehicle) {
		return vehicle != null ? edge.getLength() / calculateSpeedOnEdge(edge, vehicle)
				: calculateTravelTimeOnEdge(edge);
	}

	public static double calculateRoutingSpeed(Edge edge, List<Double> vehiclesOnEdgeSpeeds) {
		return calculateRoutingSpeed(edge,
				vehiclesOnEdgeSpeeds.isEmpty() ? null : vehiclesOnEdgeSpeeds.stream().mapToDouble(Double::doubleValue));
	}

	private static double calculateRoutingSpeed(Edge edge, DoubleStream vehiclesOnEdgeSpeeds) {
		return vehiclesOnEdgeSpeeds == null ? calculateTravelTimeOnEdge(edge)
				: Math.max(edge.getLength() / vehiclesOnEdgeSpeeds.average().getAsDouble(),
						calculateTravelTimeOnEdge(edge));
	}

	public static double calculateNettoOccupancy(Edge edge, List<Vehicle> vehicles) {
		return vehicles.isEmpty() ? 0.0
				: calculateOccupancy(edge, vehicles.stream().map(Vehicle::getLength).reduce(0.0, (a, b) -> a + b));
	}

	public static double calculateBruttoOccupancy(Edge edge, List<Vehicle> vehicles) {
		return vehicles.isEmpty() ? 0.0
				: calculateOccupancy(edge,
						vehicles.stream().map(e -> e.getLength() + e.getMinGap()).reduce(0.0, (a, b) -> a + b));
	}

	private static double calculateOccupancy(Edge edge, double vehiclesOnEdgeLengthSum) {
		return vehiclesOnEdgeLengthSum / edge.getLength() * 100;
	}

	private CalculatorUtil() {
	}

}
