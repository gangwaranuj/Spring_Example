package com.workmarket.common.template.email;

import com.workmarket.domains.model.request.PasswordResetRequest;
import com.workmarket.service.infra.communication.ReplyToType;

public class RegistrationRemindConfirmationWithPasswordResetEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -3873042366563330909L;
	private PasswordResetRequest request;
	
	public RegistrationRemindConfirmationWithPasswordResetEmailTemplate(Long fromId, Long toId, PasswordResetRequest request) {
		super(fromId, toId, null);
		this.request = request;
		this.setReplyToType(ReplyToType.TRANSACTIONAL_FROM_USER);
	}
	
	public PasswordResetRequest getRequest() {
		return request;
	}
}