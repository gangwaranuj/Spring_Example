package com.workmarket.service.business.event;

import com.workmarket.service.business.upload.users.model.BulkUserUploadRequest;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;


public class BulkUserUploadStarterEvent extends Event {

	private static final long serialVersionUID = 7379364832567744714L;

	final private BulkUserUploadRequest uploadRequest;
	final private BulkUserUploadResponse response;
	final private boolean orgEnabledForUser;

	public BulkUserUploadStarterEvent(final BulkUserUploadRequest uploadRequest,
	                                  final BulkUserUploadResponse response,
	                                  final boolean orgEnabledForUser) {
		this.uploadRequest = uploadRequest;
		this.response = response;
		this.orgEnabledForUser = orgEnabledForUser;
	}

	public BulkUserUploadRequest getUploadRequest() {
		return uploadRequest;
	}

	public BulkUserUploadResponse getResponse() {
		return response;
	}

	public boolean isOrgEnabledForUser() {
		return orgEnabledForUser;
	}
}
