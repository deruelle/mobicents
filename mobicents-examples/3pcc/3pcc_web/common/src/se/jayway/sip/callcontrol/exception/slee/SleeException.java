package se.jayway.sip.callcontrol.exception.slee;

public class SleeException extends Exception {
	private static final long serialVersionUID = 6506012685500086397L;

	public SleeException() {
		super();
	}

	public SleeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SleeException(String message) {
		super(message);
	}

	public SleeException(Throwable cause) {
		super(cause);
	}

}