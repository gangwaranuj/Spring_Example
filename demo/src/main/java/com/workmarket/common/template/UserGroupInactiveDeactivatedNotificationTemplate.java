package com.workmarket.common.template;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class UserGroupInactiveDeactivatedNotificationTemplate extends AbstractUserGroupNotificationTemplate {

	public UserGroupInactiveDeactivatedNotificationTemplate(long toId, UserGroup group) {

		super(
			Constants.EMAIL_USER_ID_TRANSACTIONAL,
			toId,
			new NotificationType(NotificationType.GROUP_INACTIVE_DEACTIVATED),
			ReplyToType.TRANSACTIONAL_FROM_COMPANY,
			group
		);
	}
}
