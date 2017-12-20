package com.workmarket.service.business.event;

public class CompanyDueInvoicesEvent extends Event {

	private static final long serialVersionUID = -906798867244901995L;
	private long companyId;

	public CompanyDueInvoicesEvent(long companyId) {
		this.companyId = companyId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

}
