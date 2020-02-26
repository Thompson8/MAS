package hu.mas.core.mas;

import hu.mas.core.agent.Route;
import hu.mas.core.mas.model.Edge;

public class VehicleData {

	private Route route;

	private Edge currentEdge;

	private final VehicleStatistics statistics;

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

	@Override
	public String toString() {
		return "VehicleData [route=" + route + ", currentEdge=" + currentEdge + ", statistics=" + statistics + "]";
	}

}
