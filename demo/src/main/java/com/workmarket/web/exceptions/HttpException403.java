package com.workmarket.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class HttpException403 extends WebException {
	public HttpException403() {
	}

	public HttpException403(String message) {
		super(message);
	}

	public HttpException403(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException403(Throwable cause) {
		super(cause);
	}
}
