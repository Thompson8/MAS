package hu.mas.core.agent.model.message;

import hu.mas.core.agent.model.route.Route;
import hu.mas.core.util.Pair;

import java.util.List;

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
