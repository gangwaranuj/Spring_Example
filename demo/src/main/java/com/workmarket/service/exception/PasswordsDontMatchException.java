package com.workmarket.service.exception;

public class PasswordsDontMatchException extends WorkMarketException {

	private static final long serialVersionUID = -104792611982349500L;

	public PasswordsDontMatchException(String message, Exception e) {
		super(message, e);
	}
	public PasswordsDontMatchException(String message) {
		super(message);
	}
	
}
