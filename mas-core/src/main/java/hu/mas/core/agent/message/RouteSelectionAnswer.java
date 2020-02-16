package hu.mas.core.agent.message;

public class RouteSelectionAnswer implements MessageBody {

	private final String routeId;

	public RouteSelectionAnswer(String routeId) {
		this.routeId = routeId;
	}

	public String getRouteId() {
		return routeId;
	}

	@Override
	public String toString() {
		return "RouteSelectionAnswer [routeId=" + routeId + "]";
	}

}
