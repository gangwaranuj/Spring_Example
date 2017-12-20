package com.workmarket.service.exception;

public class WorkMarketException extends Exception {

	private static final long serialVersionUID = 3931579881012601353L;

	public WorkMarketException(String message, Exception e) {
		super(message, e);
	}

	public WorkMarketException(String message) {
		super(message);
	}

}
