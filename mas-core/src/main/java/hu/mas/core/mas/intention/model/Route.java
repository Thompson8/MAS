package hu.mas.core.mas.intention.model;

import java.util.List;

import hu.mas.core.mas.model.graph.Vertex;

public class Route {

	private final Vertex from;

	private final Vertex to;

	private final List<Road> roads;
	
	private Double predictedTravelTime;

	public Route(Vertex from, Vertex to, List<Road> roads) {
		this.from = from;
		this.to = to;
		this.roads = roads;
	}

	public Vertex getFrom() {
		return from;
	}

	public Vertex getTo() {
		return to;
	}

	public List<Road> getRoads() {
		return roads;
	}

	public Double getPredictedTravelTime() {
		return predictedTravelTime;
	}

	public void setPredictedTravelTime(Double predictedTravelTime) {
		this.predictedTravelTime = predictedTravelTime;
	}

	@Override
	public String toString() {
		return "Route [from=" + from + ", to=" + to + ", roads=" + roads + ", predictedTravelTime="
				+ predictedTravelTime + "]";
	}

}
