package hu.mas.core.agent.model.message;

import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.agent.model.vehicle.Vehicle;

public class RouteSelectionRequest implements MessageBody {

	private final MasRoute route;

	private final Vehicle vehicle;

	public RouteSelectionRequest(MasRoute route, Vehicle vehicle) {
		this.route = route;
		this.vehicle = vehicle;
	}

	public MasRoute getRoute() {
		return route;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	@Override
	public String toString() {
		return "RouteSelection [route=" + route + ", vehicle=" + vehicle + "]";
	}

}
