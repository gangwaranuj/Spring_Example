package com.workmarket.service.exception.authentication;

import org.springframework.security.core.AuthenticationException;

public class InvalidGoogleRecaptchResponseException extends AuthenticationException {
	private String userName;

	public InvalidGoogleRecaptchResponseException(String message) {
		super(message);
	}

	public String getUserName() {
		return userName;
	}
	public AuthenticationException setUserName(String userName) {
		this.userName = userName;
		return this;
	}
}
