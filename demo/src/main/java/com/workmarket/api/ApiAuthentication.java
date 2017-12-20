package com.workmarket.api;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created by joshlevine on 12/27/16.
 */
public class ApiAuthentication extends AbstractAuthenticationToken {
	private static final long serialVersionUID = -8736661608686127670L;
	private UserDetails principal;

	public ApiAuthentication(UserDetails principal) {
		super(principal.getAuthorities());
		this.principal = principal;
		this.setAuthenticated(true);
	}

	@Override
	public String getName() {
		return "Api-Authentication";
	}

	@Override
	public Object getCredentials() {
		return principal;
	}

	@Override
	public Object getDetails() {
		return principal;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}
