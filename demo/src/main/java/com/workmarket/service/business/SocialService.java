package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import org.springframework.security.core.AuthenticationException;
import org.springframework.social.connect.ConnectionKey;

import javax.servlet.http.HttpServletRequest;

/**
 * User: micah
 * Date: 3/16/13
 * Time: 11:50 PM
 */
public interface SocialService {
	public String processSocialNoLink(HttpServletRequest request);

	public User findUserBySocialKey(ConnectionKey key);

	public void processSocialException(
		HttpServletRequest request, AuthenticationException ae
	);

	public String processSocialSignup(HttpServletRequest request);
}
