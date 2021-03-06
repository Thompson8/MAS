package hu.mas.core.mas.model.message;

import hu.mas.core.agent.model.vehicle.Vehicle;
import hu.mas.core.mas.model.graph.Vertex;

public class RouteInfoRequest implements MessageBody {

	private final Vertex from;

	private final Vertex to;

	private final Vehicle vehicle;

	public RouteInfoRequest(Vertex from, Vertex to, Vehicle vehicle) {
		this.from = from;
		this.to = to;
		this.vehicle = vehicle;
	}

	public Vertex getFrom() {
		return from;
	}

	public Vertex getTo() {
		return to;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	@Override
	public String toString() {
		return "RouteInfoRequest [from=" + from + ", to=" + to + "]";
	}

}
