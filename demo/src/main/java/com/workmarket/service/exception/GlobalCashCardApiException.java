package com.workmarket.service.exception;

/**
 * @author: gbluv
 */
public class GlobalCashCardApiException extends RuntimeException {

	/**
	 * Instance variables and constants
	 */
	private static final long serialVersionUID = 5446890555616473087L;


	public GlobalCashCardApiException(){
		super();
	}

	/**
	 * @param message
	 */
	public GlobalCashCardApiException(String message) {
		super(message);
	}

	public GlobalCashCardApiException(String message,Throwable exception){
		super(message,exception);
	}
}
