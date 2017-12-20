package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetPricingUseCase
	extends AbstractAssignmentUseCase<GetPricingUseCase, PricingDTO> {

	public GetPricingUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetPricingUseCase me() {
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
		normalizeWorkPricing();
		getConfiguration();
		getPricing();
	}

	@Override
	protected void finish() {
		loadPricingDTO();
	}

	@Override
	public GetPricingUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public PricingDTO andReturn() {
		return pricingDTOBuilder.build();
	}
}
