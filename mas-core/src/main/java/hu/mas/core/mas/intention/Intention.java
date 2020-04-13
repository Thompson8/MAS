package hu.mas.core.mas.intention;

import hu.mas.core.agent.model.vehicle.Vehicle;

public class Intention {

	private final Vehicle vehicle;
	
	private final Double start;
	
	private final Double finish;

	public Intention(Vehicle vehicle, Double start, Double finish) {
		this.vehicle = vehicle;
		this.start = start;
		this.finish = finish;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public Double getStart() {
		return start;
	}

	public Double getFinish() {
		return finish;
	}

	@Override
	public String toString() {
		return "Intention [vehicle=" + vehicle + ", start=" + start + ", finish=" + finish + "]";
	}
	
}
