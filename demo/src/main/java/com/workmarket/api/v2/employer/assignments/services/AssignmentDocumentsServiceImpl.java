package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AssignmentDocumentsServiceImpl implements AssignmentDocumentsService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public Set<DocumentDTO> get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetDocumentsUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public Set<DocumentDTO> update(String id, Set<DocumentDTO> documentDTOs, boolean readyToSend) throws WorkActionException, ValidationException {
		return useCaseFactory
			.getUseCase(UpdateDocumentsUseCase.class, id, documentDTOs, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
