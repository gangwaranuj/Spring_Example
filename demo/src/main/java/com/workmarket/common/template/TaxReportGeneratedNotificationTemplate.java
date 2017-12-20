package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class TaxReportGeneratedNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = -195063673459541526L;

	public TaxReportGeneratedNotificationTemplate(Long toId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.TAX_REPORT_CREATED_WM_INTERNAL_ONLY), ReplyToType.TRANSACTIONAL);
	}
}
