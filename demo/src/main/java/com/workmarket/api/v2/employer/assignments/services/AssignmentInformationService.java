package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.AssignmentInformationDTO;
import com.workmarket.thrift.core.ValidationException;

public interface AssignmentInformationService {
	AssignmentInformationDTO get(AssignmentDTO assignmentDTO) throws ValidationException;
}
