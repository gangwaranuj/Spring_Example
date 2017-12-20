package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentSettingsServiceImpl implements AssignmentSettingsService{
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public ConfigurationDTO get() throws ValidationException {
		return useCaseFactory
			.getUseCase(GetAssignmentSettingsUseCase.class)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public ConfigurationDTO update(ConfigurationDTO configurationDTO) throws ValidationException {
		return useCaseFactory
			.getUseCase(UpdateAssignmentSettingsUseCase.class, configurationDTO)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
