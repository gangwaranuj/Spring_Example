package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentLocationServiceImpl implements AssignmentLocationService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public LocationDTO get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetLocationUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public LocationDTO update(String id, LocationDTO locationDTO, boolean readyToSend) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateLocationUseCase.class, id, locationDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
