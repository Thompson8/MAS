package hu.mas.core.mas.model.exception;

public class MasRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 8797359757616007589L;

	public MasRuntimeException(String message) {
		super(message);
	}

	public MasRuntimeException(Exception e) {
		this(e.getMessage(), e);
	}

	public MasRuntimeException(String message, Exception e) {
		super(message, e);
	}

}
