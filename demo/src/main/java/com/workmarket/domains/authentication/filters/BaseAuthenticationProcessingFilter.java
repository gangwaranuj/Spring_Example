package com.workmarket.domains.authentication.filters;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: alexsilva Date: 11/28/14 Time: 3:38 PM
 */
public class BaseAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	protected static final String DEFAULT_FILTER_PROCESSES_PATTERN = "/login|/worker/v2/signin|/v2/worker/signin";
	protected static final String SPRING_SECURITY_FORM_USERNAME_KEY = "userEmail";
	protected static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
	protected static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";

	public BaseAuthenticationProcessingFilter() {
		super(new RegexRequestMatcher(DEFAULT_FILTER_PROCESSES_PATTERN, null));
	}

	public BaseAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
		super(new RegexRequestMatcher(defaultFilterProcessesUrl, null));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
		return null;
	}
}
