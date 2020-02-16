package hu.mas.core.agent.message;

import hu.mas.core.util.Pair;

import java.util.List;

import hu.mas.core.mas.model.Node;

public class RouteInfoAnswer implements MessageBody {

	private final List<Pair<Double, List<Node>>> route;

	public RouteInfoAnswer(List<Pair<Double, List<Node>>> route) {
		this.route = route;
	}

	public List<Pair<Double, List<Node>>> getRoute() {
		return route;
	}

	@Override
	public String toString() {
		return "RouteInfoAnswer [route=" + route + "]";
	}

}
