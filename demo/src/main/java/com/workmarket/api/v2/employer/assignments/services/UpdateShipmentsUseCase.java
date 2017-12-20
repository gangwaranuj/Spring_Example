package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateShipmentsUseCase
	extends AbstractAssignmentUseCase<UpdateShipmentsUseCase, ShipmentGroupDTO> {

	public UpdateShipmentsUseCase() {}

	public UpdateShipmentsUseCase(String id, ShipmentGroupDTO shipmentGroupDTO, boolean readyToSend) {
		this.id = id;
		this.shipmentGroupDTO = shipmentGroupDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateShipmentsUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(shipmentGroupDTO);
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
		getConfiguration();

		copyShipmentGroupDTO();

		loadConfigurationDTO();
		buildConfigurationDTO();
	}

	@Override
	protected void process() {
		loadShipments();
		loadWork();
	}

	@Override
	protected void save() throws ValidationException, WorkAuthorizationException{
		generateWorkSaveRequest();
		saveWork();
	}

	@Override
	protected void finish() {
		getWork();
		getShipments();
		loadShipmentGroupDTO();
	}

	@Override
	protected UpdateShipmentsUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
		handleWorkActionException();
		return this;
	}

	@Override
	public ShipmentGroupDTO andReturn() {
		return shipmentGroupDTOBuilder.build();
	}
}
