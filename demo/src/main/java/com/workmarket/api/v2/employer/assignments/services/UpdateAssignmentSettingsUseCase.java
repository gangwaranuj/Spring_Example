package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateAssignmentSettingsUseCase
	extends AbstractAssignmentSettingsUseCase<UpdateAssignmentSettingsUseCase, ConfigurationDTO> {

	public UpdateAssignmentSettingsUseCase(ConfigurationDTO configurationDTO) {
		this.configurationDTO = configurationDTO;
	}

	@Override
	protected UpdateAssignmentSettingsUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(configurationDTOBuilder);
	}

	@Override
	protected void init(){
		getUser();
	}

	@Override
	protected void prepare()
	{
		copyConfigurationDTO();
		validateAssignmentSettings();
	}

	@Override
	protected void process() {
		loadAssignmentSettings();
	}

	@Override
	protected void save() throws ValidationException {
		saveAssignmentSettings();
	}

	@Override
	protected void finish() {
		loadConfigurationDTO();
	}

	@Override
	public ConfigurationDTO andReturn() {
		return configurationDTOBuilder.build();
	}

	@Override
	public UpdateAssignmentSettingsUseCase handleExceptions() throws ValidationException {
		handleValidationException();
		return this;
	}
}
