package com.workmarket.common.template.email;

import com.workmarket.domains.model.Invitation;
import com.workmarket.service.infra.communication.ReplyToType;

public class RegistrationInviteExistingUserEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -1300471903526836870L;
	private Invitation invitation;

	public RegistrationInviteExistingUserEmailTemplate(Long fromId, String email, Invitation invitation) {
		super(fromId, email);
		this.invitation = invitation;
		this.setReplyToType(ReplyToType.INVITATION_FROM_USER);
	}

	public Invitation getInvitation() {
		return invitation;
	}
}
