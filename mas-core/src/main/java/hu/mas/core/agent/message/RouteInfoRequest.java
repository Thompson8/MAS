package hu.mas.core.agent.message;

import hu.mas.core.mas.model.Node;

public class RouteInfoRequest implements MessageBody {

	private final Node from;

	private final Node to;

	public RouteInfoRequest(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

	@Override
	public String toString() {
		return "RouteInfoRequest [from=" + from + ", to=" + to + "]";
	}

}
