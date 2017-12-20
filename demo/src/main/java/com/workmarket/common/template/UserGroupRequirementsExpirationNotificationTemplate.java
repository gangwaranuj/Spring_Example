package com.workmarket.common.template;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class UserGroupRequirementsExpirationNotificationTemplate extends AbstractUserGroupNotificationTemplate {
	public UserGroupRequirementsExpirationNotificationTemplate(Long fromId, Long toId, UserGroup group, Company company) {
		super(fromId, toId, new NotificationType(NotificationType.GROUP_INVITED_REQUIREMENTS_EXPIRED), ReplyToType.TRANSACTIONAL_FROM_COMPANY, group, company);
	}
}
