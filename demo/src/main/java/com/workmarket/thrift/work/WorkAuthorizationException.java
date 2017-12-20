package com.workmarket.thrift.work;

public class WorkAuthorizationException extends Exception {

	public WorkAuthorizationException() {}

	public WorkAuthorizationException(String message) {
		super(message);
	}

	public WorkAuthorizationException(Throwable cause) {
		super(cause);
	}

}
