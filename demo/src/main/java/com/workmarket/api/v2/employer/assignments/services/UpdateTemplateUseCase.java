package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class UpdateTemplateUseCase
	extends AbstractAssignmentUseCase<UpdateTemplateUseCase, TemplateDTO> {

	public UpdateTemplateUseCase(String id, TemplateDTO templateDTO) {
		this.id = id;
		this.templateDTO = templateDTO;
	}

	@Override
	protected UpdateTemplateUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(templateDTO);
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
		getTemplate();
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
		getDocuments();
		getDeliverablesGroup();
		getConfiguration();

		copyTemplateDTO();

		getAssignmentDTO();
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
		loadSchedule();
		loadAddress();
		loadLocation();
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
		loadTemplate();
	}

	@Override
	protected void save() throws ValidationException {
		generateWorkSaveRequest();
		saveTemplate();
	}

	@Override
	protected void finish() {
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
		loadTemplateDTO();
		refreshTemplateDTO();
	}

	@Override
	public UpdateTemplateUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
		handleWorkActionException();
		return this;
	}

	@Override
	public TemplateDTO andReturn() {
		return templateDTOBuilder.build();
	}

	private void refreshTemplateDTO() {
		Assert.notNull(workResponse);
		templateDTOBuilder.setId(workResponse.getWork().getWorkNumber());
	}
}
