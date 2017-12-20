package com.workmarket.common.template;


import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.StringUtilities;

import java.util.List;

public class BulkUploadFinishedNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -1031168862721097821L;
	private static final int MAX_FAILED_WORK_NUMBERS = 10;

	private List<String> workNumbers;
	private final String failedRowsMessage;

	public BulkUploadFinishedNotificationTemplate(long toId, List<String> workNumbers, List<Long> failedRows) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.BULK_UPLOAD_COMPLETE), ReplyToType.TRANSACTIONAL, null);
		this.workNumbers = workNumbers;
		int failures = failedRows.size();
		if (failures == 0) {
			failedRowsMessage = "";
		} else {
			failedRowsMessage = failures > MAX_FAILED_WORK_NUMBERS ?
				String.format("Failed to upload %d rows. ", failures) :
				String.format("Failed to upload the %s on %s %s. ", StringUtilities.pluralize("assignment", failures), StringUtilities.pluralize("row ", failures),
					StringUtilities.JoinWithLastSeparator(failedRows, ", ", " and "));
		}
	}

	public String getFailedRowsMessage() {
		return failedRowsMessage;
	}

	public List<String> getWorkNumbers() {
		return workNumbers;
	}
}
