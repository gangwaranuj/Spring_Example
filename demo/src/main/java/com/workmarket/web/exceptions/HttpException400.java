package com.workmarket.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class HttpException400 extends WebException {
	public HttpException400() {}
	public HttpException400(String message) {
		super(message);
	}

	public HttpException400(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException400(Throwable cause) {
		super(cause);
	}
}
