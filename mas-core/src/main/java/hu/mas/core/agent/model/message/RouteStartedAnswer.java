package hu.mas.core.agent.model.message;

public class RouteStartedAnswer implements MessageBody {

	private final String message;

	public RouteStartedAnswer(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "RouteStartedAnswer [message=" + message + "]";
	}
	
}
