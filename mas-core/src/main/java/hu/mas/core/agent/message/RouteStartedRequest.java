package hu.mas.core.agent.message;

import hu.mas.core.agent.Vehicle;

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
