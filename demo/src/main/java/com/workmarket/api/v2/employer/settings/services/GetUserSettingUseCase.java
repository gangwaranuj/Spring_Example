package com.workmarket.api.v2.employer.settings.services;


import com.workmarket.api.exceptions.ForbiddenException;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetUserSettingUseCase extends AbstractUserSettingUseCase<GetUserSettingUseCase, UserDTO> {

	public GetUserSettingUseCase(String userNumber) {
		this.userNumber = userNumber;
	}

	@Override
	protected void failFast() {
		Assert.notNull(userNumber);
	}

	@Override
	protected void init() throws ForbiddenException {
		checkAccessPermissions();
		getUserDetails();
	}

	@Override
	protected void process() {
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
	protected GetUserSettingUseCase me() {
		return this;
	}

	@Override
	protected GetUserSettingUseCase handleExceptions() throws Exception {
		handleForbiddenException();
		return this;
	}
}
