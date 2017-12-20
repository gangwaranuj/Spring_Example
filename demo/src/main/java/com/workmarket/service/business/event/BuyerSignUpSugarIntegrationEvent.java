package com.workmarket.service.business.event;

public class BuyerSignUpSugarIntegrationEvent extends Event {

	private static final long serialVersionUID = 282798175680712721L;

	private Long companyId;

	public BuyerSignUpSugarIntegrationEvent(Long companyId) {
		this.companyId = companyId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

}
