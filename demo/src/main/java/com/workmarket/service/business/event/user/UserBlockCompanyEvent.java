package com.workmarket.service.business.event.user;

import com.workmarket.service.business.event.Event;

public class UserBlockCompanyEvent extends Event {

	private static final long serialVersionUID = 506879883206635368L;
	private long companyId;
	private long userId;

	public UserBlockCompanyEvent(long companyId, long userId) {
		this.companyId = companyId;
		this.userId = userId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public long getUserId() {
		return userId;
	}
}
