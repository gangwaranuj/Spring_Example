package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentRoutingServiceImpl implements AssignmentRoutingService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public RoutingDTO get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetRoutingUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public RoutingDTO update(String id, RoutingDTO routingDTO, boolean readyToSend) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateRoutingUseCase.class, id, routingDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
