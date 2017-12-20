package com.workmarket.service.exception.authentication;

import org.springframework.security.core.AuthenticationException;

public class EmailNotConfirmedException extends AuthenticationException {
	private String userNumber;

	public EmailNotConfirmedException(String message) {
		super(message);
	}

	public String getUserNumber() {
		return userNumber;
	}
	public AuthenticationException setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}
}
