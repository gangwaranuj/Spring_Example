package com.workmarket.web.validators;

import com.google.api.client.util.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.InvitationDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvitationDTOValidator {

	@Autowired private MessageBundleHelper messageBundleHelper;
	@Autowired private RegistrationService registrationService;
	@Autowired private UserService userService;
	@Autowired private LaneService laneService;
	@Autowired private ProfileService profileService;
	@Autowired private AuthenticationService authenticationService;

	public List<String> validate(InvitationDTO invitationDTO) {
		List<String> errors = Lists.newArrayList();

		if (!StringUtilities.isLengthBetween(invitationDTO.getFirstName(), 1, 50)) {
			errors.add(messageBundleHelper.getMessage("Pattern.newUser.firstName"));
		}

		if (!StringUtilities.isLengthBetween(invitationDTO.getLastName(), 1, 50)) {
			errors.add(messageBundleHelper.getMessage("Pattern.newUser.lastName"));
		}

		if (!EmailValidator.getInstance().isValid(invitationDTO.getEmail())) {
			errors.add(messageBundleHelper.getMessage("invitations.send.failed_invalid_email_one", invitationDTO.getEmail()));
		} else if (registrationService.invitationExists(invitationDTO.getInviterUserId(), invitationDTO.getEmail())) {
			errors.add(messageBundleHelper.getMessage("invitations.send.dupes_one", invitationDTO.getEmail()));
		}

		if (!errors.isEmpty()) {
			return errors;
		}

		User invitedUser = userService.findUserByEmail(invitationDTO.getEmail());
		if(invitedUser == null) {
			return errors;
		}

		if (authenticationService.isSuspended(invitedUser)
				|| authenticationService.isDeactivated(invitedUser)
				|| !authenticationService.isActive(invitedUser)) {
			errors.add(messageBundleHelper.getMessage("invitations.send.failed_unavailable_one", invitationDTO.getEmail()));
		}

		if (laneService.isUserPartOfLane123(invitedUser.getId(), invitationDTO.getInvitingCompanyId())) {
			errors.add(messageBundleHelper.getMessage("invitations.send.success_in_worker_pool_one", invitationDTO.getEmail()));
		}

		Company invitedUserCompany = profileService.findCompany(invitedUser.getId());
		if (invitedUserCompany != null && invitedUserCompany.getId().equals(invitationDTO.getInvitingCompanyId())) {
			errors.add(messageBundleHelper.getMessage("invitations.send.success_in_worker_pool_one", invitationDTO.getEmail()));
		}
		return errors;
	}
}
