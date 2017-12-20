package com.workmarket.common.template;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.infra.communication.ReplyToType;

public class BulkUserUploadFinishedNotificationTemplate extends NotificationTemplate {
	private static final long serialVersionUID = -8104319863557982830L;
	private int uploadCount;

	public BulkUserUploadFinishedNotificationTemplate(Long toId, BulkUserUploadResponse response) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.BULK_USER_UPLOAD_COMPLETE), ReplyToType.TRANSACTIONAL);
		this.setEnabledDeliveryMethods(false, false, true, false, false);
		this.uploadCount = response.getUploadCount();
	}

	public int getUploadCount() {
		return uploadCount;
	}
}
