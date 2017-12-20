package com.workmarket.common.template;

import java.util.List;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class ProfileCompletenessNotificationTemplate extends NotificationTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4942936353772489547L;
	private String message;
	private List<String> description;

	public ProfileCompletenessNotificationTemplate(Long fromId, Long toId, String message, List<String> description) {
		super(fromId, toId, new NotificationType(NotificationType.PROFILE_COMPLETENESS), ReplyToType.TRANSACTIONAL_FROM_USER);
		this.message = message;
		this.description = description;
	}

	public String getMessage() {
		return message;
	}

	public List<String> getDescription() {
		return description;
	}
}