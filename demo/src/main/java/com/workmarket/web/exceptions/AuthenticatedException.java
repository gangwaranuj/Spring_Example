package com.workmarket.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AuthenticatedException extends ServletException {
	private static final long serialVersionUID = 8220138503420438555L;

	private final Exception e;
	private final String userEmail;
	private final String userNumber;
	private final String companyName;
	private final String companyId;

	public AuthenticatedException(Exception e, String userEmail, String userNumber, String companyName, String companyId) {
		super(e);
	    this.e = e;
		this.userEmail = userEmail;
		this.userNumber = userNumber;
		this.companyName = companyName;
		this.companyId = companyId;
	}

	public Exception getE() {
		return e;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getCompanyId() {
		return companyId;
	}
}
