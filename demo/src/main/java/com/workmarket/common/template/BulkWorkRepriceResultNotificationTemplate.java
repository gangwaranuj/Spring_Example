package com.workmarket.common.template;


import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.infra.communication.ReplyToType;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class BulkWorkRepriceResultNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 7511399799139353446L;

	private int succeeded;
	private int failed;

	public BulkWorkRepriceResultNotificationTemplate(Long toId, int succeeded, int failed) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.BULK_USER_UPLOAD_FAILED), ReplyToType.TRANSACTIONAL);
		this.setEnabledDeliveryMethods(false, false, true, false, false);
		this.succeeded = succeeded;
		this.failed = failed;
	}

	public int getSucceeded() {
		return succeeded;
	}

	public int getFailed() {
		return failed;
	}
}
