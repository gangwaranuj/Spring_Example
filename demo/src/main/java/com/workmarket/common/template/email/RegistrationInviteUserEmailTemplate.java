package com.workmarket.common.template.email;

import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class RegistrationInviteUserEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -8796790716740270983L;
	private Invitation invitation;
	private boolean privateInvitation;

	public RegistrationInviteUserEmailTemplate(Long fromId, String email, Invitation invitation) {
		super(fromId, email);
		this.invitation = invitation;
		this.privateInvitation =
			invitation == null || invitation.getInvitationType() == null ?
				false :
				invitation.getInvitationType().equals(InvitationType.EXCLUSIVE);
		this.setReplyToType(ReplyToType.INVITATION_FROM_USER);
	}

	public Invitation getInvitation() {
		return invitation;
	}
	public boolean isPrivateInvitation() {
		return privateInvitation;
	}
}
