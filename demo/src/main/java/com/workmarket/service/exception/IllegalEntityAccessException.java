package com.workmarket.service.exception;

/**
 * Created by nick on 2012-12-13 11:06 AM
 */
public class IllegalEntityAccessException extends Exception {
	private static final long serialVersionUID = 1L;
	public IllegalEntityAccessException() {
	}

	public IllegalEntityAccessException(String s) {
		super(s);
	}

	public IllegalEntityAccessException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
