package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;

public interface AssignmentPricingService {
	PricingDTO get(String id) throws WorkActionException;
	PricingDTO update(String id, PricingDTO pricingDTO, boolean readyToSend) throws WorkActionException, ValidationException, WorkAuthorizationException;
}
