package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

/**
 * Created by andy on 4/7/15.
 */
public class WorkBundleCancelSubmitEvent extends Event {
	private static final long serialVersionUID = 3282356000507820924L;
	private Long workId;

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}
}
