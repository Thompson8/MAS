package hu.mas.core.agent.model.exception;

public class AgentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AgentException(String msg) {
		super(msg);
	}

	public AgentException(Throwable t) {
		super(t);
	}

}
