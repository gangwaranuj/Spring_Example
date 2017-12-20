package com.workmarket.service.business.event.asset;

import com.workmarket.service.business.event.Event;

import java.util.List;

public class BuildDocumentationPackageEvent extends Event {

	private static final long serialVersionUID = -990611812918613402L;
	private List<Long> userIds;
	private Long groupId;
	private Long downloaderId;

	public BuildDocumentationPackageEvent(Long downloaderId, Long groupId, List<Long> userIds) {
		this.userIds = userIds;
		this.groupId = groupId;
		this.downloaderId = downloaderId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public Long getDownloaderId() {
		return downloaderId;
	}

	public void setDownloaderId(Long downloaderId) {
		this.downloaderId = downloaderId;
	}

}
