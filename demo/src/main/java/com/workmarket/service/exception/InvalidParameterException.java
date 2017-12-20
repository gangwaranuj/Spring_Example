package com.workmarket.service.exception;

public class InvalidParameterException extends WorkMarketException {

	private static final long serialVersionUID = -6394387239788433425L;
	
	public InvalidParameterException(String message, Exception e) {
		super(message, e);
	}
	public InvalidParameterException(String message) {
		super(message);
	}

}
