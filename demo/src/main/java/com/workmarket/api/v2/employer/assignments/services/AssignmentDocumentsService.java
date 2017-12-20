package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

import java.util.Set;

public interface AssignmentDocumentsService {
	Set<DocumentDTO> get(String id) throws WorkActionException;
	Set<DocumentDTO> update(String id, Set<DocumentDTO> documentDTOs, boolean readyToSend) throws WorkActionException, ValidationException;
}
