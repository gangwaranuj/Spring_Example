package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.RecurrenceDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class GetAssignmentUseCase
	extends AbstractAssignmentUseCase<GetAssignmentUseCase, AssignmentDTO> {

	@Autowired private RecurrenceService recurrenceService;

	public GetAssignmentUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetAssignmentUseCase me() {
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
		getDeliverablesGroup();
		getDocuments();
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
		loadRecurrenceDTO();
		loadAssignmentDTO();
	}

	@Override
	public GetAssignmentUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public AssignmentDTO andReturn() {
		return assignmentDTOBuilder.build();
	}

	protected void loadRecurrenceDTO() {
		if (workResponse != null && workResponse.getWork() != null) {
			this.recurrenceDTOBuilder = new RecurrenceDTO.Builder(recurrenceService.getRecurrence(workResponse.getWork().getId()));
		}
	}
}
