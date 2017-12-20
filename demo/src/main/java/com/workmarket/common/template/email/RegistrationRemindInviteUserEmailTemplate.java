package com.workmarket.common.template.email;

import com.workmarket.domains.model.Invitation;
import com.workmarket.service.infra.communication.ReplyToType;

public class RegistrationRemindInviteUserEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = 2532874969116634618L;
	private Invitation invitation;
	
	public RegistrationRemindInviteUserEmailTemplate(Long fromId, String email, Invitation invitation){
		super(fromId, email, "Someone wants to work with you on Work Market");
		this.invitation = invitation;
		this.setReplyToType(ReplyToType.INVITATION_FROM_USER);
	}
	
	public Invitation getInvitation() {
		return invitation;
	}
	
}
