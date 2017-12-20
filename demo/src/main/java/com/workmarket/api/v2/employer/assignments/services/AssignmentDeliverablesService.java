package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

public interface AssignmentDeliverablesService {
	DeliverablesGroupDTO get(String id) throws WorkActionException;
	DeliverablesGroupDTO update(String id, DeliverablesGroupDTO deliverablesGroupDTO, boolean readyToSend) throws WorkActionException, ValidationException;
}
