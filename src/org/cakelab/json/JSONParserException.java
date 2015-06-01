package org.cakelab.json;

public class JSONParserException extends Exception {
	private static final long serialVersionUID = 1L;

	public JSONParserException() {
		super();
	}

	public JSONParserException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JSONParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public JSONParserException(String message) {
		super(message);
	}

	public JSONParserException(Throwable cause) {
		super(cause);
	}

}
