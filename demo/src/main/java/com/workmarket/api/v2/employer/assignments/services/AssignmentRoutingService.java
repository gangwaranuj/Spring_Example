package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

public interface AssignmentRoutingService {
	RoutingDTO get(String id) throws WorkActionException;
	RoutingDTO update(String id, RoutingDTO routingDTO, boolean readyToSend) throws WorkActionException, ValidationException;
}
