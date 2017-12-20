package com.workmarket.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class HttpException405 extends WebException {
	public HttpException405() {}
	public HttpException405(String message) {
		super(message);
	}

	public HttpException405(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException405(Throwable cause) {
		super(cause);
	}
}
