package com.workmarket.thrift.work;

import com.workmarket.domains.work.service.audit.WorkActionRequest;

public class WorkActionException extends Exception {
	private static final long serialVersionUID = 1L;

	private WorkRequest originalWorkRequest;
	private AddResourcesToWorkRequest originalAddResourcesToWorkRequest;
	private ResourceNoteRequest originalResourceNoteRequest;
	private WorkActionRequest originalWorkActionRequest;
	private WorkQuestionRequest originalWorkQuestionRequest;

	public WorkActionException() {
	}

	public WorkActionException(String message) {
		super(message);
	}

	public WorkActionException(Throwable cause) {
		super(cause);
	}

	public WorkActionException(AddResourcesToWorkRequest originalAddResourcesToWorkRequest) {
		this.originalAddResourcesToWorkRequest = originalAddResourcesToWorkRequest;
	}

	public WorkActionException(WorkActionRequest originalWorkActionRequest) {
		this.originalWorkActionRequest = originalWorkActionRequest;
	}

	public WorkActionException(String message, ResourceNoteRequest originalResourceNoteRequest) {
		super(message);
		this.originalResourceNoteRequest = originalResourceNoteRequest;
	}

	public WorkActionException(String message, WorkActionRequest originalWorkActionRequest) {
		super(message);
		this.originalWorkActionRequest = originalWorkActionRequest;
	}

	public WorkRequest getOriginalWorkRequest() {
		return originalWorkRequest;
	}

	public AddResourcesToWorkRequest getOriginalAddResourcesToWorkRequest() {
		return originalAddResourcesToWorkRequest;
	}

	public ResourceNoteRequest getOriginalResourceNoteRequest() {
		return originalResourceNoteRequest;
	}

	public String getWhy() {
		return super.getMessage();
	}

	public WorkActionRequest getOriginalWorkActionRequest() {
		return originalWorkActionRequest;
	}

	public WorkQuestionRequest getOriginalWorkQuestionRequest() {
		return originalWorkQuestionRequest;
	}
}