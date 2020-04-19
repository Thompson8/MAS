package hu.mas.core.mas.model.message;

import hu.mas.core.agent.model.route.MasRoute;
import hu.mas.core.util.Pair;

import java.util.List;

public class RouteInfoAnswer implements MessageBody {

	private final List<Pair<Double, MasRoute>> routes;

	public RouteInfoAnswer(List<Pair<Double, MasRoute>> routes) {
		this.routes = routes;
	}

	public List<Pair<Double, MasRoute>> getRoutes() {
		return routes;
	}

	@Override
	public String toString() {
		return "RouteInfoAnswer [routes=" + routes + "]";
	}

}
