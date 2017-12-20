package com.workmarket.common.template.email;

import com.workmarket.domains.model.request.PasswordResetRequest;
import com.workmarket.configuration.Constants;

public class RegistrationRemindPasswordEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -7883181160613657765L;
	private PasswordResetRequest request;

	public RegistrationRemindPasswordEmailTemplate(Long toId, PasswordResetRequest request) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, "Work Market password reset");
		this.request = request;
	}

	public PasswordResetRequest getRequest() {
		return request;
	}
}
