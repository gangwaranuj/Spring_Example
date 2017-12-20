package com.workmarket.dao.exception.random;

/**
 * User: alexsilva Date: 4/9/14 Time: 1:53 PM
 */
public class IdentifierGenerationException extends RuntimeException {

	private static final long serialVersionUID = -1969370245921789285L;

	public IdentifierGenerationException() {
		super();
	}

	public IdentifierGenerationException(String message) {
		super(message);
	}

}
