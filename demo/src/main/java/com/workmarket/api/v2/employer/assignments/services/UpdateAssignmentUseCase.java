package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateAssignmentUseCase
	extends AbstractAssignmentUseCase<UpdateAssignmentUseCase, AssignmentDTO> {

	public UpdateAssignmentUseCase(String id, AssignmentDTO assignmentDTO, boolean readyToSend) {
		this.id = id;
		this.assignmentDTO = assignmentDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateAssignmentUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(assignmentDTO);
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
		getLocation();
		getLocationContact();
		getSecondaryLocationContact();
		getBuyer();
		getSupportContact();
		getPricing();
		getRouting();
		getCustomFieldGroups();
		getShipments();
		getSurveys();
		getDeliverablesGroup();
		getDocuments();
		getConfiguration();

		copyAssignmentDTO();

		getScheduleDTO();
		getLocationDTO();
		getLocationContactDTO();
		getSecondaryLocationContactDTO();
		getPricingDTO();
		getRoutingDTO();
		getCustomFieldGroupDTOs();
		getShipmentDTOs();
		getSurveyDTOs();
		getDocumentDTOs();
		getDeliverablesGroupDTO();
		getConfigurationDTO();

		createAddress();
		createDocumentUploads();
	}

	@Override
	protected void process() {
		loadAddress();
		loadLocation();
		loadSchedule();
		loadLocationContact();
		loadSecondaryLocationContact();
		loadBuyer();
		loadSupportContact();
		loadPricing();
		loadRouting();
		loadCustomFieldGroups();
		loadShipments();
		loadSurveys();
		loadDocuments();
		loadDeliverablesGroup();
		loadConfiguration();
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
		getSchedule();
		getLocation();
		getLocationContact();
		getSecondaryLocationContact();
		getBuyer();
		getSupportContact();
		getPricing();
		getRouting();
		getCustomFieldGroups();
		getShipments();
		getSurveys();
		getDeliverablesGroup();
		getDocuments();
		getConfiguration();
		loadScheduleDTO();
		loadLocationDTO();
		loadLocationContactDTO();
		loadSecondaryLocationContactDTO();
		loadPricingDTO();
		loadRoutingDTO();
		loadCustomFieldGroupDTOs();
		loadShipmentGroupDTO();
		loadSurveyDTOs();
		loadDocumentDTOs();
		loadDeliverablesGroupDTO();
		loadConfigurationDTO();
		loadAssignmentDTO();
		refreshAssignmentDTO();
		sendWork();
	}

	@Override
	public UpdateAssignmentUseCase handleExceptions() throws ValidationException, WorkActionException, WorkAuthorizationException {
		handleValidationException();
		handleWorkActionException();
		handleWorkAuthorizationException();
		return this;
	}

	@Override
	public AssignmentDTO andReturn() {
		return assignmentDTOBuilder.build();
	}

	private void refreshAssignmentDTO() {
		Assert.notNull(workResponse);
		assignmentDTOBuilder.setId(workResponse.getWork().getWorkNumber());
	}
}
