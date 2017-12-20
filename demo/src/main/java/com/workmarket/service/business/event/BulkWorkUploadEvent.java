package com.workmarket.service.business.event;

import com.workmarket.thrift.work.WorkSaveRequest;

import java.util.List;

public class BulkWorkUploadEvent extends Event {

	private static final long serialVersionUID = -744531513186565844L;
	private List<WorkSaveRequest> saveRequests;

	private final String uploadKey;
	private final String uploadSizeKey;

	public List<WorkSaveRequest> getSaveRequests() {
		return saveRequests;
	}

	public void setSaveRequest(List<WorkSaveRequest> saveRequests) {
		this.saveRequests = saveRequests;
	}

	public String getUploadSizeKey() {
		return uploadSizeKey;
	}

	public String getUploadKey() {
		return uploadKey;
	}

	public BulkWorkUploadEvent(final List<WorkSaveRequest> saveRequests, String uploadKey, String uploadSizeKey) {
		this.saveRequests = saveRequests;
		this.uploadKey = uploadKey;
		this.uploadSizeKey = uploadSizeKey;
	}
}
