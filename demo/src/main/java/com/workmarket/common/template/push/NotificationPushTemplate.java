package com.workmarket.common.template.push;

import com.workmarket.common.template.NotificationModel;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.UserNotification;
import com.workmarket.domains.model.notification.NotificationType;

/**
 * User: andrew
 * Date: 11/19/13
 */
public class NotificationPushTemplate extends PushTemplate implements NotificationModel, UserNotification {

	private static final long serialVersionUID = 1L;

	private final String templateTemplatePath;
	private final String templateTemplate;
	private final String headerTemplatePath;
	private final String footerTemplatePath;

	private final NotificationTemplate parent;

	public NotificationPushTemplate(Long fromId, Long toId, NotificationType notificationType, NotificationTemplate parent) {
		super(fromId, toId, notificationType);
		this.parent = parent;
		this.templateTemplatePath = makePushTemplatePath(
			canonicalizeClassName(this.parent.getClass().getSimpleName())
				.replace("NotificationTemplate", "PushTemplate"));
		this.templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
		this.headerTemplatePath = makePushTemplatePath(getHeaderTemplate());
		this.footerTemplatePath = makePushTemplatePath(getFooterTemplate());
	}

	public String getHeaderTemplate() {
		return PUSH_HEADER_TEMPLATE;
	}

	public String getTemplateTemplate() {
		return templateTemplate;
	}

	public String getFooterTemplate() {
		return PUSH_FOOTER_TEMPLATE;
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

	static String makePushTemplatePath(final String pathName) {
		return PUSH_TEMPLATE_DIRECTORY_PATH +
			"/" +
			pathName +
			PUSH_TEMPLATE_EXTENSION;
	}
}
