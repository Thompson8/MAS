package hu.mas.core.mas;

import hu.mas.core.agent.model.route.Route;
import hu.mas.core.mas.model.graph.Edge;

public class VehicleData {

	private Route route;

	private Edge currentEdge;

	private final VehicleStatistics statistics;

	private Integer expectedStartIteration;
	
	private Integer expectedFinishIteration;
	
	public VehicleData() {
		this.statistics = new VehicleStatistics();
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Edge getCurrentEdge() {
		return currentEdge;
	}

	public void setCurrentEdge(Edge currentEdge) {
		this.currentEdge = currentEdge;
	}

	public VehicleStatistics getStatistics() {
		return statistics;
	}

	public Integer getExpectedStartIteration() {
		return expectedStartIteration;
	}

	public void setExpectedStartIteration(Integer expectedStartIteration) {
		this.expectedStartIteration = expectedStartIteration;
	}

	public Integer getExpectedFinishIteration() {
		return expectedFinishIteration;
	}

	public void setExpectedFinishIteration(Integer expectedFinishIteration) {
		this.expectedFinishIteration = expectedFinishIteration;
	}

	@Override
	public String toString() {
		return "VehicleData [route=" + route + ", currentEdge=" + currentEdge + ", statistics=" + statistics + "]";
	}

}
