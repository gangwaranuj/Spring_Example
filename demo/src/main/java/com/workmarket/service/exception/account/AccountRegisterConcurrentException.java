package com.workmarket.service.exception.account;


public class AccountRegisterConcurrentException extends RuntimeException {
	
	/**
	 * Instance variables and constants
	 */
	private static final long serialVersionUID = 5246890555615473067L;

	/**
	 * 
	 */
	public AccountRegisterConcurrentException() {
		super();
	}

	/**
	 * @param message
	 */
	public AccountRegisterConcurrentException(String message) {
		super(message);
	}
	
}
