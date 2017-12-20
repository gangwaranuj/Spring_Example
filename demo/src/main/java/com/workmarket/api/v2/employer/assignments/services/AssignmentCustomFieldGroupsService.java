package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

import java.util.Set;

public interface AssignmentCustomFieldGroupsService {
	Set<CustomFieldGroupDTO> get(String id) throws WorkActionException;
	Set<CustomFieldGroupDTO> update(String id, Set<CustomFieldGroupDTO> customFieldGroupDTOs, boolean readyToSend) throws WorkActionException, ValidationException;
}
