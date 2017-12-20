package com.workmarket.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class HttpException401 extends WebException {
	public HttpException401() {
		this.redirectUri = "redirect:/error/no_access";
	}
	public HttpException401(String message) {
		super(message);
	}

	public HttpException401(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException401(Throwable cause) {
		super(cause);
	}
}
