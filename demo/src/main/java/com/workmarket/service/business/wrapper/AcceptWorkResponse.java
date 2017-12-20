package com.workmarket.service.business.wrapper;

import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.status.AcceptWorkStatus;

public class AcceptWorkResponse extends MessageResponse {

	private WorkResource activeResource;
	private Work work;

	public AcceptWorkResponse() {
		super();
		this.status = AcceptWorkStatus.NONE;
	}

	public AcceptWorkResponse(AcceptWorkStatus status) {
		super(status);
	}

	public AcceptWorkResponse(AcceptWorkStatus status, String message) {
		super(status, message);
	}

	public AcceptWorkResponse(Work work, AcceptWorkStatus status, String message) {
		super(status, message);
		this.work = work;
	}

	public AcceptWorkResponse(Work work, WorkResource activeResource, AcceptWorkStatus status, String message) {
		super(status, message);
		this.work = work;
		this.activeResource = activeResource;
	}

	public static AcceptWorkResponse success() {
		return new AcceptWorkResponse(AcceptWorkStatus.SUCCESS);
	}

	public static AcceptWorkResponse fail() {
		return new AcceptWorkResponse(AcceptWorkStatus.FAILURE);
	}

	public WorkResource getActiveResource() {
		return activeResource;
	}

	public AcceptWorkResponse setActiveResource(WorkResource activeResource) {
		this.activeResource = activeResource;
		return this;
	}

	public boolean hasActiveResource() {
		return this.activeResource != null;
	}

	public Work getWork() {
		return work;
	}

	public AcceptWorkResponse setWork(Work work) {
		this.work = work;
		return this;
	}

	public boolean hasWork() {
		return this.work != null;
	}
}
