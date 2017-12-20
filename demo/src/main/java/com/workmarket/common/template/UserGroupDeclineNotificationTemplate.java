package com.workmarket.common.template;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class UserGroupDeclineNotificationTemplate extends AbstractUserGroupNotificationTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = -959959406237904002L;

	public UserGroupDeclineNotificationTemplate(Long fromId, Long toId, UserGroup group, Company company) {
		super(fromId, toId, new NotificationType(NotificationType.GROUP_DECLINED), ReplyToType.TRANSACTIONAL_FROM_COMPANY, group, company);
	}
}
