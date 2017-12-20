package com.workmarket.service.exception.account;


public class AccountRegisterNoActiveWorkResourceException extends RuntimeException {
	
	/**
	 * Instance variables and constants
	 */
	private static final long serialVersionUID = 2947965535891415843L;

	/**
	 * 
	 */
	public AccountRegisterNoActiveWorkResourceException() {
		super();
	}

	/**
	 * @param message
	 */
	public AccountRegisterNoActiveWorkResourceException(String message) {
		super(message);
	}
	
}
