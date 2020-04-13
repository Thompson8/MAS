package hu.mas.core.agent.model.message;

import hu.mas.core.agent.model.route.Route;
import hu.mas.core.agent.model.vehicle.Vehicle;

public class ReRouteStartedRequest implements MessageBody {
	
	private final Route route;

	private final Vehicle vehicle;

	public ReRouteStartedRequest(Route route, Vehicle vehicle) {
		this.route = route;
		this.vehicle = vehicle;
	}

	public Route getRoute() {
		return route;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	@Override
	public String toString() {
		return "ReRouteRequest [route=" + route + ", vehicle=" + vehicle + "]";
	}
	
}
