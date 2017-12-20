package com.workmarket.domains.authentication.services;

import com.workmarket.domains.authentication.providers.SocialAuthenticationProvider;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.SocialService;
import com.workmarket.domains.authentication.model.SocialAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.TextEscapeUtils;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * User: micah
 * Date: 3/14/13
 * Time: 1:35 PM
 *
 * We only get here once the user has authenticated with Social AND we have
 * matched their email address to a WM user account.
 */
@Component
public class SocialSignInAdapter implements SignInAdapter {

	@Autowired private SocialAuthenticationProvider authenticationManager;
	@Autowired private SocialService socialService;

	private AuthenticationDetailsSource authenticationDetailsSource =
		new WebAuthenticationDetailsSource();
	private static final String SPRING_SECURITY_LAST_EXCEPTION ="SPRING_SECURITY_LAST_EXCEPTION";


	private String handleFacebook(
		ConnectionKey key, Facebook api, HttpServletRequest request
	) {
		// First, see if we already have a social user connection record
		User user = socialService.findUserBySocialKey(key);
		String email = (user != null) ?
			user.getEmail() :
			api.userOperations().getUserProfile().getEmail();

		HttpSession session = request.getSession(false);

		Authentication authentication =
			new UsernamePasswordAuthenticationToken(email, null);

		try {
			authentication = authenticationManager.authenticate(authentication);
		}
		catch (AuthenticationException ae) {
			socialService.processSocialException(request, ae);
			if (session != null) { request.getSession().setAttribute(SPRING_SECURITY_LAST_EXCEPTION, ae); }
			return "/login?error";
		}

		if (session != null) {
			request.getSession().setAttribute(
					"SPRING_SECURITY_LAST_USERNAME",
					TextEscapeUtils.escapeEntities(
							((UserDetails) authentication.getPrincipal()).getUsername()
					)
			);
		}

		// Allow subclasses to set the "details" property
		((SocialAuthenticationToken)authentication).
				setDetails(authenticationDetailsSource.buildDetails(request));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "/home";
	}

	private String handleGoogle(
			ConnectionKey key, Google api, HttpServletRequest request
	) {
		User user = socialService.findUserBySocialKey(key);
		String email = (user != null) ?
					user.getEmail() :
					api.plusOperations().getGoogleProfile().getAccountEmail();

		HttpSession session = request.getSession(false);

		Authentication authentication = new UsernamePasswordAuthenticationToken(email, null);

		try {
			authentication = authenticationManager.authenticate(authentication);
		} catch (AuthenticationException ae) {
			socialService.processSocialException(request, ae);
			if(session != null) {
				session.setAttribute(SPRING_SECURITY_LAST_EXCEPTION, ae);
			}
			return "/login?error";
		}

		if (session != null) {
			request.getSession().setAttribute("SPRING_SECURITY_LAST_USERNAME",
					TextEscapeUtils.escapeEntities(((UserDetails) authentication.getPrincipal()).getUsername()));
		}

		((SocialAuthenticationToken) authentication).
				setDetails(authenticationDetailsSource.buildDetails(request));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "/home";
	}

	@Override
	public String signIn(
		String s, Connection<?> connection, NativeWebRequest nativeWebRequest
	) {
		HttpServletRequest request =
				nativeWebRequest.getNativeRequest(HttpServletRequest.class);

		Object apiObject = connection.getApi();

		if (apiObject instanceof Facebook) {
			return handleFacebook(connection.getKey(), (Facebook)apiObject, request);
		} else if (apiObject instanceof Google) {
			return handleGoogle(connection.getKey(), (Google)apiObject, request);
		}
		return null;
	}
}