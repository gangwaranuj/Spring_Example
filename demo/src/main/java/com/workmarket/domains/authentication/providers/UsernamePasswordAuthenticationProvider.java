package com.workmarket.domains.authentication.providers;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.domains.authentication.model.WebAuthenticationDetailsWrapper;
import com.workmarket.domains.model.User;
import com.workmarket.service.exception.authentication.EmailNotConfirmedException;
import com.workmarket.service.exception.authentication.InetAddressNotAuthorizedException;
import com.workmarket.service.exception.authentication.InvalidGoogleRecaptchResponseException;
import com.workmarket.service.external.GoogleRecaptchaAdapter;
import com.workmarket.service.external.vo.GoogleRecaptchaResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.InetAddressUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class UsernamePasswordAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	
	@Autowired private AuthenticationService authn;
	@Autowired private AuthorizationService authz;
	@Autowired private MetricRegistry registry;
	@Autowired private GoogleRecaptchaAdapter googleRecaptchaAdapter;

	private UserDetailsService userDetailsService;

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		String principal = (String)authentication.getPrincipal();
		String credentials = (String)authentication.getCredentials();
		String inetAddress = null;
		String sessionId = null;
		String gRecaptchaUserResponse = null;
		boolean isRecaptchaExcluded = false;
		if (authentication.getDetails() instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();
			inetAddress = InetAddressUtilities.parseInetAddress(details.getRemoteAddress());
			sessionId = details.getSessionId();
			if (authentication.getDetails() instanceof WebAuthenticationDetailsWrapper) {
				WebAuthenticationDetailsWrapper webAuthenticationDetailsWrapper = (WebAuthenticationDetailsWrapper)authentication.getDetails();
				gRecaptchaUserResponse = webAuthenticationDetailsWrapper.getRecaptchaUserResponse();
				isRecaptchaExcluded = webAuthenticationDetailsWrapper.isRecaptchaExcluded();
			}
		}

		registry.meter("auth.userpassword").mark();

		User user = authn.auth(principal, credentials, inetAddress, sessionId, gRecaptchaUserResponse, isRecaptchaExcluded);
		if (user == null) {
			throw new BadCredentialsException(messages.getMessage("auth.usernamePassword.invalid"));
		}

		if (!authn.getEmailConfirmed(user))
			throw new EmailNotConfirmedException(messages.getMessage("auth.usernamePassword.confirmEmail")).setUserNumber(user.getUserNumber());
		if (authn.isDeactivated(user))
			throw new DisabledException(messages.getMessage("auth.usernamePassword.deactivated"));
		if (authn.isSuspended(user))
			throw new LockedException(messages.getMessage("auth.usernamePassword.suspended"));
		if (authn.isLocked(user))
			throw new LockedException(messages.getMessage("auth.usernamePassword.locked"));

		boolean recaptchaEnabled = authn.isRecaptchaEnabledOnUser(user) && !isRecaptchaExcluded;
		if (recaptchaEnabled || (gRecaptchaUserResponse != null && isEmpty(gRecaptchaUserResponse))) {
			throw new InvalidGoogleRecaptchResponseException(messages.getMessage("auth.recaptcha.required")).setUserName(user.getEmail());
		}

		if (isNotBlank(gRecaptchaUserResponse) && !authentication.isAuthenticated()) {
			GoogleRecaptchaResponse recaptchaResponse = googleRecaptchaAdapter.verify(gRecaptchaUserResponse);
			if (!recaptchaResponse.isSuccess()) {
				authn.reportRecaptchaFailure(user, inetAddress);
				throw new InvalidGoogleRecaptchResponseException(messages.getMessage("auth.recaptcha.invalid")).setUserName(user.getEmail());
			}
		}

		if (!authz.authorizeByInetAddress(user, inetAddress))
			throw new InetAddressNotAuthorizedException(messages.getMessage("auth.usernamePassword.invalidIp", CollectionUtilities.newArray(inetAddress)));

		authn.setCurrentUser(user);
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		return userDetailsService.loadUserByUsername(username);
	}
	
	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		return super.authenticate(authentication);
	}
}
