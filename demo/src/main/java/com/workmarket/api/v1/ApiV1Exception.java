package com.workmarket.api.v1;

import com.workmarket.api.exceptions.ApiException;

/**
 * Returns a meaningful API Exception which can be used for logging and for returning to consumers.
 */
public class ApiV1Exception extends ApiException {
	//TODO consider inheriting from WebException and what that would mean
	final private Object[] arguments;
	final private Integer statusCode;

	public ApiV1Exception(String errorMessageProperty, int statusCode, Object... arguments) {
		super(errorMessageProperty);
		this.arguments = arguments;
		this.statusCode = statusCode;
	}

	public Object[] getMessageArguments() {
		return arguments;
	}

	public Integer getStatusCode() {
		return statusCode;
	}
}
