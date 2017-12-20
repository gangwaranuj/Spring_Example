package com.workmarket.service.business.upload.users.model;


import java.io.Serializable;

public class BulkUserUploadRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uuid;
	private Long userId;

	public BulkUserUploadRequest(String uuid, Long userId) {
		this.uuid = uuid;
		this.userId = userId;
	}

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
