package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

public interface AssignmentScheduleService {
	ScheduleDTO get(String id) throws WorkActionException;
	ScheduleDTO update(String id, ScheduleDTO scheduleDTO, boolean readyToSend) throws WorkActionException, ValidationException;
}
