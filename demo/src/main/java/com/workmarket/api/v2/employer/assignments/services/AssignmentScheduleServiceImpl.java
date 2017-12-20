package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentScheduleServiceImpl implements AssignmentScheduleService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public ScheduleDTO get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetScheduleUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public ScheduleDTO update(String id, ScheduleDTO scheduleDTO, boolean readyToSend) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateScheduleUseCase.class, id, scheduleDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
