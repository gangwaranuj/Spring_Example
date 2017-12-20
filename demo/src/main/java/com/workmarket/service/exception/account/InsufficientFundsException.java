package com.workmarket.service.exception.account;


public class InsufficientFundsException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5952473675876965754L;

	public InsufficientFundsException() {
		//Empty constructor
	}
	
	public InsufficientFundsException(String message) {
		super(message);
	}	
}
