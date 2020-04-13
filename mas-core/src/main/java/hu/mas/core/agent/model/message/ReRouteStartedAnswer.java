package hu.mas.core.agent.model.message;

public class ReRouteStartedAnswer implements MessageBody {

	private final String message;

	public ReRouteStartedAnswer(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "ReRouteAnswer [message=" + message + "]";
	}
	
}
