package hu.mas.core.agent.message;

import hu.mas.core.agent.Vehicle;

public class LocationInfoRequest implements MessageBody {

	private final Vehicle vehicle;

	public LocationInfoRequest(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	@Override
	public String toString() {
		return "LocationInfoRequest [vehicle=" + vehicle + "]";
	}

}
