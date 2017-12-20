package com.workmarket.common.template;


import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.infra.communication.ReplyToType;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class BulkUserUploadFailedNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = -8104319868557982830L;
	private String errorMessages;
	private String fileUUID;
	private boolean showError;
	private int status;

	public BulkUserUploadFailedNotificationTemplate(Long toId, BulkUserUploadResponse response, boolean showError) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.BULK_USER_UPLOAD_FAILED), ReplyToType.TRANSACTIONAL);
		this.setEnabledDeliveryMethods(false, false, true, false, false);
		createErrorMessages(response.getErrors());
		this.fileUUID = response.getFileUUID();
		this.showError = showError;
		this.status = response.getStatus().ordinal();
	}

	private void createErrorMessages(List<String> errors) {
		errorMessages = StringUtils.join(errors, ". ");
	}

	public String getErrorMessages() {
		return errorMessages;
	}

	public String getFileUUID() {
		return fileUUID;
	}

	public boolean getShowError() {
		return showError;
	}

	public int getStatus() {
		return status;
	}
}
