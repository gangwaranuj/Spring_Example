package com.workmarket.domains.authentication.filters;

import com.workmarket.domains.authentication.providers.LinkedInAuthenticationProvider;
import com.workmarket.domains.authentication.model.SocialAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.TextEscapeUtils;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import com.workmarket.service.business.LinkedInService;

public class CustomLinkedInLoginFilter extends BaseAuthenticationProcessingFilter {

	@Autowired LinkedInService linkedInService;
	@Autowired LinkedInAuthenticationProvider authenticationManager;

	public CustomLinkedInLoginFilter() {
		super("/social/login/linkedin_finish");
	}

	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest request,
			HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException
	{
		// copied from Spring implementation
		if (!request.getMethod().equals("GET")) {
			throw new AuthenticationServiceException(
				"Authentication method not supported: " + request.getMethod());
		}

		String oauthToken = (request.getParameter("oauth_token") != null) ?
			request.getParameter("oauth_token").trim() :
			"";

		SocialAuthenticationToken authRequest =
			new SocialAuthenticationToken(oauthToken);

		Authentication authentication =
			authenticationManager.authenticate(authRequest);

		// Place the last username attempted into HttpSession for views
		HttpSession session = request.getSession(false);

		// TODO: need to refactor based on failed auth scenarios
		if (
			authentication.isAuthenticated() &&
			(session != null || getAllowSessionCreation())
		) {
			request.getSession().setAttribute(
				SPRING_SECURITY_LAST_USERNAME_KEY,
				TextEscapeUtils.escapeEntities(
					((UserDetails)authentication.getPrincipal()).getUsername()
				)
			);
		}

		// Allow subclasses to set the "details" property
		((SocialAuthenticationToken)authentication).setDetails(authenticationDetailsSource.buildDetails(request));

		return authentication;
	}

	@Override
	public void doFilter(
		ServletRequest req,
		ServletResponse res,
	  FilterChain chain)
		throws IOException, ServletException
	{
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

		if(request.getMethod().equals("GET")) {
			// If the incoming request is a GET, then we send it up
			// to the AbstractAuthenticationProcessingFilter.
			super.doFilter(request, response, chain);
		} else {
			// If it's a POST, we ignore this request and send it
			// to the next filter in the chain.  In this case, that
			// pretty much means the request will hit the /login
			// controller which will process the request to show the
			// login page.
			chain.doFilter(request, response);
		}
	}
}