package com.workmarket.api.helpers;

import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.changelog.user.UserCreatedChangeLog;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserChangeLogService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountServicesImpl implements AccountServices {

	@Autowired private RegistrationService registrationService;
	@Autowired private ProfileService profileService;
	@Autowired private UserService userService;
	@Autowired private UserChangeLogService userChangeLogService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserIndexer userIndexer;

	public User registerNewUser(UserDTO userDTO, AddressDTO profileAddress, boolean sendEmail,
			boolean onboardCompleted, boolean autoConfirmEmail) throws Exception {

		// Checks and validations
		registrationService.validatePassword(userDTO.getPassword());
		registrationService.checkForExistingUser(userDTO.getEmail()); // throws Exception

		// Create params
		Company userCompany = registrationService.createUserCompany(null, null, userDTO.getOperatingAsIndividualFlag(), false, true);
		User user = registrationService.createUserObject(userDTO, userCompany);
		user.setLane3ApprovalStatus(ApprovalStatus.APPROVED); // default approved for marketplace

		ProfileDTO profileDTO = new ProfileDTO();
		profileDTO.setOnboardCompleted(onboardCompleted);
		profileDTO.setMobilePhone(userDTO.getMobilePhone());
		profileDTO.setResumeUrl(userDTO.getResumeUrl());
		profileDTO.setFindWork(true);

		registrationService.registerUserInvitation(user, null);
		userService.saveOrUpdateUser(user);
		authenticationService.createUser(user.getUuid(), user.getEmail(), userDTO.getPassword(), userCompany.getUuid(), new UserStatusType(UserStatusType.APPROVED));
		registrationService.associateCompanyWithUser(userCompany, user);
		registrationService.createUserProfile(user, profileDTO, null, false, user.getCompany().getAddress(), userDTO.getRecruitingCampaignId(), null);
		registrationService.assignRolesAndRelationships(user, profileDTO);
		registrationService.handleRecruitingCampaignScenario(userDTO.getRecruitingCampaignId(), user);

		if (profileAddress != null && StringUtils.isNotBlank(profileAddress.getAddress1())) {
			profileService.updateProfileAddressProperties(user.getId(), buildAddressPropertiesMap(profileAddress));
		}

		if (sendEmail) {
			registrationService.dispatchNotifications(user, userCompany, true, false);
		}


		userService.saveOrUpdatePersonaPreference(new PersonaPreference()
			.setUserId(user.getId())
			.setBuyer(false)
			.setSeller(true));

		// Log
		userChangeLogService.createChangeLog(
			new UserCreatedChangeLog(user.getId(), authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId())
		);

		// Index
		userIndexer.reindexById(user.getId());

		if (autoConfirmEmail) {
			registrationService.confirmAndApproveAccount(user.getId());
		}
		return user;
	}

	private Map<String, String> buildAddressPropertiesMap(AddressDTO address) {
		return CollectionUtilities.newStringMap(
			"address1", address.getAddress1(),
			"address2", address.getAddress2(),
			"city", address.getCity(),
			"state", address.getState(),
			"postalCode", address.getPostalCode(),
			"country", address.getCountry(),
			"latitude", String.valueOf(address.getLatitude()),
			"longitude", String.valueOf(address.getLongitude()),
			"addressType", (address.getAddressTypeCode()));
	}
}
