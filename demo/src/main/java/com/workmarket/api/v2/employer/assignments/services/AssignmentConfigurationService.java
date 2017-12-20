package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

public interface AssignmentConfigurationService {
	ConfigurationDTO get(String id) throws WorkActionException;
	ConfigurationDTO update(String id, ConfigurationDTO builder, boolean readyToSend) throws WorkActionException, ValidationException;
}
