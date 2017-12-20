package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

public class WorkBundleDeclinedEvent extends Event {
	private static final long serialVersionUID = -2786868448058174568L;
	private Long workId;
	private Long userId;
	private Long onBehalfOfUserId;

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getOnBehalfOfUserId() {
		return onBehalfOfUserId;
	}

	public void setOnBehalfOfUserId(Long onBehalfOfUserId) {
		this.onBehalfOfUserId = onBehalfOfUserId;
	}
}
