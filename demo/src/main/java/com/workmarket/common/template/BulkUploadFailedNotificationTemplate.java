package com.workmarket.common.template;


import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.thrift.work.uploader.WorkUpload;
import com.workmarket.thrift.work.uploader.WorkUploadError;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

public class BulkUploadFailedNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -2824440695284586561L;

	private String errorMessages;

	public BulkUploadFailedNotificationTemplate(long toId, List<WorkUpload> errorUploads) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.BULK_UPLOAD_FAILED), ReplyToType.TRANSACTIONAL, null);
		createErrorMessages(errorUploads);
	}

	private void createErrorMessages(List<WorkUpload> errorUploads) {
		errorMessages = "";
		for(WorkUpload upload : errorUploads) {
			for(WorkUploadError e : upload.getErrors()) {
				errorMessages += "Line " + upload.getLineNumber() + ": " + StringEscapeUtils.escapeHtml(e.getViolation().getWhy());
			}
		}
	}

	public String getErrorMessages() {
		return errorMessages;
	}
}
