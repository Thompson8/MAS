package hu.mas.core.agent.message;

public class RouteSelectionAnswer implements MessageBody {

	private String message;

	public RouteSelectionAnswer(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "RouteSelectionAnswer [message=" + message + "]";
	}

}
