package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetTemplateUseCase
	extends AbstractAssignmentUseCase<GetTemplateUseCase, TemplateDTO> {

	public GetTemplateUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetTemplateUseCase me() {
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
		getTemplate();
		getSchedule();
		getLocation();
		getLocationContact();
		getSecondaryLocationContact();
		getBuyer();
		getSupportContact();
		getAddress();
		getPricing();
		getRouting();
		getCustomFieldGroups();
		getShipments();
		getSurveys();
		getDocuments();
		getDeliverablesGroup();
		getConfiguration();
	}

	@Override
	protected void finish() {
		loadScheduleDTO();
		loadLocationContactDTO();
		loadSecondaryLocationContactDTO();
		loadLocationDTO();
		loadPricingDTO();
		loadRoutingDTO();
		loadCustomFieldGroupDTOs();
		loadShipmentGroupDTO();
		loadSurveyDTOs();
		loadDocumentDTOs();
		loadDeliverablesGroupDTO();
		loadConfigurationDTO();
		loadAssignmentDTO();
		loadTemplateDTO();
	}

	@Override
	public GetTemplateUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public TemplateDTO andReturn() {
		return templateDTOBuilder.build();
	}
}
