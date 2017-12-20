package com.workmarket.service.business.event;


import com.workmarket.thrift.work.uploader.WorkUploadRequest;

public class BulkWorkUploadStarterEvent extends Event {

	private static final long serialVersionUID = 7379364832567744714L;

	private WorkUploadRequest uploadRequest;
	private Long userId;

	public BulkWorkUploadStarterEvent(WorkUploadRequest uploadRequest, Long userId) {
		this.uploadRequest = uploadRequest;
		this.userId = userId;
	}

	public WorkUploadRequest getUploadRequest() {
		return uploadRequest;
	}

	public Long getUserId() {
		return userId;
	}
}
