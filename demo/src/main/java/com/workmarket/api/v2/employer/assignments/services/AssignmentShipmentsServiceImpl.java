package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentShipmentsServiceImpl implements AssignmentShipmentsService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public ShipmentGroupDTO get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetShipmentsUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public ShipmentGroupDTO update(String id, ShipmentGroupDTO shipmentGroupDTO, boolean readyToSend) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateShipmentsUseCase.class, id, shipmentGroupDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
