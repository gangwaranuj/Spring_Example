package com.workmarket.common.template.voice;

import com.workmarket.common.template.NotificationModel;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.UserNotification;
import com.workmarket.domains.model.notification.NotificationType;

public class NotificationVoiceTemplate extends VoiceTemplate implements NotificationModel, UserNotification {
	/**
	 *
	 */
	private static final long serialVersionUID = 6312936723373528264L;

	private final String templateTemplate;

	private NotificationTemplate parent;

	public NotificationVoiceTemplate(Long fromId, Long toId, NotificationType notificationType, NotificationTemplate parent) {
		super(fromId, toId, notificationType);
		this.parent = parent;
		this.templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
	}

	public String getHeaderTemplate() {
		return null;
	}

	public String getTemplateTemplate() {
		return templateTemplate;
	}

	public String getFooterTemplate() {
		return null;
	}

	public String getHeaderTemplatePath() {
		return null;
	}

	public String getTemplateTemplatePath() {
		return makeVoiceTemplatePath(
			canonicalizeClassName(this.parent.getClass().getSimpleName())
				.replace("NotificationTemplate", "VoiceTemplate") +
			"." +
			getCurrentState());
	}

	public String getFooterTemplatePath() {
		return null;
	}

	@Override
	public Object getModel() {
		return parent;
	}
}
