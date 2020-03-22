package hu.mas.core.mas.intention.simple;

import hu.mas.core.agent.Vehicle;

public class Intention {

	private Vehicle vehicle;
	
	private Double start;
	
	private Double finish;

	public Intention(Vehicle vehicle, Double start, Double finish) {
		this.vehicle = vehicle;
		this.start = start;
		this.finish = finish;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Double getStart() {
		return start;
	}

	public void setStart(Double start) {
		this.start = start;
	}

	public Double getFinish() {
		return finish;
	}

	public void setFinish(Double finish) {
		this.finish = finish;
	}

	@Override
	public String toString() {
		return "Intention [vehicle=" + vehicle + ", start=" + start + ", finish=" + finish + "]";
	}
	
}
