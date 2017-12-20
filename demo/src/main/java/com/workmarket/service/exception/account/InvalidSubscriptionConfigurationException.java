package com.workmarket.service.exception.account;

public class InvalidSubscriptionConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidSubscriptionConfigurationException() {
		super();
	}

	public InvalidSubscriptionConfigurationException(String message) {
		super(message);
	}
}
