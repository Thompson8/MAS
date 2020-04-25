package hu.mas.core.mas.model.graph;

import java.util.ArrayList;
import java.util.List;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.util.CalculatorUtil;
import hu.mas.core.util.Pair;

public class Road {

	private final AbstractEdge edge;

	private final List<Pair<Vehicle, Pair<Double, Double>>> arrivalList;

	private final List<Pair<Vehicle, Pair<Double, Double>>> vehiclesOnRoad;
	
	private Double predictedTravelTime;

	public Road(AbstractEdge edge) {
		this.edge = edge;
		this.arrivalList = new ArrayList<>();
		this.vehiclesOnRoad = new ArrayList<>();
		this.predictedTravelTime = CalculatorUtil.calculateTravelTimeOnEdge(edge);
	}

	public List<Pair<Vehicle, Pair<Double, Double>>> getArrivalList() {
		return arrivalList;
	}

	public AbstractEdge getEdge() {
		return edge;
	}

	public Double getPredictedTravelTime() {
		return predictedTravelTime;
	}

	public void setPredictedTravelTime(Double predictedTravelTime) {
		this.predictedTravelTime = predictedTravelTime;
	}

	public List<Pair<Vehicle, Pair<Double, Double>>> getVehiclesOnRoad() {
		return vehiclesOnRoad;
	}

	@Override
	public String toString() {
		return "Road [edge=" + edge + ", arrivalList=" + arrivalList + ", predictedTravelTime=" + predictedTravelTime
				+ "]";
	}

}
