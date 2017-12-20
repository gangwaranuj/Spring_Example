package com.workmarket.web.validators;

import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.InvitationConcern;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component("invitationConcernValidator")
public class InvitationConcernValidator extends ConcernValidator {
	@Override
	public void validateEntityId(Concern concern, Errors errors) {
		InvitationConcern invitationConcern = (InvitationConcern)concern;
		Invitation invitation = invitationConcern.getInvitation();

		if ((invitation == null) || (invitation.getId() == null)) {
			errors.reject("NotEmpty", "Invitation");
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return (InvitationConcern.class == clazz);
	}
}
