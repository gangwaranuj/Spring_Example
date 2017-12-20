package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetShipmentsUseCase
	extends AbstractAssignmentUseCase<GetShipmentsUseCase, ShipmentGroupDTO> {

	public GetShipmentsUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetShipmentsUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
	}

	@Override
	protected void init() throws WorkActionException {
		getUser();
		generateWorkRequest();
		getWorkResponse();
	}

	@Override
	protected void prepare() {
		getWork();
		getShipments();
	}

	@Override
	protected void finish() {
		loadShipmentGroupDTO();
	}

	@Override
	protected GetShipmentsUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public ShipmentGroupDTO andReturn() {
		return shipmentGroupDTOBuilder.build();
	}
}
