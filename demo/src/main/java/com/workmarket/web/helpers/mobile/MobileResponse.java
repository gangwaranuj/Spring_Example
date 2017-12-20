package com.workmarket.web.helpers.mobile;

public class MobileResponse {
	private String message;
	private boolean successful;

	public MobileResponse(){}

	public MobileResponse(boolean successful, String message) {
		this.successful = successful;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}
