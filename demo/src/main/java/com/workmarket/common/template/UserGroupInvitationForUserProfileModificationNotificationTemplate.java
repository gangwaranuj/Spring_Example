package com.workmarket.common.template;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class UserGroupInvitationForUserProfileModificationNotificationTemplate extends AbstractUserGroupNotificationTemplate {
	/**
	 *
	 */
	private static final long serialVersionUID = -2030171994302078824L;

	public UserGroupInvitationForUserProfileModificationNotificationTemplate(Long fromId, Long toId, UserGroup group, Company company) {
		super(fromId, toId, new NotificationType(NotificationType.GROUP_INVITED_PROFILE_MODIFIED), ReplyToType.TRANSACTIONAL_FROM_COMPANY, group, company);
	}
}
