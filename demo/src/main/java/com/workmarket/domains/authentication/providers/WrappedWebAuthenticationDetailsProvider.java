package com.workmarket.domains.authentication.providers;

import com.workmarket.domains.authentication.model.WebAuthenticationDetailsWrapper;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Custom implementation of {@link AuthenticationDetailsSource} that returns
 * a wrapped {@link WebAuthenticationDetails} object which uses an alternate method
 * to initialize its values from the originating HTTP request.
 *
 * See {@link org.springframework.security.web.authentication.WebAuthenticationDetailsSource} for reference.
 */
public class WrappedWebAuthenticationDetailsProvider implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {
	@Override
	public WebAuthenticationDetails buildDetails(HttpServletRequest httpRequest) {
		return new WebAuthenticationDetailsWrapper(httpRequest);
	}
}
