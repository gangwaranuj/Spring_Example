package com.workmarket.service.exception.account;


public class InvalidAccountRegisterException extends RuntimeException {

	/**
	 *
	 */
	public InvalidAccountRegisterException() {
		super();
	}

	/**
	 * @param message
	 */
	public InvalidAccountRegisterException(String message) {
		super(message);
	}
	
}
