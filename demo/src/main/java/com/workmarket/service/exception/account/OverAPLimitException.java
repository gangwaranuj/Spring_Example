package com.workmarket.service.exception.account;


public class OverAPLimitException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5952473675876965754L;

	public OverAPLimitException(){
		//Empty constructor
	}
	
	public OverAPLimitException(String message){
		super(message);
	}	
}
