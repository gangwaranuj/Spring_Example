package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateRoutingUseCase
	extends AbstractAssignmentUseCase<UpdateRoutingUseCase, RoutingDTO> {

	public UpdateRoutingUseCase(String id, RoutingDTO routingDTO, boolean readyToSend) {
		this.id = id;
		this.routingDTO = routingDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateRoutingUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(routingDTO);
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
		getRouting();
		getConfiguration();

		copyRoutingDTO();
	}

	@Override
	protected void process() {
		loadRouting();
		loadWork();
	}

	@Override
	protected void save() throws ValidationException, WorkAuthorizationException {
		generateWorkSaveRequest();
		saveWork();
	}

	@Override
	protected void finish() throws WorkAuthorizationException {
		getWork();
		getRouting();
		getConfiguration();
		loadRoutingDTO();
		sendWork();
	}

	@Override
	public UpdateRoutingUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
		handleWorkActionException();
		return this;
	}

	@Override
	public RoutingDTO andReturn() {
		return routingDTOBuilder.build();
	}
}
