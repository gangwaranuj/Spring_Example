package com.workmarket.service.web;

import com.google.common.collect.ImmutableList;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by nick on 7/12/13 11:48 AM
 */
public interface CSRFTokenService {

	public final static List<String> METHODS_TO_CHECK = ImmutableList.of("POST", "PUT", "DELETE");
	public final static String TOKEN_PARAMETER_NAME = "_tk";
	public final static String TOKEN_ATTRIBUTE_NAME = "CSRFToken";
	public final static String TOKEN_HEADER_NAME = "X-CSRF-Token";
	/**
	 * Generates a new CSRF Protection token
	 */
	public String generateToken();

	/**
	 * Obtains the token from the session. If there is no token, a new one will be generated.
	 */
	public String getTokenFromSession(final HttpServletRequest request);

	/**
	 * This version can shut off check for request.userPrincipal, to support API production of
	 * CSRF token on login, which is an unsecured resource (so no user Principle available)
	 */
	public String getTokenFromSession(final HttpServletRequest request, boolean checkForRequestUser);

	/**
	 * Returns true if a token is valid on a request
	 */
	public boolean acceptsTokenIn(HttpServletRequest request);
}
