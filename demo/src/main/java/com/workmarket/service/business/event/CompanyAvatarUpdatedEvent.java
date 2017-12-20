package com.workmarket.service.business.event;

public class CompanyAvatarUpdatedEvent extends Event {

	private static final long serialVersionUID = 7038783778470199684L;

	private Long companyId;
	
	public CompanyAvatarUpdatedEvent() {}
	
	public CompanyAvatarUpdatedEvent(Long companyId) {
		this.companyId = companyId;
	}

	public Long getCompanyId() {
		return companyId;
	}
}
