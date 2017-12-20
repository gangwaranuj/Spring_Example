package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.assignments.services.UseCaseFactory;
import com.workmarket.api.v2.employer.settings.models.CreateUserDTO;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.web.forms.user.ReassignUserForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.validators.ReassignValidator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSettingServiceImpl implements UserSettingService {

	@Autowired UseCaseFactory useCaseFactory;
	@Autowired ReassignValidator reassignValidator;
	@Autowired UserService userService;
	@Autowired SecurityContextFacade securityContextFacade;
	@Autowired MessageBundleHelper messageBundleHelper;
	@Autowired AuthenticationService authenticationService;

	@Override
	public UserDTO create(CreateUserDTO createUserDTO) throws Exception {
		return useCaseFactory
			.getUseCase(CreateUserSettingUseCase.class, createUserDTO)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public UserDTO update(String userNumber, UserDTO userDTO) throws Exception {
		return useCaseFactory
			.getUseCase(UpdateUserSettingUseCase.class, userNumber, userDTO)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public UserDTO get(String userNumber) throws Exception {
		return useCaseFactory
			.getUseCase(GetUserSettingUseCase.class, userNumber)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public UserDTO deactivate(final ReassignUserForm reassignUserForm) throws ValidationException {

		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, securityContextFacade.getCurrentUser());

		if (CollectionUtils.isNotEmpty(errors)) {
			throw new ValidationException(errors);
		}

		final User userToDeactivate = userService.findUserByUserNumber(reassignUserForm.getCurrentOwner());

		userService.deactivateUser(
			userToDeactivate.getId(),
			reassignUserForm.getNewWorkOwner(),
			reassignUserForm.getNewGroupsOwner(),
			reassignUserForm.getNewAssessmentsOwner());

		final User deactivatedUser = userService.findUserByUserNumber(userToDeactivate.getUserNumber());

		return new UserDTO.Builder()
			.setId(deactivatedUser.getId())
			.setUserNumber(deactivatedUser.getUserNumber())
			.setUserStatusType(authenticationService.getUserStatus(deactivatedUser).getCode())
			.build();
	}
}
