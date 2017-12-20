package com.workmarket.api.v2.employer.assignments.services;

import com.google.common.collect.Sets;
import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;

@Component
@Scope("prototype")
public class UpdateDocumentsUseCase
	extends AbstractAssignmentUseCase<UpdateDocumentsUseCase, Set<DocumentDTO>> {

	public UpdateDocumentsUseCase(String id, Set<DocumentDTO> documentDTOs, boolean readyToSend) {
		this.id = id;
		this.documentDTOs = documentDTOs;
		this.readyToSend = readyToSend;
	}

	@Override
	protected UpdateDocumentsUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(id);
		Assert.notNull(documentDTOs);
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

		copyDocumentDTOs();
	}

	@Override
	protected void process() {
		loadDocuments();
		loadWork();
	}

	@Override
	protected void save() throws ValidationException, WorkAuthorizationException {
		generateWorkSaveRequest();
		saveWork();
	}

	@Override
	protected void finish() {
		getWork();
		getDocuments();
		loadDocumentDTOs();
	}

	@Override
	protected UpdateDocumentsUseCase handleExceptions() throws ValidationException, WorkActionException {
		handleValidationException();
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
