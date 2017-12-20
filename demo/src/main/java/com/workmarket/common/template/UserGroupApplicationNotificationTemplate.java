package com.workmarket.common.template;

import com.workmarket.domains.model.User;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class UserGroupApplicationNotificationTemplate extends AbstractUserGroupNotificationTemplate {
	/**
	 *
	 */
	private static final long serialVersionUID = -1998983015737344750L;
	private User resource;
	private boolean hasRequirements;
	private boolean overrideRequested;

	public UserGroupApplicationNotificationTemplate(Long toId, UserGroup group, User resource, boolean overrideRequested) {
		super(resource.getId(), toId, new NotificationType(NotificationType.GROUP_APPLY), ReplyToType.TRANSACTIONAL_FROM_USER, group);
		this.resource = resource;
		this.overrideRequested = overrideRequested;
		this.hasRequirements =
			group.getRequirementSet() != null &&
			group.getRequirementSet().getRequirements() != null &&
			!group.getRequirementSet().getRequirements().isEmpty();
	}

	public User getResource() {
		return resource;
	}

	public Boolean getOverrideRequested() {
		return overrideRequested;
	}

	public Boolean getHasRequirements() {
		return hasRequirements;
	}
}
