package com.workmarket.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class WebException extends RuntimeException {
	String messageKey;
	String redirectUri;
	boolean printStackTrace = true;

	public WebException() {}
	public WebException(String message) {
		super(message);
	}

	public WebException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebException(Throwable cause) {
		super(cause);
	}

	public String getMessageKey() {
		return messageKey;
	}

	public WebException setMessageKey(String messageKey) {
		this.messageKey = messageKey;
		return this;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public WebException setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
		return this;
	}

	public boolean hasRedirectUri() {
		return redirectUri != null;
	}

	public boolean shouldPrintStackTrace() {
		return printStackTrace;
	}

	public void setPrintStackTrace(boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
	}
}
