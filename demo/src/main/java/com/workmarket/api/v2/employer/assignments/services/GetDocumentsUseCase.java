package com.workmarket.api.v2.employer.assignments.services;


import com.google.api.client.util.Sets;
import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;

@Component
@Scope("prototype")
public class GetDocumentsUseCase
	extends AbstractAssignmentUseCase<GetDocumentsUseCase, Set<DocumentDTO>> {

	public GetDocumentsUseCase(String id) {
		this.id = id;
	}

	@Override
	protected GetDocumentsUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
	}

	@Override
	protected void init() throws WorkActionException {
		getUser();
		generateWorkRequest();
		getWorkResponse();
	}

	@Override
	protected void prepare() {
		getWork();
		getDocuments();
	}

	@Override
	protected void finish() {
		loadDocumentDTOs();
	}

	@Override
	protected GetDocumentsUseCase handleExceptions() throws WorkActionException {
		handleWorkActionException();
		return this;
	}

	@Override
	public Set<DocumentDTO> andReturn() {
		Set<DocumentDTO> documentDTOs = Sets.newHashSet();

		for (DocumentDTO.Builder builder : documentDTOBuilders) {
			documentDTOs.add(builder.build());
		}

		return documentDTOs;
	}
}
