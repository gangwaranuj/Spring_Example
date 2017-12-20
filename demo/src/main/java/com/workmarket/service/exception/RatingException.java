package com.workmarket.service.exception;

/**
 * Created by michaelrothbaum on 8/29/17.
 */
public class RatingException extends Exception {

	public RatingException(String message, Exception e) {
		super(message, e);
	}

	public RatingException(String message) {
		super(message);
	}

}
