package com.workmarket.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class HttpException404 extends WebException {
	public HttpException404() {
		this.redirectUri = "redirect:/error/404";
	}
	public HttpException404(String message) {
		super(message);
	}

	public HttpException404(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException404(Throwable cause) {
		super(cause);
	}
}
