package com.workmarket.service.business.event;

import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;

public class BulkUserUploadFinishedEvent extends Event {

	private static final long serialVersionUID = 7373364832567744714L;

	private BulkUserUploadResponse response;

	public BulkUserUploadFinishedEvent(BulkUserUploadResponse response) {
		this.response = response;
	}

	public BulkUserUploadResponse getResponse() {
		return response;
	}
}
