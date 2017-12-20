package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class WorkViewRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long workId;
	private WorkViewType viewType;
	private Long userId;

	public WorkViewRequest() {
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public WorkViewType getViewType() {
		return viewType;
	}

	public void setViewType(WorkViewType viewType) {
		this.viewType = viewType;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}