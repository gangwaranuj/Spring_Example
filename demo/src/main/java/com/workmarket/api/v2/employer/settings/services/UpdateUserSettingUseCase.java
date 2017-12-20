package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.service.business.UserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateUserSettingUseCase extends AbstractUserSettingUseCase<UpdateUserSettingUseCase, UserDTO> {

	@Autowired private UserService userService;

	public UpdateUserSettingUseCase(String userNumber, UserDTO userDTO) {
		this.userNumber = userNumber;
		this.userDTO = userDTO; // Update DTO from POST
	}

	protected void copyUserDTO() {
		Assert.notNull(userNumber);

		user = userService.findUserByUserNumber(userNumber);
		// Set the ID so it doesn't look like a new user downstream
		this.userDTOBuilder = new UserDTO.Builder(userDTO).setId(user.getId());
		this.id = user.getId();
		userDTO = new UserDTO.Builder(userDTO).setId(user.getId()).build();
	}
	
	@Override
	protected void failFast() {
		Assert.notNull(userNumber);
		Assert.notNull(userDTO);
	}

	@Override
	protected void init() throws Exception {
		checkAccessPermissions();
		getUserDetails();
		getUser();
	}

	@Override
	protected void prepare() {
		copyUserDTO();
		getRoleSettingsDTO();
		getPermissionSettingsDTO();
		validateUser();
		validateAddress();
	}

	@Override
	protected void process() throws BeansException {
		createEmployeeDTO();
		loadEmployeeDTO();
		loadRoles();
	}

	@Override
	protected void save() throws Exception {
		saveOrUpdateUser();
		saveOrUpdateProfile(true);
		saveOrUpdateCustomPermissions();
		saveOrUpdateOrgUnitMemberships();
	}

	@Override
	protected void finish() {
		getUser();
		getUserAclRoleAssociations();
		loadRoleSettingsDTO();
		loadPermissionSettingsDTO();
		getUserOrgUnitMemberships();
		loadUserDTO();
	}

	@Override
	public UserDTO andReturn() {
		return userDTOBuilder.build();
	}

	@Override
	protected UpdateUserSettingUseCase me() {
		return this;
	}

	@Override
	protected UpdateUserSettingUseCase handleExceptions() throws Exception {
		handleValidationException();
		handleForbiddenException();
		handleNoSuchMethodException();
		handleBeansException();
		handleIllegalAccessException();
		handleInvocationTargetException();
		return this;
	}
}
