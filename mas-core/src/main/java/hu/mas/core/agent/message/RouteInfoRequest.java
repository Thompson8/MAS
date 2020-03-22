package hu.mas.core.agent.message;

import hu.mas.core.agent.Vehicle;
import hu.mas.core.mas.model.Node;

public class RouteInfoRequest implements MessageBody {

	private final Node from;

	private final Node to;

	private final Vehicle vehicle;

	public RouteInfoRequest(Node from, Node to, Vehicle vehicle) {
		this.from = from;
		this.to = to;
		this.vehicle = vehicle;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
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
