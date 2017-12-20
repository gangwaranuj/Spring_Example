package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetAssignmentConfigurationUseCase
	extends AbstractAssignmentUseCase<GetAssignmentConfigurationUseCase, ConfigurationDTO> {

	public GetAssignmentConfigurationUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetAssignmentConfigurationUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
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
	}

	@Override
	protected void finish() {
		loadConfigurationDTO();
	}

	@Override
	protected GetAssignmentConfigurationUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public ConfigurationDTO andReturn() {
		return configurationDTOBuilder.build();
	}
}
