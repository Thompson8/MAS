package hu.mas.core.mas.intention.model;

import java.util.ArrayList;
import java.util.List;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.Edge;
import hu.mas.core.mas.util.CalculatorUtil;
import hu.mas.core.util.Pair;

public class Road {

	private final Edge edge;

	private final List<Pair<Vehicle, Pair<Double, Double>>> arrivalList;

	private Double predictedTravelTime;

	public Road(Edge edge) {
		this.edge = edge;
		this.arrivalList = new ArrayList<>();
		this.predictedTravelTime = CalculatorUtil.calculateTravelTimeOnEdge(edge);
	}

	public List<Pair<Vehicle, Pair<Double, Double>>> getArrivalList() {
		return arrivalList;
	}

	public Edge getEdge() {
		return edge;
	}

	public Double getPredictedTravelTime() {
		return predictedTravelTime;
	}

	public void setPredictedTravelTime(Double predictedTravelTime) {
		this.predictedTravelTime = predictedTravelTime;
	}

	@Override
	public String toString() {
		return "Road [edge=" + edge + ", arrivalList=" + arrivalList + ", predictedTravelTime=" + predictedTravelTime
				+ "]";
	}

}
