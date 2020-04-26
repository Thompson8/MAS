package hu.mas.core.mas.model.vehicle;

import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.mas.model.graph.AbstractEdge;

public class VehicleData {

	private MasRoute route;

	private AbstractEdge currentEdge;

	private Double currentSpeed;

	private final VehicleStatistics statistics;

	private Integer expectedStartIteration;

	private Integer expectedFinishIteration;

	public VehicleData() {
		this.statistics = new VehicleStatistics();
	}

	public MasRoute getRoute() {
		return route;
	}

	public void setRoute(MasRoute route) {
		this.route = route;
	}

	public AbstractEdge getCurrentEdge() {
		return currentEdge;
	}

	public void setCurrentEdge(AbstractEdge currentEdge) {
		this.currentEdge = currentEdge;
	}

	public Double getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(Double currentSpeed) {
		this.currentSpeed = currentSpeed;
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
		return "VehicleData [route=" + route + ", currentEdge=" + currentEdge + ", currentSpeed=" + currentSpeed
				+ ", statistics=" + statistics + ", expectedStartIteration=" + expectedStartIteration
				+ ", expectedFinishIteration=" + expectedFinishIteration + "]";
	}

}
