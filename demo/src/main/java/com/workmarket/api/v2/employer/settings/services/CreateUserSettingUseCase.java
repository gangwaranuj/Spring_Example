package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.CreateUserDTO;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class CreateUserSettingUseCase extends AbstractUserSettingUseCase<CreateUserSettingUseCase, CreateUserDTO> {
	
	public CreateUserSettingUseCase(CreateUserDTO createUserDTO) {
		this.userDTO = createUserDTO;
		this.onboardingNotificationStrategy = createUserDTO.getOnboardingNotificationStrategy();
	}

	@Override
	protected void failFast() {
		Assert.notNull(userDTO);
	}

	@Override
	protected void init() {
		getUserDetails();
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
		saveOrUpdateProfile(false);
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
	public CreateUserDTO andReturn() {
		return new CreateUserDTO.Builder(userDTOBuilder.build())
			.build();
	}

	@Override
	protected CreateUserSettingUseCase me() {
		return this;
	}

	@Override
	protected CreateUserSettingUseCase handleExceptions() throws Exception {
		handleValidationException();
		handleNoSuchMethodException();
		handleBeansException();
		handleIllegalAccessException();
		handleInvocationTargetException();
		return this;
	}
}
