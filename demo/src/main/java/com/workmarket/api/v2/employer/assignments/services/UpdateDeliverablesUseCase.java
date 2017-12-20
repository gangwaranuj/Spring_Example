package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateDeliverablesUseCase
	extends AbstractAssignmentUseCase<UpdateDeliverablesUseCase, DeliverablesGroupDTO> {

	public UpdateDeliverablesUseCase(String id, DeliverablesGroupDTO deliverablesGroupDTO, boolean readyToSend) {
		this.id = id;
		this.deliverablesGroupDTO = deliverablesGroupDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateDeliverablesUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(deliverablesGroupDTO);
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

		copyDeliverablesGroupDTO();
	}

	@Override
	protected void process() {
		loadDeliverablesGroup();
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
		getDeliverablesGroup();
		loadDeliverablesGroupDTO();
	}

	@Override
	public UpdateDeliverablesUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
		handleWorkActionException();
		return this;
	}

	@Override
	public DeliverablesGroupDTO andReturn() {
		return deliverablesGroupDTOBuilder.build();
	}
}
