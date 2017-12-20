package com.workmarket.common.template.notification;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.common.template.NotificationModel;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.UserNotification;
import com.workmarket.configuration.Constants;

public class NotificationUserNotificationTemplate extends UserNotificationTemplate implements NotificationModel, UserNotification {

	private static final long serialVersionUID = -4579368670274172398L;
	private final String templateTemplateName;
	private final String headerTemplatePath;
	private final String footerTemplatePath;
	private final String templateTemplatePath;

	private final NotificationTemplate parent;

	public NotificationUserNotificationTemplate(Long toId, Long fromId, NotificationType notificationType, boolean sticky, NotificationTemplate parent) {
		super(toId, fromId, notificationType, sticky);
		this.parent = parent;
		this.templateTemplatePath = makeNotificationTemplatePath(
			canonicalizeClassName(this.parent.getClass().getSimpleName())
				.replace("NotificationTemplate", "UserNotificationTemplate"));
		this.templateTemplateName = canonicalizeClassName(this.getClass().getSimpleName());
		this.headerTemplatePath = makeNotificationTemplatePath(getHeaderTemplate());
		this.footerTemplatePath = makeNotificationTemplatePath(getFooterTemplate());
	}

	public String getHeaderTemplate() {
		return Constants.USER_NOTIFICATION_HEADER_TEMPLATE;
	}

	public String getTemplateTemplate() {
		return templateTemplateName;
	}

	public String getFooterTemplate() {
		return Constants.USER_NOTIFICATION_FOOTER_TEMPLATE;
	}

	public String getHeaderTemplatePath() {
		return headerTemplatePath;
	}

	public String getTemplateTemplatePath() {
		return templateTemplatePath;
	}

	public String getFooterTemplatePath() {
		return footerTemplatePath;
	}

	@Override
	public Object getModel() {
		return parent;
	}
}
