package com.workmarket.service.business.wrapper;

import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.service.business.status.WorkNegotiationResponseStatus;

public class WorkNegotiationResponse extends MessageResponse {

	private Long workNegotiationId;

	public WorkNegotiationResponse() {
		super();
		this.status = WorkNegotiationResponseStatus.NONE;
	}

	public WorkNegotiationResponse(WorkNegotiationResponseStatus status) {
		super(status);
	}

	public WorkNegotiationResponse(WorkNegotiationResponseStatus status, Long workNegotiationId) {
		super(status);
		this.workNegotiationId = workNegotiationId;
	}

	public WorkNegotiationResponse(WorkNegotiationResponseStatus status, String message) {
		super(status, message);
	}

	public static WorkNegotiationResponse success() {
		return new WorkNegotiationResponse(WorkNegotiationResponseStatus.SUCCESS);
	}

	public static WorkNegotiationResponse fail() {
		return new WorkNegotiationResponse(WorkNegotiationResponseStatus.FAILURE);
	}

	public Long getWorkNegotiationId() {
		return workNegotiationId;
	}

	public void setWorkNegotiationId(Long workNegotiationId) {
		this.workNegotiationId = workNegotiationId;
	}
}
