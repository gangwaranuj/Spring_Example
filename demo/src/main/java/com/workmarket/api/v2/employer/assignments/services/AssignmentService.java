package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;

import java.util.List;

public interface AssignmentService {
	AssignmentDTO get(String id) throws WorkActionException;
	AssignmentDTO create(AssignmentDTO assignmentDTO, boolean readyToSend) throws ValidationException, WorkAuthorizationException;
	List<AssignmentDTO> createMultiple(AssignmentDTO assignmentDTO, int numberOfCopies, boolean readyToSend) throws ValidationException, WorkAuthorizationException;
	AssignmentDTO update(String id, AssignmentDTO assignmentDTO, boolean readyToSend) throws ValidationException, WorkActionException, WorkAuthorizationException;
	List<ApiBaseError> validate(AssignmentDTO assignmentDTO, boolean readyToSend);
	List<Integer> getAssignmentCopyQuantities();
}
