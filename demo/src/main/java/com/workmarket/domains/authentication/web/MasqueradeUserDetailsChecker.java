package com.workmarket.domains.authentication.web;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

public class MasqueradeUserDetailsChecker extends AccountStatusUserDetailsChecker implements UserDetailsChecker {
	@Override
	public void check(UserDetails user) {
		if (user instanceof ExtendedUserDetails) {
			ExtendedUserDetails details = (ExtendedUserDetails)user;
			if (details.isInternal())
				throw new SessionAuthenticationException("Invalid user status");
		}
		super.check(user);
	}
}
