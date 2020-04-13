package hu.mas.core.mas.model.exception;

public class MasException extends Exception {

	private static final long serialVersionUID = 9089999018944559681L;

	public MasException(String message) {
		super(message);
	}

	public MasException(Exception e) {
		this(e.getMessage(), e);
	}

	public MasException(String message, Exception e) {
		super(message, e);
	}

}
