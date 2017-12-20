package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

public interface AssignmentLocationService {
	LocationDTO get(String id) throws WorkActionException;
	LocationDTO update(String id, LocationDTO locationDTO, boolean readyToSend) throws WorkActionException, ValidationException;
}
