package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentPricingServiceImpl implements AssignmentPricingService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public PricingDTO get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetPricingUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public PricingDTO update(String id, PricingDTO pricingDTO, boolean readyToSend) throws WorkActionException, ValidationException, WorkAuthorizationException {
		return useCaseFactory
			.getUseCase(UpdatePricingUseCase.class, id, pricingDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
