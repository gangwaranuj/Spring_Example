package com.workmarket.common.template;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class UserGroupMessageNotificationTemplate extends AbstractUserGroupNotificationTemplate {
	/**
	 *
	 */
	private static final long serialVersionUID = 8680437331547421961L;
	private String message;
	private String title;

	public UserGroupMessageNotificationTemplate(Long fromId, Long toId, String message, String title, UserGroup group, Company company) {
		super(fromId, toId, new NotificationType(NotificationType.GROUP_MESSAGE), ReplyToType.USER, group, company);
		setFromId(fromId);
		this.message = message;
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}
}
