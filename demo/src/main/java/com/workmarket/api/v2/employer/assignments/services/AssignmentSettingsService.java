package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.thrift.core.ValidationException;

public interface AssignmentSettingsService {
	ConfigurationDTO get() throws ValidationException;

	ConfigurationDTO update(ConfigurationDTO configurationDTO) throws ValidationException;
}
