package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public abstract class AbstractTaxReportAvailableNotificationTemplate<T extends AbstractTaxReport> extends NotificationTemplate {

	private static final long serialVersionUID = -195063673459541526L;
	private T taxReport;

	protected AbstractTaxReportAvailableNotificationTemplate(Long toId, T taxReport) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.TAX_REPORT_AVAILABLE), ReplyToType.TRANSACTIONAL);
		this.taxReport = taxReport;
	}

	public T getTaxReport() {
		return taxReport;
	}
}
