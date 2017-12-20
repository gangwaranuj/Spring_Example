package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentDeliverablesServiceImpl implements AssignmentDeliverablesService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public DeliverablesGroupDTO get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetDeliverablesUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public DeliverablesGroupDTO update(String id, DeliverablesGroupDTO deliverablesGroupDTO, boolean readyToSend) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateDeliverablesUseCase.class, id, deliverablesGroupDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
