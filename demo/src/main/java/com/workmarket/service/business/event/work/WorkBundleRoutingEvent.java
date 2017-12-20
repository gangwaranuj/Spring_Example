package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

public class WorkBundleRoutingEvent extends Event {

	private static final long serialVersionUID = 7342551789545872986L;
	private Long workBundleId;

	public WorkBundleRoutingEvent() {
	}

	public WorkBundleRoutingEvent(Long workBundleId) {
		this.workBundleId = workBundleId;
	}

	public Long getWorkBundleId() {
		return workBundleId;
	}

	public void setWorkBundleId(Long workBundleId) {
		this.workBundleId = workBundleId;
	}
}
