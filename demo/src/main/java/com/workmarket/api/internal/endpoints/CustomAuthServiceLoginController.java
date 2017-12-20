package com.workmarket.api.internal.endpoints;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.api.vo.Response;
import com.workmarket.domains.authentication.model.AuthServiceAuthenticationToken;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.User;
import com.workmarket.service.infra.business.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Basically a way to get session cookie without doing anything else.
 */
@Controller
@RequestMapping("/v2/internal/auth")
public class CustomAuthServiceLoginController {
	@Autowired
	SessionRegistryImpl sessionRegistry;
	@Autowired
	ExtendedUserDetailsService euds;
	@Autowired
	AuthenticationService authservice;

	@RequestMapping(
		value="/authSession/{sessionId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public Response<Boolean> auth(
			final HttpServletRequest request,
			@PathVariable(value = "sessionId") final String sessionId) throws IOException {
		final User user = authservice.findUserByJsessionId(sessionId);
		if (user == null) {
			return Response.valueWithMessageAndResults("not logged in", ImmutableList.of(false));
		}
		final UserDetails details = euds.loadUser(user);

		// Authenticate the user -- will blow an exception if not authorized
		final AuthServiceAuthenticationToken auth = new AuthServiceAuthenticationToken(details);
		auth.setAuthenticated(true);
		sessionRegistry.registerNewSession(sessionId, auth);
		// Create a new session and add the security context.
		final HttpSession session = request.getSession(true);

		final SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(auth);
		session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

		return Response.valueWithMessageAndResults("success", ImmutableList.of(true));
	}

	@RequestMapping(
			value="/logout",
			method = POST,
			produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public Response<Boolean> logout(
			final HttpServletRequest request) throws ServletException {
		request.logout();
		return Response.valueWithMessageAndResults("success", ImmutableList.of(true));
	}
}
