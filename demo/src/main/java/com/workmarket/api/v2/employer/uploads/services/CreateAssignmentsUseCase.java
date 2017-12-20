package com.workmarket.api.v2.employer.uploads.services;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentService;
import com.workmarket.api.v2.employer.assignments.services.UseCase;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
@Scope("prototype")
public class CreateAssignmentsUseCase implements UseCase<CreateAssignmentsUseCase, List<AssignmentDTO>> {
	@Autowired private AssignmentService assignmentService;
	@Autowired private PreviewStorageService previewStorageService;

	private List<AssignmentDTO> createdAssignmentDTOs = Lists.newArrayList();
	private String uuid;
	private boolean readyToSend;

	public CreateAssignmentsUseCase(String uuid, boolean readyToSend) {
		this.uuid = uuid;
		this.readyToSend = readyToSend;
	}

	@Override
	public CreateAssignmentsUseCase execute() throws ValidationException, WorkAuthorizationException {
		Assert.notNull(uuid);

		long count = previewStorageService.size(uuid);

		for (int i = 0; i < count; i++) {
			Optional<PreviewDTO> preview = previewStorageService.get(uuid, i);
			if (preview.isPresent()) {
				createdAssignmentDTOs.add(assignmentService.create(preview.get().getAssignmentDTO(), readyToSend));
			}
		}

		return this;
	}

	@Override
	public List<AssignmentDTO> andReturn() {
		return createdAssignmentDTOs;
	}

	public CreateAssignmentsUseCase handleExceptions() {
		return this;
	}
}
