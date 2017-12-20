package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

public class WorkBundleVendorRoutingEvent extends Event {
	private static final long serialVersionUID = 1683472886698079195L;
	private final Long workBundleId;

	public WorkBundleVendorRoutingEvent(Long workBundleId) {
		this.workBundleId = workBundleId;
	}

	public Long getWorkBundleId() {
		return workBundleId;
	}
}
