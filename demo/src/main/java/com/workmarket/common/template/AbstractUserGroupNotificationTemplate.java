package com.workmarket.common.template;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;
import org.apache.commons.lang3.StringEscapeUtils;

public class AbstractUserGroupNotificationTemplate extends NotificationTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = 509415916190878727L;
	private UserGroup userGroup;
	private Company company;
	private String userGroupName;

	protected AbstractUserGroupNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, UserGroup group, Company company) {
		super(fromId, toId, notificationType, replyToType);
		this.userGroup = group;
		this.company = company;
		this.userGroupName = StringEscapeUtils.unescapeHtml4(group.getName());
	}

	protected AbstractUserGroupNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, UserGroup group) {
		super(fromId, toId, notificationType, replyToType);
		this.userGroup = group;
		this.userGroupName = StringEscapeUtils.unescapeHtml4(group.getName());
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public Company getCompany() {
		return company;
	}

	public String getUserGroupName() {
		return userGroupName;
	}
}
