package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AssignmentCustomFieldGroupsServiceImpl implements AssignmentCustomFieldGroupsService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public Set<CustomFieldGroupDTO> get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetCustomFieldGroupsUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public Set<CustomFieldGroupDTO> update(String id, Set<CustomFieldGroupDTO> customFieldGroupDTOs, boolean readyToSend) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateCustomFieldGroupsUseCase.class, id, customFieldGroupDTOs, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
