package com.workmarket.service.exception.authentication;

import org.springframework.security.core.AuthenticationException;

/**
 * User: micah
 * Date: 3/17/13
 * Time: 1:02 AM
 */
public class SocialUserNotFoundException extends AuthenticationException {
	private String socialId;

	public SocialUserNotFoundException(String message) {
		super(message);
	}

	public String getSocialId() {
		return this.socialId;
	}

	public AuthenticationException setSocialId(String socialId) {
		this.socialId = socialId;
		return this;
	}
}
