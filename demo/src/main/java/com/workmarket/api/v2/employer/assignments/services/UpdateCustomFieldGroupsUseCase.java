package com.workmarket.api.v2.employer.assignments.services;

import com.google.api.client.util.Sets;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;

@Component
@Scope("prototype")
public class UpdateCustomFieldGroupsUseCase
	extends AbstractAssignmentUseCase<UpdateCustomFieldGroupsUseCase, Set<CustomFieldGroupDTO>> {

	public UpdateCustomFieldGroupsUseCase(String id, Set<CustomFieldGroupDTO> customFieldGroupDTOs, boolean readyToSend) {
		this.id = id;
		this.customFieldGroupDTOs = customFieldGroupDTOs;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateCustomFieldGroupsUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(customFieldGroupDTOs);
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

		copyCustomFieldGroupDTOs();
	}

	@Override
	protected void process() {
		loadCustomFieldGroups();
		loadWork();
	}

	@Override
	protected void save() throws ValidationException, WorkAuthorizationException {
		generateWorkSaveRequest();
		saveWork();
	}

	@Override
	protected void finish() {
		getWork();
		getCustomFieldGroups();
		loadCustomFieldGroupDTOs();
	}

	@Override
	protected UpdateCustomFieldGroupsUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
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
