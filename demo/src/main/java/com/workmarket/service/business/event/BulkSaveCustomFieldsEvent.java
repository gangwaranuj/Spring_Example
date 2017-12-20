package com.workmarket.service.business.event;

import com.workmarket.domains.model.customfield.BulkSaveCustomFieldsRequest;

import java.util.List;

public class BulkSaveCustomFieldsEvent extends Event {

	private static final long serialVersionUID = -5751385486737435131L;

	private List<BulkSaveCustomFieldsRequest> requests;
	private Long masqueradeUserId;

	public BulkSaveCustomFieldsEvent(List<BulkSaveCustomFieldsRequest> requests, Long masqueradeUserId) {
		this.requests = requests;
		this.masqueradeUserId = masqueradeUserId;
	}

	public BulkSaveCustomFieldsEvent() {
		super();
	}

	public List<BulkSaveCustomFieldsRequest> getRequests() {
		return requests;
	}

	public Long getMasqueradeUserId() {
		return masqueradeUserId;
	}
}
