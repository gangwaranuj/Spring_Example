package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateAssignmentConfigurationUseCase
	extends AbstractAssignmentUseCase<UpdateAssignmentConfigurationUseCase, ConfigurationDTO> {

	public UpdateAssignmentConfigurationUseCase(String id, ConfigurationDTO configurationDTO, boolean readyToSend) {
		this.id = id;
		this.configurationDTO = configurationDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateAssignmentConfigurationUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(configurationDTO);
	}

	@Override
	protected void init() throws WorkActionException {
		getUser();
		generateWorkRequest();
		getWorkResponse();
	}

	@Override
	protected void prepare() {
		getWork();
		getConfiguration();

		copyConfigurationDTO();
	}

	@Override
	protected void process() {
		loadConfiguration();
		loadWork();
	}

	@Override
	protected void save() throws ValidationException, WorkAuthorizationException {
		generateWorkSaveRequest();
		saveWork();
	}

	@Override
	protected void finish() {
		getWork();
		getConfiguration();
		loadConfigurationDTO();
	}

	@Override
	public UpdateAssignmentConfigurationUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
		handleWorkActionException();
		return this;
	}

	@Override
	public ConfigurationDTO andReturn() {
		return configurationDTOBuilder.build();
	}
}
