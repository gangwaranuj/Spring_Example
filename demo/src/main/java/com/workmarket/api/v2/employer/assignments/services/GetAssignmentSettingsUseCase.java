package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GetAssignmentSettingsUseCase extends AbstractAssignmentSettingsUseCase<GetAssignmentSettingsUseCase, ConfigurationDTO> {

	public GetAssignmentSettingsUseCase() {
	}

	@Override
	protected void init() {
		getUser();
	}

	@Override
	protected GetAssignmentSettingsUseCase me() {
		return this;
	}

	@Override
	protected GetAssignmentSettingsUseCase handleExceptions() throws ValidationException {
		return this;
	}

	@Override
	public void prepare() {
		loadConfigurationDTO();
	}

	@Override
	public ConfigurationDTO andReturn() {
		return configurationDTOBuilder.build();
	}
}
