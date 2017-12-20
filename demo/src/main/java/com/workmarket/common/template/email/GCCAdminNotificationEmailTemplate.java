package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;

public class GCCAdminNotificationEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -3268881396247787324L;
	private String errorMessage;

	public GCCAdminNotificationEmailTemplate(String toEmail, String errorMessage) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toEmail);

		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
