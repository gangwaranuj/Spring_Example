package com.workmarket.domains.authentication.filters;

import com.workmarket.auth.AuthenticationClient;
import com.workmarket.service.infra.business.AuthTrialCommon;
import com.workmarket.service.web.WebRequestContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter to make sure that the auth service knows when people log out.
 */
public class CustomAuthServiceLogoutHandler implements LogoutHandler {
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired AuthTrialCommon common;
	@Autowired AuthenticationClient client;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		final HttpSession session = (request).getSession(false);
		// We should have one normally, but you never know
		if (session == null) {
			return;
		}
		final String sessionId = session.getId();
		client.logout(sessionId, common.getApiContext()).toBlocking().single();
	}
}
