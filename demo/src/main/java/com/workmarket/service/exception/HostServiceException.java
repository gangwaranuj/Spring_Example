package com.workmarket.service.exception;

public class HostServiceException extends Exception {
	private static final long serialVersionUID = 1L;
	public HostServiceException() {
		super();
	}
	public HostServiceException(String message) {
		super(message);
	}
	public HostServiceException(Throwable e) {
		super(e);
	}
}