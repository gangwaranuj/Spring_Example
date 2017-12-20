package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateLocationUseCase
	extends AbstractAssignmentUseCase<UpdateLocationUseCase, LocationDTO> {

	public UpdateLocationUseCase(String id, LocationDTO locationDTO, boolean readyToSend) {
		this.id = id;
		this.locationDTO = locationDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateLocationUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(locationDTO);
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
		getLocation();
		getLocationContact();
		getSecondaryLocationContact();

		copyLocationDTO();
		copyLocationContactDTO();
		copySecondaryLocationContactDTO();

		createAddress();
	}

	@Override
	protected void process() {
		loadAddress();
		loadLocation();
		loadLocationContact();
		loadSecondaryLocationContact();
		loadWork();
	}

	@Override
	protected void save() throws ValidationException, WorkAuthorizationException {
		generateWorkSaveRequest();
		saveWork();
	}

	@Override
	protected void finish() {
		getWork();
		getLocation();
		getLocationContact();
		getSecondaryLocationContact();
		loadLocationContactDTO();
		loadSecondaryLocationContactDTO();
		loadLocationDTO();
	}

	@Override
	public UpdateLocationUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
		handleWorkActionException();
		return this;
	}

	@Override
	public LocationDTO andReturn() {
		return locationDTOBuilder.build();
	}
}
