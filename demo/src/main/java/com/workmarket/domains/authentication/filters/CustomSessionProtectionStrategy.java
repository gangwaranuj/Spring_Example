package com.workmarket.domains.authentication.filters;

import com.workmarket.auth.AuthenticationClient;
import com.workmarket.service.infra.business.AuthTrialCommon;
import com.workmarket.service.web.WebRequestContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Because of the session protection strategy, we have to tell the auth service about the change.  Here, we just
 * wrap the original one.
 */
public class CustomSessionProtectionStrategy implements SessionAuthenticationStrategy, ApplicationEventPublisherAware {
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired SessionFixationProtectionStrategy strategy;
	@Autowired AuthTrialCommon trialCommon;
	@Autowired AuthenticationClient authenticationClient;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		strategy.setApplicationEventPublisher(applicationEventPublisher);
	}

	public void setStrategy(SessionFixationProtectionStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public void onAuthentication(
			final Authentication auth,
			final HttpServletRequest request,
			final HttpServletResponse response) throws SessionAuthenticationException {
		final HttpSession session = request.getSession(false);
		final String oldSessionId;
		if (session != null) {
			oldSessionId = session.getId();
		} else {
			oldSessionId = null;
		}

		strategy.onAuthentication(auth, request, response);
		final String newSessionId = request.getSession().getId();

		if (!newSessionId.equals(oldSessionId) && oldSessionId != null) {
			authenticationClient
				.changeSession(oldSessionId, newSessionId, trialCommon.getApiContext())
				.toBlocking().single();
		}
	}
}
