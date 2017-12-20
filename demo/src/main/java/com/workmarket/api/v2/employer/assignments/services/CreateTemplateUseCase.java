package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
class CreateTemplateUseCase
	extends AbstractAssignmentUseCase<CreateTemplateUseCase, TemplateDTO> {

	public CreateTemplateUseCase(TemplateDTO templateDTO) {
		this.templateDTO = templateDTO;
	}

	@Override
	protected CreateTemplateUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(templateDTO);
	}

	@Override
	protected void init() {
		getUser();
	}

	@Override
	protected void prepare() {
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

		copyScheduleDTO();
		copyLocationDTO();
		copyLocationContactDTO();
		copySecondaryLocationContactDTO();
		copyPricingDTO();
		copyRoutingDTO();
		copyCustomFieldGroupDTOs();
		copyShipmentGroupDTO();
		copySurveyDTOs();
		copyDocumentDTOs();
		copyDeliverablesGroupDTO();
		copyConfigurationDTO();
		copyAssignmentDTO();
		copyTemplateDTO();

		createSchedule();
		createAddress();
		createLocation();
		createLocationContact();
		createSecondaryLocationContact();
		createBuyer();
		createSupportContact();
		createPricing();
		createRouting();
		createCustomFieldGroups();
		createShipments();
		createSurveys();
		createDocuments();
		createDocumentUploads();
		createDeliverablesGroup();
		createConfiguration();
		createTemplate();
		createWork();
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
		loadTemplate();
		loadWork();
	}

	@Override
	protected void save() throws ValidationException {
		generateWorkSaveRequest();
		saveTemplate();
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
		refreshTemplateDTO();
	}

	@Override
	public CreateTemplateUseCase handleExceptions() throws ValidationException {
		handleValidationException();
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
