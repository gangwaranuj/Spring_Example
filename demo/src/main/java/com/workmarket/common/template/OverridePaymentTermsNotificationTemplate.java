package com.workmarket.common.template;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class OverridePaymentTermsNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 1L;

	private Company company;
	private String note;

	public OverridePaymentTermsNotificationTemplate(Long toId, Company company, String note) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.MISC), ReplyToType.TRANSACTIONAL);
		this.company = company;
		this.note = note;
	}

	public Company getCompany() {
		return company;
	}

	public String getNote() {
		return note;
	}
}
