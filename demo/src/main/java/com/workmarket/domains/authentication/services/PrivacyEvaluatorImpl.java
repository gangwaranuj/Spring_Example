package com.workmarket.domains.authentication.services;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

/**
 * User: micah
 * Date: 5/7/13
 * Time: 9:48 AM
 */
public class PrivacyEvaluatorImpl implements PrivacyEvaluator {
	@Autowired UserService userService;

	@Override
	public boolean isProtected(Authentication authentication, String profileProperty) {
		if (!(authentication.getPrincipal() instanceof ExtendedUserDetails))
			return false;

		ExtendedUserDetails user = (ExtendedUserDetails)authentication.getPrincipal();
		User myUser = userService.getUser(user.getId());

		return false;
	}
}
