package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentConfigurationServiceImpl implements AssignmentConfigurationService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public ConfigurationDTO get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetAssignmentConfigurationUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public ConfigurationDTO update(String id, ConfigurationDTO configurationDTO, boolean readyToSend) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateAssignmentConfigurationUseCase.class, id, configurationDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
