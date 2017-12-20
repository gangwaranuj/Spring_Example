package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.search.IndexerEvent;

public class WorkUpdateSearchIndexByCompanyEvent extends IndexerEvent {
	private static final long serialVersionUID = -7945002793046337224L;

	private Long companyId;

	public WorkUpdateSearchIndexByCompanyEvent(Long companyId) {
		this.companyId = companyId;
	}

	public Long getCompanyId() {
		return companyId;
	}
}
