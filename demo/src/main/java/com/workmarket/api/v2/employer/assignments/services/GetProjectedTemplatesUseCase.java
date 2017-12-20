package com.workmarket.api.v2.employer.assignments.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.work.model.WorkTemplatePagination;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.ProjectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope("prototype")
class GetProjectedTemplatesUseCase implements UseCase<GetProjectedTemplatesUseCase, ImmutableList<Map>> {
	@Autowired private WorkTemplateService workTemplateService;
	@Autowired private AuthenticationService authenticationService;

	private static final Map<String, String> FIELD_NAMES = ImmutableMap.of(
		"id", "workNumber",
		"name", "templateName",
		"description", "templateDescription"
	);

	private String[] fields;
	private WorkTemplatePagination pagination;

	public GetProjectedTemplatesUseCase(String[] fields) {
		this.fields = fields;
	}

	@Override
	public GetProjectedTemplatesUseCase execute() throws Exception {
		createPagination();

		findAllActiveWorkTemplates();
		projectResults();

		return this;
	}

	@Override
	public ImmutableList<Map> andReturn() {
		return ImmutableList.copyOf(pagination.getProjectionResults());
	}

	private void projectResults() throws Exception {
		pagination.setProjectionResults(ProjectionUtilities.projectAsArray(pagination.getProjection(), FIELD_NAMES, pagination.getResults()));
	}

	private void findAllActiveWorkTemplates() {
		workTemplateService.findAllActiveWorkTemplates(authenticationService.getCurrentUser().getCompany().getId(), pagination);
	}

	private void createPagination() {
		pagination = new WorkTemplatePagination(true);
		pagination.setProjection(fields);
	}
}
