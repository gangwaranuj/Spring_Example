package com.workmarket.api.v2.employer.assignments.services;

import org.springframework.util.Assert;

public abstract class BaseCreateAssignmentUseCase<T, K> extends AbstractAssignmentUseCase<T, K> {
	@Override
	protected void failFast() {
		Assert.notNull(assignmentDTO);
	}

	@Override
	protected void init() {
		getUser();
	}

	@Override
	protected void prepare() {
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
		createWork();
	}

	@Override
	protected void process() {
		loadAddress();
		loadLocationContact();
		loadSecondaryLocationContact();
		loadBuyer();
		loadSupportContact();
		loadLocation();
		loadSchedule();
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
}
