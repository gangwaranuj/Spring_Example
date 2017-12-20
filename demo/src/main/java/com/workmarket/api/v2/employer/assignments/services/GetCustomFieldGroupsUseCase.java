package com.workmarket.api.v2.employer.assignments.services;

import com.google.api.client.util.Sets;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;

@Component
@Scope("prototype")
public class GetCustomFieldGroupsUseCase
	extends AbstractAssignmentUseCase<GetCustomFieldGroupsUseCase, Set<CustomFieldGroupDTO>> {

	public GetCustomFieldGroupsUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetCustomFieldGroupsUseCase me() {
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
		getCustomFieldGroups();
	}

	@Override
	protected void finish() {
		loadCustomFieldGroupDTOs();
	}

	@Override
	protected GetCustomFieldGroupsUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public Set<CustomFieldGroupDTO> andReturn() {
		Set<CustomFieldGroupDTO> customFieldGroupDTOs = Sets.newHashSet();

		for (CustomFieldGroupDTO.Builder builder : customFieldGroupDTOBuilders) {
			customFieldGroupDTOs.add(builder.build());
		}

		return customFieldGroupDTOs;
	}
}
