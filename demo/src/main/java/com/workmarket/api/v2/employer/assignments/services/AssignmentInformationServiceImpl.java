package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.AssignmentInformationDTO;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentInformationServiceImpl implements AssignmentInformationService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public AssignmentInformationDTO get(AssignmentDTO assignmentDTO) throws ValidationException {
		return useCaseFactory
			.getUseCase(BuildAssignmentUseCase.class, assignmentDTO, false)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
