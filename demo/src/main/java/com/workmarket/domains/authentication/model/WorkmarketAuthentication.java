package com.workmarket.domains.authentication.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

/**
 * Created by bluesockets on 11/26/14.
 * This class provides a deep copy of the Authentication object passed around by spring.
 * Without this wrapper class, sensitive information ends up exposed and vulnerable because it gets passed around to a memcache session manager.
 * It requires serialization because it's serialized and passed around as a saved session.
 */
public class WorkmarketAuthentication implements Authentication {

	private static final long serialVersionUID = 5442008691012639237L;
	protected Collection<? extends GrantedAuthority> authorities;
	protected Object details;
	protected Object principal;
	protected String name;
	protected boolean authenticated;

	public WorkmarketAuthentication() {}

	public WorkmarketAuthentication(
		final Collection<? extends GrantedAuthority> newAuthorities,
		final Object details,
		final Object principal,
		final String name,
		final boolean authenticated
	) {
		this.authorities = newAuthorities;
		this.details = details;
		this.principal = principal;
		this.name = name;
		this.authenticated = authenticated;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return "";
	}

	@Override
	public Object getDetails() {
		return details;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean b) throws IllegalArgumentException {}

	@Override
	public String getName() {
		return name;
	}

}
