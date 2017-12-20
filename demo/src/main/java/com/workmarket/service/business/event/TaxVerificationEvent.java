package com.workmarket.service.business.event;

import java.util.List;

public class TaxVerificationEvent extends Event {

	private List<Long> ids;
	public TaxVerificationEvent(List<Long> transitionedEntityIds) {
		this.ids = transitionedEntityIds;
	}

	public List<Long> getIds() {
		return ids;
	}
}
