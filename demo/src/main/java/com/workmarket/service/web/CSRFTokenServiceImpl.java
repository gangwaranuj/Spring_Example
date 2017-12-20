package com.workmarket.service.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by nick on 7/12/13 11:50 AM
 */
@Service("csrfTokenService")
public class CSRFTokenServiceImpl implements CSRFTokenService {

	private final SecureRandom random = new SecureRandom();

	@Override
	public String generateToken() {
		final byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		return Base64.encodeBase64URLSafeString(bytes);
	}

	@Override
	public String getTokenFromSession(HttpServletRequest request) {
		return getTokenFromSession(request, true);
	}

	@Override
	public String getTokenFromSession(final HttpServletRequest request, boolean checkForRequestUser) {
		return (checkForRequestUser && request.getUserPrincipal() == null) ? null
				: getTokenFromSession(request.getSession(false));
	}

	@Override
	public boolean acceptsTokenIn(HttpServletRequest request) {

		if (request.getUserPrincipal() == null)
			return true;

		final HttpSession session = request.getSession(false);
		String sessionToken = getTokenFromSession(session);
		String requestToken = getTokenFromRequest(request);
		return session != null && sessionToken.equals(requestToken);
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		return StringUtils.defaultString(
				request.getParameter(TOKEN_PARAMETER_NAME),
				request.getHeader(TOKEN_HEADER_NAME)); // also allow it to be sent as a header (AJAX)
	}

	private String getTokenFromSession(final HttpSession session) {
		String token = null;

		if (session != null) {
			token = (String) session.getAttribute(TOKEN_ATTRIBUTE_NAME);
			if (isBlank(token)) {
				token = generateToken();
				session.setAttribute(TOKEN_ATTRIBUTE_NAME, token);
			}
		}
		return token;
	}
}
