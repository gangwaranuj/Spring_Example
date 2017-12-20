package com.workmarket.domains.authentication.providers;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.User;
import com.workmarket.service.exception.authentication.EmailNotConfirmedException;
import com.workmarket.service.exception.authentication.InetAddressNotAuthorizedException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.InetAddressUtilities;
import com.workmarket.domains.authentication.model.SocialAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: micah
 * Date: 3/15/13
 * Time: 4:31 PM
 */
@Component
@Transactional
public class SocialAuthenticationProvider
	implements MessageSourceAware
{
	@Autowired private AuthenticationService authn;
	@Autowired private AuthorizationService authz;
	@Autowired private ExtendedUserDetailsService userDetailsService;
	@Autowired private MetricRegistry registry;

	protected MessageSourceAccessor messages =
		SpringSecurityMessageSource.getAccessor();


	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}

	private void additionalAuthenticationChecks(Authentication authentication) {
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
			throw new InetAddressNotAuthorizedException(messages.getMessage(
				"auth.usernamePassword.invalidIp",
				CollectionUtilities.newArray(inetAddress)
			));

		authn.setCurrentUser(user);
	}

	public Authentication authenticate(Authentication authentication) {
		registry.meter("auth.social").mark();
		UserDetails userDetails = userDetailsService.
			loadUserByUsername((String)authentication.getPrincipal());
		SocialAuthenticationToken authToken =
			new SocialAuthenticationToken(userDetails);
		authToken.setAuthenticated(true);

		additionalAuthenticationChecks(authToken);

		return authToken;
	}
}
