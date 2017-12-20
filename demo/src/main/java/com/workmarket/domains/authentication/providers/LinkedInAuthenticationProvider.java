package com.workmarket.domains.authentication.providers;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.LinkedInResult;
import com.workmarket.service.business.LinkedInService;
import com.workmarket.service.exception.authentication.EmailNotConfirmedException;
import com.workmarket.service.exception.authentication.InetAddressNotAuthorizedException;
import com.workmarket.service.exception.authentication.LinkedInUserNotFoundException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.InetAddressUtilities;
import com.workmarket.domains.authentication.model.SocialAuthenticationToken;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * User: micah
 * Date: 2/19/13
 * Time: 10:48 AM
 */
public class LinkedInAuthenticationProvider
	implements AuthenticationProvider, InitializingBean, MessageSourceAware
{
	@Autowired private AuthenticationService authn;
	@Autowired private AuthorizationService authz;
	@Autowired private LinkedInService linkedInService;
	@Autowired private MetricRegistry registry;

	protected MessageSourceAccessor messages =
		SpringSecurityMessageSource.getAccessor();

	private UserDetailsService userDetailsService;

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return (SocialAuthenticationToken.class.isAssignableFrom(authentication));
	}

	protected void additionalAuthenticationChecks(
		SocialAuthenticationToken authentication)
		throws AuthenticationException
	{
		String inetAddress = null;
		if (authentication.getDetails() instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details =
				(WebAuthenticationDetails)authentication.getDetails();
			inetAddress =
				InetAddressUtilities.parseInetAddress(details.getRemoteAddress());
		}
		User user = authn.authLinkedIn(
			((UserDetails)authentication.getPrincipal()).getUsername(),
			inetAddress
		);

		if (user == null)
			throw new BadCredentialsException(
				messages.getMessage("auth.usernamePassword.invalid")
			);

		if (!authn.getEmailConfirmed(user))
			throw new EmailNotConfirmedException(
				messages.getMessage("auth.usernamePassword.confirmEmail")
			).setUserNumber(user.getUserNumber());

		if (authn.isDeactivated(user))
			throw new DisabledException(
				messages.getMessage("auth.usernamePassword.deactivated")
			);

		if (authn.isSuspended(user))
			throw new LockedException(
				messages.getMessage("auth.usernamePassword.suspended")
			);

		if (authn.isLocked(user))
			throw new LockedException(
				messages.getMessage("auth.usernamePassword.locked")
			);

		if (!authz.authorizeByInetAddress(user, inetAddress))
			throw new InetAddressNotAuthorizedException(
				messages.getMessage("auth.usernamePassword.invalidIp",
					CollectionUtilities.newArray(inetAddress))
			);

		authn.setCurrentUser(user);
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		// TODO: don't like the exception here. maybe handle with object?
		// Also, need to get back the LinkedIn info in case we need to
		// redirect for link.
		registry.meter("auth.linkedin").mark();
		LinkedInResult linkedInResult = linkedInService.findUsernameByOAuth(
			(String)authentication.getCredentials()
		);
		if (linkedInResult.getStatus().equals(LinkedInResult.Status.ERROR)) {
			throw new BadCredentialsException(
				messages.getMessage("auth.linkedIn.apiError")
			);
		}
		else if (linkedInResult.getStatus().equals(LinkedInResult.Status.FAILURE)) {
			throw new LinkedInUserNotFoundException(messages.getMessage(
				"auth.linkedIn.notLinked",
				CollectionUtilities.newArray(linkedInResult.getLinkedInEmail()))
			).setLinkedInResult(linkedInResult);
		}
		UserDetails userDetails =
			userDetailsService.loadUserByUsername(linkedInResult.getUserEmail());
		SocialAuthenticationToken socialAuthenticationToken =
			new SocialAuthenticationToken(userDetails);
		socialAuthenticationToken.setAuthenticated(true);
		additionalAuthenticationChecks(socialAuthenticationToken);
		return socialAuthenticationToken;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}
}
