package com.workmarket.service.exception.account;


public class InvalidPricingException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5952474655876965754L;

	public InvalidPricingException(){
		//Empty constructor
	}
	
	public InvalidPricingException(String message){
		super(message);
	}	
}
