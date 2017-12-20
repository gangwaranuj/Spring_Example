package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateScheduleUseCase
	extends AbstractAssignmentUseCase<UpdateScheduleUseCase, ScheduleDTO> {

	public UpdateScheduleUseCase(String id, ScheduleDTO scheduleDTO, boolean readyToSend) {
		this.id = id;
		this.scheduleDTO = scheduleDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateScheduleUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(scheduleDTO);
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

		copyScheduleDTO();
	}

	@Override
	protected void process() {
		loadSchedule();
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
		getSchedule();
		loadScheduleDTO();
	}

	@Override
	public UpdateScheduleUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
		handleWorkActionException();
		return this;
	}

	@Override
	public ScheduleDTO andReturn() {
		return scheduleDTOBuilder.build();
	}
}
