package hu.mas.core.mas.model.message;

import hu.mas.core.agent.model.vehicle.Vehicle;

public class RouteStartedRequest implements MessageBody {

	private final Vehicle vehicle;

	public RouteStartedRequest(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	@Override
	public String toString() {
		return "RouteStartedRequest [vehicle=" + vehicle + "]";
	}

}
