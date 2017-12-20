package com.workmarket.service.business.event;

public class UnlockCompanyEvent extends Event {

	private static final long serialVersionUID = -1021840836742776441L;
	private long companyId;

	public UnlockCompanyEvent(long companyId) {
		this.companyId = companyId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

}
