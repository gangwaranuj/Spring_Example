package com.workmarket.domains.authentication.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class SocialAuthenticationToken extends AbstractAuthenticationToken {
	Object principal;
	String credentials;
	Object details;

	public SocialAuthenticationToken(String oauthToken) {
		super(null);
		this.credentials = oauthToken;
		setAuthenticated(false);
	}

	public SocialAuthenticationToken(UserDetails details) {
		super(details.getAuthorities());
		this.principal = details;
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	public Object getDetails() {
		return details;
	}

	public void setDetails(Object details) {
		this.details = details;
	}
}
