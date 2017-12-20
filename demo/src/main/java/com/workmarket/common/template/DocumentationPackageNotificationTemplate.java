package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

/**
 * User: alexsilva Date: 1/31/14 Time: 1:28 PM
 */
public class DocumentationPackageNotificationTemplate extends NotificationTemplate {
	private static final long serialVersionUID = 4906379792657017389L;

	private String uri;

	public DocumentationPackageNotificationTemplate(Long toId, String uri) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.ASSET_DOCUMENTATION_PACKAGE_DOWNLOAD), ReplyToType.TRANSACTIONAL);
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
