package com.workmarket.common.template.notification;

import com.workmarket.common.template.TwoWayTypedTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;

public class UserNotificationTemplate extends TwoWayTypedTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = 8810315288796660976L;

	private boolean sticky = false;

	private final String templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
	private final String headerTemplatePath = makeNotificationTemplatePath(getHeaderTemplate());
	private final String templateTemplatePath = makeNotificationTemplatePath(getTemplateTemplate());
	private final String footerTemplatePath = makeNotificationTemplatePath(getFooterTemplate());

	public UserNotificationTemplate() {
	}

	public UserNotificationTemplate(Long toId, NotificationType notificationType) {
		super(null, toId, notificationType);
	}

	public UserNotificationTemplate(Long toId, Long fromId, NotificationType notificationType) {
		super(fromId, toId, notificationType);
	}

	public UserNotificationTemplate(Long toId, NotificationType notificationType, boolean sticky) {
		super(null, toId, notificationType);
		this.sticky = sticky;
	}

	public UserNotificationTemplate(Long toId, Long fromId, NotificationType notificationType, boolean sticky) {
		super(fromId, toId, notificationType);
		this.sticky = sticky;
	}

	public String getHeaderTemplate() {
		return Constants.USER_NOTIFICATION_HEADER_TEMPLATE;
	}

	public String getTemplateTemplate() {
		return templateTemplate;
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

	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

	public boolean isSticky() {
		return sticky;
	}

	@Override
	public String getPath() {
		return "/template/notification/";
	}

	static String makeNotificationTemplatePath(final String pathName) {
		return Constants.USER_NOTIFICATION_TEMPLATE_DIRECTORY_PATH +
			"/" +
            pathName +
			Constants.USER_NOTIFICATION_TEMPLATE_EXTENSION;
	}

}
