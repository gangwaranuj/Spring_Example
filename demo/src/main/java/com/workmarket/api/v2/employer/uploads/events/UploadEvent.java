package com.workmarket.api.v2.employer.uploads.events;

import com.workmarket.api.v2.employer.uploads.visitors.Visitor;
import com.workmarket.service.web.AbstractWebRequestContextAware;

import java.io.Serializable;

public abstract class UploadEvent extends AbstractWebRequestContextAware implements Serializable {
	private static final long serialVersionUID = 5284925432040245429L;

	private final Long userId;

	protected UploadEvent(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public abstract String getUuid();
	public abstract void accept(Visitor visitor);

	@Override
	public String toString() {
		return "UploadEvent{" +
			"userId=" + userId +
			", uuid=" + getUuid() +
			'}';
	}
}
