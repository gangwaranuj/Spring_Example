package com.workmarket.service.business.registration;

import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.changelog.user.UserCreatedChangeLog;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserChangeLogService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.InvitationUserRegistrationDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.BeanUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceFacadeImpl implements RegistrationServiceFacade {

	@Autowired private RegistrationService registrationService;
	@Autowired private UserService userService;
	@Autowired private UserIndexer userIndexer;
	@Autowired private UserChangeLogService userChangeLogService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WebRequestContextProvider context;

	@Override
	public User registerNew(UserDTO userDTO,
	                        Long invitationId,
	                        String companyName,
	                        AddressDTO addressDTO,
	                        ProfileDTO profileDTO,
	                        boolean isBuyer) throws Exception {

		// Checks and validations
		registrationService.validatePassword(userDTO.getPassword());
		registrationService.checkForExistingUser(userDTO.getEmail()); // throws Exception

		// Create params
		boolean isCompanyNameBlank = StringUtils.isBlank(companyName);
		Company userCompany = registrationService.createUserCompany(companyName, addressDTO, userDTO.getOperatingAsIndividualFlag(), isBuyer, isCompanyNameBlank);
		User user = registrationService.createUserObject(userDTO, userCompany);

		registrationService.registerUserInvitation(user, invitationId);
		userService.saveOrUpdateUser(user);
		authenticationService.createUser(user.getUuid(), user.getEmail(), userDTO.getPassword(), userCompany.getUuid(), new UserStatusType(UserStatusType.APPROVED));
		registrationService.associateCompanyWithUser(userCompany, user);
		registrationService.createUserProfile(user, profileDTO, addressDTO, isBuyer, user.getCompany().getAddress(), userDTO.getRecruitingCampaignId(), invitationId);
		registrationService.assignRolesAndRelationships(user, profileDTO);
		registrationService.handleRecruitingCampaignScenario(userDTO.getRecruitingCampaignId(), user);
		registrationService.dispatchNotifications(user, userCompany, isCompanyNameBlank, isBuyer);

		// Log
		userService.saveOrUpdatePersonaPreference(new PersonaPreference()
				.setUserId(user.getId())
				.setBuyer(isBuyer)
				.setSeller(!isBuyer));

		userChangeLogService.createChangeLog(
			new UserCreatedChangeLog(user.getId(), authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId())
		);

		// Index
		userIndexer.reindexById(user.getId());
		return user;
	}

	@Override
	public User registerNew(UserDTO userDTO, Long invitationId) throws Exception {
		return registerNew(userDTO, invitationId, null, null, null, false);
	}

	@Override
	public User registerUserSimple(InvitationUserRegistrationDTO invitationDTO, boolean isBuyer) throws Exception {
		UserDTO userDTO = new UserDTO();
		BeanUtilities.copyProperties(userDTO, invitationDTO);
		userDTO.setRecruitingCampaignId(invitationDTO.getCampaignId());
		ProfileDTO profileDTO = new ProfileDTO();
		BeanUtilities.copyProperties(profileDTO, invitationDTO);
		profileDTO.setPostalCode(invitationDTO.getPostalCode());
		return registerNew (
			userDTO, invitationDTO.getInvitationId(), invitationDTO.getCompanyName(), invitationDTO, profileDTO, isBuyer
		);
	}

}
