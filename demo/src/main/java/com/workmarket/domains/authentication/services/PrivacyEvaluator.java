package com.workmarket.domains.authentication.services;

import org.springframework.security.core.Authentication;

/**
 * User: micah
 * Date: 5/7/13
 * Time: 9:47 AM
 */
public interface PrivacyEvaluator {
	public boolean isProtected(Authentication authentication, String profileProperty);
}
