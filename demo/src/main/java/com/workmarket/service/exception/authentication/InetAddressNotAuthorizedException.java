package com.workmarket.service.exception.authentication;

import org.springframework.security.core.AuthenticationException;

public class InetAddressNotAuthorizedException extends AuthenticationException {
	public InetAddressNotAuthorizedException(String message) {
		super(message);
	}
}
