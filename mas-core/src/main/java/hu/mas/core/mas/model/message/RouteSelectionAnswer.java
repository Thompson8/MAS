package hu.mas.core.mas.model.message;

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
