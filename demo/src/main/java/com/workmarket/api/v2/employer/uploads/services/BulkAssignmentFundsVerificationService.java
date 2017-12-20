package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;

import java.util.List;

public interface BulkAssignmentFundsVerificationService {
	boolean hasSufficientFundsForAssignments(List<AssignmentDTO> assignmentDTOs) throws Exception;
}
