package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetDeliverablesUseCase
	extends AbstractAssignmentUseCase<GetDeliverablesUseCase, DeliverablesGroupDTO> {

	public GetDeliverablesUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetDeliverablesUseCase me() {
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
		getDeliverablesGroup();
	}

	@Override
	protected void finish() {
		loadDeliverablesGroupDTO();
	}

	@Override
	public GetDeliverablesUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public DeliverablesGroupDTO andReturn() {
		return deliverablesGroupDTOBuilder.build();
	}
}
