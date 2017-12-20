package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetRoutingUseCase
	extends AbstractAssignmentUseCase<GetRoutingUseCase, RoutingDTO> {

	public GetRoutingUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetRoutingUseCase me() {
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
		getRouting();
		getConfiguration();
	}

	@Override
	protected void finish() {
		loadRoutingDTO();
	}

	@Override
	public GetRoutingUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public RoutingDTO andReturn() {
		return routingDTOBuilder.build();
	}
}
