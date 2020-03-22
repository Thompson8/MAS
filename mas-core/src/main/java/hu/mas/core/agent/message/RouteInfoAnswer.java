package hu.mas.core.agent.message;

import hu.mas.core.util.Pair;

import java.util.List;

import hu.mas.core.agent.Route;

public class RouteInfoAnswer implements MessageBody {

	private final List<Pair<Double, Route>> routes;

	public RouteInfoAnswer(List<Pair<Double, Route>> routes) {
		this.routes = routes;
	}

	public List<Pair<Double, Route>> getRoutes() {
		return routes;
	}

	@Override
	public String toString() {
		return "RouteInfoAnswer [routes=" + routes + "]";
	}

}
