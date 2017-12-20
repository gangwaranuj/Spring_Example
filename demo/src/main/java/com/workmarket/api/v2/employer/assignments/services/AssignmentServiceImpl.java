package com.workmarket.api.v2.employer.assignments.services;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {
	@Autowired UseCaseFactory useCaseFactory;

	private static final List<Integer> ASSIGNMENT_COPY_QUANTITIES = new ImmutableList.Builder<Integer>()
		.add(1)
		.add(2)
		.add(3)
		.add(4)
		.add(5)
		.add(6)
		.add(7)
		.add(8)
		.add(9)
		.add(10)
		.build();

	@Override
	public AssignmentDTO get(String id) throws WorkActionException {
		return useCaseFactory
			.getUseCase(GetAssignmentUseCase.class, id)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public AssignmentDTO create(AssignmentDTO assignmentDTO, boolean readyToSend) throws ValidationException, WorkAuthorizationException {
		return useCaseFactory
			.getUseCase(CreateAssignmentUseCase.class, assignmentDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public List<AssignmentDTO> createMultiple(AssignmentDTO assignmentDTO, int numberOfCopies, boolean readyToSend) throws ValidationException, WorkAuthorizationException {
		if (!ASSIGNMENT_COPY_QUANTITIES.contains(numberOfCopies)) {
			throw new ValidationException("Invalid number of copies: " + numberOfCopies, null);
		}

		final List<AssignmentDTO> results = Lists.newArrayList();

		for (int ix = 0; ix < numberOfCopies; ix++) {
			results.add(create(assignmentDTO, readyToSend));
		}

		return results;
	}

	@Override
	public AssignmentDTO update(String id, AssignmentDTO assignmentDTO, boolean readyToSend) throws ValidationException, WorkActionException, WorkAuthorizationException {
		return useCaseFactory
			.getUseCase(UpdateAssignmentUseCase.class, id, assignmentDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public List<ApiBaseError> validate(AssignmentDTO assignmentDTO, boolean readyToSend) {
		return useCaseFactory
			.getUseCase(ValidateAssignmentUseCase.class, assignmentDTO, readyToSend)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public List<Integer> getAssignmentCopyQuantities() {
		return ASSIGNMENT_COPY_QUANTITIES;
	}
}
