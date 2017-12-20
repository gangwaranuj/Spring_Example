package com.workmarket.test.mock.auth;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.test.mock.answer.defaults.DefaultExtendedUserDetailsAnswer;

public class AuthenticationMock implements Authentication {
	private static final long serialVersionUID = -8736661608686127670L;
	private ExtendedUserDetails principal;
	
	public AuthenticationMock(ExtendedUserDetails principal) {
		this.principal = principal;
	}
	
	public AuthenticationMock(DefaultExtendedUserDetailsAnswer answer) {
		try {
			this.principal = answer.answer(null);
		} 
		catch (Throwable e) { /*never happens*/ }
	}
	
	@Override
	public String getName() {
		return "Test-Auth";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return principal.getAuthorities();
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

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		

	}

}
