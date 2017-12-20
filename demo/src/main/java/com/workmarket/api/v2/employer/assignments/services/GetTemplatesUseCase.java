package com.workmarket.api.v2.employer.assignments.services;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.common.collect.ImmutableList;

import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.service.infra.business.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class GetTemplatesUseCase implements UseCase<GetTemplatesUseCase, ImmutableList<Map>>  {

	@Autowired private WorkTemplateService workTemplateService;
	@Autowired private AuthenticationService authenticationService;

	private static final String TEMPLATE_ID_KEY = "id";
	private static final String TEMPLATE_NAME_KEY = "name";
	private static final String TEMPLATE_CLIENT_ID_KEY = "clientId";

	private List<Map> templates = Lists.newArrayList();

	@Override
	public GetTemplatesUseCase execute() throws Exception {
		findAllActiveWorkTemplates();
		return this;
	}

	@Override
	public ImmutableList<Map> andReturn() {
		return ImmutableList.copyOf(templates);
	}

	private void findAllActiveWorkTemplates() {
		Long companyId = authenticationService.getCurrentUser().getCompany().getId();
		findAllActiveWorkTemplates(companyId);
	}

	@VisibleForTesting
	void findAllActiveWorkTemplates(final Long companyId) {
		Map<String, Map<String, Object>> templatesMap = workTemplateService.findAllActiveWorkTemplatesWorkNumberNameMap(companyId, null);
		for (Map.Entry<String, Map<String, Object>> entry : templatesMap.entrySet()) {
			Map<String, String> result = Maps.newHashMap();
			final String templateName = entry.getValue().get("template_name") != null ? entry.getValue().get("template_name").toString() : "";
			final String clientId = entry.getValue().get("client_id") != null ? entry.getValue().get("client_id").toString() : "";
			result.put(TEMPLATE_ID_KEY, entry.getKey());
			result.put(TEMPLATE_NAME_KEY, templateName);
			result.put(TEMPLATE_CLIENT_ID_KEY, clientId);
			templates.add(result);
		}
	}

}
