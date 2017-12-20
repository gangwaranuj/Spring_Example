package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
@Scope("prototype")
public class CreateAssignmentUseCase
	extends BaseCreateAssignmentUseCase<CreateAssignmentUseCase, AssignmentDTO> {
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private RecurrenceServiceImpl recurrenceService;

	public CreateAssignmentUseCase() {
	}

	public CreateAssignmentUseCase(AssignmentDTO assignmentDTO, boolean readyToSend) {
		this.assignmentDTO = assignmentDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected CreateAssignmentUseCase me() {
		return this;
	}

	@Override
	protected void save() throws ValidationException, WorkAuthorizationException {
		generateWorkSaveRequest();
		saveWork();
		sendWebhooks();
		saveRecurrence();
	}

	private void sendWebhooks() {
		webHookEventService.onWorkCreated(workResponse.getWork().getId(), authenticationService.getCurrentUserCompanyId(), null);
	}

	protected void saveRecurrence() {
		this.recurrenceDTOBuilder = recurrenceService.createRecurrence(assignmentDTO, getCompany(), workResponse.getWork().getId(), workResponse.getWork().getTimeZone());
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
		getAddress();
		getPricing();
		getRouting();
		getCustomFieldGroups();
		getShipments();
		getSurveys();
		getDeliverablesGroup();
		getDocuments();
		getConfiguration();
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
		refreshAssignmentDTO();
		sendWork();
	}

	@Override
	public CreateAssignmentUseCase handleExceptions() throws ValidationException, WorkAuthorizationException {
		handleValidationException();
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
