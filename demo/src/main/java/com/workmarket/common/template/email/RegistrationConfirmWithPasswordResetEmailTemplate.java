package com.workmarket.common.template.email;

import com.workmarket.domains.model.request.PasswordResetRequest;
import com.workmarket.service.infra.communication.ReplyToType;

public class RegistrationConfirmWithPasswordResetEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -4180837114864019480L;
	private PasswordResetRequest request;
	
	public RegistrationConfirmWithPasswordResetEmailTemplate(Long fromId, Long toId, PasswordResetRequest request) {
		super(fromId, toId, null);
		this.request = request;
		this.setReplyToType(ReplyToType.TRANSACTIONAL_FROM_USER);
	}
	
	public PasswordResetRequest getRequest() {
		return request;
	}
}