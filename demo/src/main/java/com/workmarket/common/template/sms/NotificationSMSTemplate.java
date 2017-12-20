package com.workmarket.common.template.sms;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.common.template.NotificationModel;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.UserNotification;
import com.workmarket.configuration.Constants;

public class NotificationSMSTemplate extends SMSTemplate implements NotificationModel, UserNotification {
	/**
	 *
	 */
	private static final long serialVersionUID = -1885740421721997878L;

	private final String templateTemplate;
	private final String headerTemplatePath;
	private final String templateTemplatePath;
	private final String footerTemplatePath;

	private final NotificationTemplate parent;

	public NotificationSMSTemplate(Long providerId, String toNumber, Long fromId, Long toId, NotificationType notificationType, NotificationTemplate parent) {
		super(providerId, toNumber, fromId, toId, notificationType);
		this.parent = parent;
		this.templateTemplatePath = makeSMSTemplatePath(
			canonicalizeClassName(this.parent.getClass().getSimpleName())
				.replace("NotificationTemplate", "SMSTemplate"));
		this.templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
		this.headerTemplatePath = makeSMSTemplatePath(getHeaderTemplate());
		this.footerTemplatePath = makeSMSTemplatePath(getFooterTemplate());
	}

	public String getHeaderTemplate() {
		return Constants.SMS_HEADER_TEMPLATE;
	}

	public String getTemplateTemplate() {
		return templateTemplate;
	}

	public String getFooterTemplate() {
		return Constants.SMS_FOOTER_TEMPLATE;
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
