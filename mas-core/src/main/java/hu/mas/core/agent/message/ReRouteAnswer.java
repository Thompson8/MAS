package hu.mas.core.agent.message;

public class ReRouteAnswer implements MessageBody {

	private final String routeId;

	public ReRouteAnswer(String routeId) {
		this.routeId = routeId;
	}

	public String getRouteId() {
		return routeId;
	}

	@Override
	public String toString() {
		return "ReRouteAnswer [routeId=" + routeId + "]";
	}

}
