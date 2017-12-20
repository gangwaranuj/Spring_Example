package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdatePricingUseCase
	extends AbstractAssignmentUseCase<UpdatePricingUseCase, PricingDTO> {

	public UpdatePricingUseCase(String id, PricingDTO pricingDTO, boolean readyToSend) {
		this.id = id;
		this.pricingDTO = pricingDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdatePricingUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(pricingDTO);
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
		getConfiguration();
		getPricing();

		copyPricingDTO();
	}

	@Override
	protected void process() {
		loadPricing();
		loadWork();
	}

	@Override
	protected void save() throws ValidationException, WorkAuthorizationException {
		generateWorkSaveRequest();
		saveWork();
	}

	@Override
	protected void finish() throws WorkAuthorizationException{
		getWork();
		getPricing();
		loadPricingDTO();
	}

	@Override
	public UpdatePricingUseCase handleExceptions() throws ValidationException, WorkActionException, WorkAuthorizationException {
		handleValidationException();
		handleWorkActionException();
		handleWorkAuthorizationException();
		return this;
	}

	@Override
	public PricingDTO andReturn() {
		return pricingDTOBuilder.build();
	}
}
