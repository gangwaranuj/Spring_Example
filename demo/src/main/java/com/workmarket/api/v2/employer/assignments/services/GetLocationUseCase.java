package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetLocationUseCase
	extends AbstractAssignmentUseCase<GetLocationUseCase, LocationDTO> {

	public GetLocationUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetLocationUseCase me() {
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
		getLocation();
		getLocationContact();
		getSecondaryLocationContact();
		getAddress();
	}

	@Override
	protected void finish() {
		loadLocationContactDTO();
		loadSecondaryLocationContactDTO();
		loadLocationDTO();
	}

	@Override
	public GetLocationUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public LocationDTO andReturn() {
		return locationDTOBuilder.build();
	}
}
