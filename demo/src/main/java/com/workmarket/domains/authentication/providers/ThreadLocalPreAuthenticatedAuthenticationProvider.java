package com.workmarket.domains.authentication.providers;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;

import com.workmarket.domains.model.User;
import com.workmarket.service.infra.business.AuthenticationService;

public class ThreadLocalPreAuthenticatedAuthenticationProvider implements AuthenticationProvider, InitializingBean, Ordered {

	private AuthenticationUserDetailsService preAuthenticatedUserDetailsService = null;
	private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

	@Autowired private AuthenticationService authenticationService;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		User currentUser = authenticationService.getCurrentUser();
		authentication = new PreAuthenticatedAuthenticationToken(currentUser.getEmail(), "");

		UserDetails ud = preAuthenticatedUserDetailsService.loadUserDetails(authentication);

		userDetailsChecker.check(ud);

		PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(ud, authentication.getCredentials(), ud.getAuthorities());
		result.setDetails(authentication.getDetails());

		return result;
	}

	public boolean supports(Class<? extends Object> authentication) {
		return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public void setPreAuthenticatedUserDetailsService(AuthenticationUserDetailsService aPreAuthenticatedUserDetailsService) {
		this.preAuthenticatedUserDetailsService = aPreAuthenticatedUserDetailsService;
	}

	public void setUserDetailsChecker(UserDetailsChecker userDetailsChecker) {
		Assert.notNull(userDetailsChecker, "userDetailsChecker cannot be null");
		this.userDetailsChecker = userDetailsChecker;
	}

	public void afterPropertiesSet() throws Exception {}

	public int getOrder() {
		return -1;
	}
}
