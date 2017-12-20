package com.workmarket.common.template.email;

import com.workmarket.group.UserGroupExpiration;
import com.workmarket.configuration.Constants;

/**
 * User: micah
 * Date: 1/17/14
 * Time: 1:58 PM
 */
public class UserGroupExpirationEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = 7921155190877556244L;
	UserGroupExpiration userGroupExpiration;

	public UserGroupExpirationEmailTemplate(UserGroupExpiration userGroupExpiration) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, userGroupExpiration.getUserGroup().getOwner().getEmail());
		this.userGroupExpiration = userGroupExpiration;
	}

	public UserGroupExpiration getUserGroupExpiration() {
		return userGroupExpiration;
	}
}
