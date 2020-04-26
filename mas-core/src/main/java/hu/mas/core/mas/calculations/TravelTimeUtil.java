package hu.mas.core.mas.calculations;

import java.util.List;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.Road;
import hu.mas.core.mas.util.CalculatorUtil;

public class TravelTimeUtil {

	private static final double EXPERIMENTAL_CONSTANT = 0.001;

	private final double timeFrameForFlowCalculation;

	public TravelTimeUtil(double timeFrameForFlowCalculation) {
		this.timeFrameForFlowCalculation = timeFrameForFlowCalculation;
	}

	public double calculateRoadTravelTimeByCurrentRoadConditions(Road road, Vehicle vehicle, double time) {
		final double lookForStart = time - timeFrameForFlowCalculation;
		double flow = calculateFlow(
				road.getVehiclesOnRoad().stream()
						.filter(e -> e.getRigth().getLeft() >= lookForStart && e.getRigth().getLeft() <= time).count(),
				timeFrameForFlowCalculation);
		return calculateRoadTravelTime(road, vehicle, flow);
	}

	public double calculateRoadTravelTimeByIntention(Road road, Vehicle vehicle, double time) {
		final double lookForStart = time - timeFrameForFlowCalculation;
		double flow = calculateFlow(
				road.getArrivalList().stream()
						.filter(e -> lookForStart <= e.getRigth().getLeft() && e.getRigth().getRigth() <= time).count(),
				timeFrameForFlowCalculation);
		return calculateRoadTravelTime(road, vehicle, flow);
	}

	protected double calculateFlow(List<Vehicle> vehicles, double timeFrameForFlowCalculation) {
		return calculateFlow(vehicles.size(), timeFrameForFlowCalculation);
	}

	protected double calculateFlow(long count, double timeFrameForFlowCalculation) {
		return count / timeFrameForFlowCalculation;
	}

	protected double calculateRoadTravelTime(Road road, Vehicle vehicle, double flow) {
		double a = road.getEdge().getLength() * EXPERIMENTAL_CONSTANT;
		double b = CalculatorUtil.calculateTravelTimeOnEdge(road.getEdge(), vehicle);
		return a * flow + b;
	}

}
