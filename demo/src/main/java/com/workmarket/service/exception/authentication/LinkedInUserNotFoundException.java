package com.workmarket.service.exception.authentication;

import com.workmarket.service.business.LinkedInResult;
import org.springframework.security.core.AuthenticationException;

/**
 * User: micah
 * Date: 2/27/13
 * Time: 9:32 PM
 */
public class LinkedInUserNotFoundException extends AuthenticationException {
	private LinkedInResult linkedInResult;

	public LinkedInUserNotFoundException(String message) {
		super(message);
	}

	public LinkedInResult getLinkedInResult() {
		return this.linkedInResult;
	}

	public AuthenticationException
	 setLinkedInResult(LinkedInResult linkedInResult)
	{
		this.linkedInResult = linkedInResult;
		return this;
	}
}
