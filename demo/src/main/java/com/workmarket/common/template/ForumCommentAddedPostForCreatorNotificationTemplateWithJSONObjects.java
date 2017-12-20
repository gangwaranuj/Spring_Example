package com.workmarket.common.template;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

import java.util.Map;

public class ForumCommentAddedPostForCreatorNotificationTemplateWithJSONObjects extends NotificationTemplate {
    private static final NotificationType NOTIFICATION_TYPE = new NotificationType(NotificationType.FORUM_POST_COMMENT_ADDED);

    private Map<String, Object> comment;
    private Map<String, Object> parent;

    protected ForumCommentAddedPostForCreatorNotificationTemplateWithJSONObjects(final Long toId, final Map<String, Object> comment, final Map<String, Object> parent, final String creatorFullName) {
        super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, NOTIFICATION_TYPE, ReplyToType.TRANSACTIONAL, creatorFullName);
        this.setComment(comment);
        this.setParent(parent);
    }

    public Map<String, Object> getComment() {
        return comment;
    }

    public void setComment(final Map<String, Object> comment) {
        this.comment = comment;
    }

    public Map<String, Object> getParent() {
        return parent;
    }

    public void setParent(final Map<String, Object> parent) {
        this.parent = parent;
    }
}
