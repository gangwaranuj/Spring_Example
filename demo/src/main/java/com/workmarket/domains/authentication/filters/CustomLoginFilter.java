package com.workmarket.domains.authentication.filters;

import com.workmarket.domains.authentication.providers.UsernamePasswordAuthenticationProvider;
import com.workmarket.domains.authentication.model.WorkmarketAuthentication;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.TextEscapeUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class CustomLoginFilter extends BaseAuthenticationProcessingFilter implements Serializable {

	@Autowired UsernamePasswordAuthenticationProvider authenticationManager;

	private static final long serialVersionUID = -9047394725295219543L;
	private static final String POST = "POST";
  	private static final String OPTIONS = "OPTIONS";
	private static final String[] excludeCaptchaPaths = { "/v2/worker/signin" , "/worker/v2/signin" };

	public CustomLoginFilter () {
		super();
	}

	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// copied from Spring implementation
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		String username = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);

		if (username == null) {
			username = "";
		}
		if (password == null) {
			password = "";
		}

		username = username.trim();

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

		// Place the last username attempted into HttpSession for views
		HttpSession session = request.getSession(false);

		if (session != null || getAllowSessionCreation()) {
			request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextEscapeUtils.escapeEntities(username));
			if (StringUtils.isNotBlank(request.getParameter("linkedInId"))) {
				request.getSession().setAttribute("linkedInId", request.getParameter("linkedInId"));
			}
			else if (StringUtils.isNotBlank(request.getParameter("socialId"))) {
				request.getSession().setAttribute("socialId", request.getParameter("socialId"));
			}
		}

		request.getSession().setAttribute("recaptchaExcluded", Arrays.asList(excludeCaptchaPaths).contains(request.getRequestURI()));

		// Allow subclasses to set the "details" property
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
		Authentication a = authenticationManager.authenticate(authRequest);

		return new WorkmarketAuthentication(a.getAuthorities(), a.getDetails(), a.getPrincipal(), a.getName(), a.isAuthenticated());
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		if(request.getMethod().equals(POST) || request.getMethod().equals(OPTIONS)) {
			// If the incoming request is a POST or OPTIONS, then we send it up
			// to the AbstractAuthenticationProcessingFilter.
			super.doFilter(request, response, chain);
		} else {
			// If it's a GET, we ignore this request and send it
			// to the next filter in the chain.  In this case, that
			// pretty much means the request will hit the /login
			// controller which will process the request to show the
			// login page.
			chain.doFilter(request, response);
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && getClass() == obj.getClass();
	}

}
