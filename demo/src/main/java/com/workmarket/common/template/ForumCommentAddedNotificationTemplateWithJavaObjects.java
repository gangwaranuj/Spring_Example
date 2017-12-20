package com.workmarket.common.template;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class ForumCommentAddedNotificationTemplateWithJavaObjects extends NotificationTemplate {
	private static final NotificationType NOTIFICATION_TYPE = new NotificationType(NotificationType.FORUM_POST_COMMENT_ADDED);

	private ForumPost comment;
	private ForumPost parent;

	protected ForumCommentAddedNotificationTemplateWithJavaObjects(final Long toId, final ForumPost comment, final ForumPost parent, final String creatorFullName) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, NOTIFICATION_TYPE, ReplyToType.TRANSACTIONAL, creatorFullName);
		this.setComment(comment);
		this.setParent(parent);
	}

	public ForumPost getComment() {
		return comment;
	}

	public void setComment(final ForumPost comment) {
		this.comment = comment;
	}

	public ForumPost getParent() {
		return parent;
	}

	public void setParent(final ForumPost parent) {
		this.parent = parent;
	}
}
