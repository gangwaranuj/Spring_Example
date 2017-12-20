package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetScheduleUseCase
	extends AbstractAssignmentUseCase<GetScheduleUseCase, ScheduleDTO> {

	public GetScheduleUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetScheduleUseCase me() {
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
		getSchedule();
		getConfiguration();
	}

	@Override
	protected void finish() {
		loadScheduleDTO();
	}

	@Override
	public GetScheduleUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public ScheduleDTO andReturn() {
		return scheduleDTOBuilder.build();
	}
}
