package hu.mas.core.agent.message;

import java.util.Optional;

import hu.mas.core.mas.model.Edge;

public class LocationInfoAnswer implements MessageBody {

	private final Optional<Edge> currentEdge;

	public LocationInfoAnswer(Optional<Edge> currentEdge) {
		this.currentEdge = currentEdge;
	}

	public Optional<Edge> getCurrentEdge() {
		return currentEdge;
	}

	@Override
	public String toString() {
		return "LocationInfoAnswer [currentEdge=" + currentEdge + "]";
	}

}
