package com.workmarket.domains.authentication.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * An authentication token when authenticating via session with the authentication service.
 */
public class AuthServiceAuthenticationToken extends AbstractAuthenticationToken {
	private Object principal;
	private String credentials;
	private Object details;

	public AuthServiceAuthenticationToken(final UserDetails details) {
		super(details.getAuthorities());
		this.principal = details;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	public Object getDetails() {
		return details;
	}

	public void setDetails(Object details) {
		this.details = details;
	}
}
