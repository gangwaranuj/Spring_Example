package com.workmarket.service.exception.authentication;

import org.springframework.security.core.AuthenticationException;

/**
 * User: micah
 * Date: 3/18/13
 * Time: 5:59 PM
 */
public class InternalAuthenticationException extends AuthenticationException {
	public InternalAuthenticationException(String msg) {
		super(msg);
	}
}
