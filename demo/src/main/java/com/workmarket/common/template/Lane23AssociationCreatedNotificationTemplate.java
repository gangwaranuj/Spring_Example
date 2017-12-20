package com.workmarket.common.template;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class Lane23AssociationCreatedNotificationTemplate extends NotificationTemplate {
	/**
	 *
	 */
	private static final long serialVersionUID = 8238577532064244888L;
	private Company company;
	private String companyNumber;
	private boolean includeWelcome;

	public Lane23AssociationCreatedNotificationTemplate(Long toId, Company company, boolean includeWelcomeEmail) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.LANE_23_CREATED), ReplyToType.TRANSACTIONAL);
		this.company = company;
		this.companyNumber = company.getEncryptedId();
		this.includeWelcome = includeWelcomeEmail;
	}

	public Company getCompany() {
		return company;
	}

	public String getCompanyNumber() {
		return companyNumber;
	}

	public boolean isIncludeWelcome() {
		return includeWelcome;
	}

}
