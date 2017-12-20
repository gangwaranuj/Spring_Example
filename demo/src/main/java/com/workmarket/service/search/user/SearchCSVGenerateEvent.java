package com.workmarket.service.search.user;

import com.workmarket.service.business.event.Event;
import com.workmarket.search.request.user.PeopleSearchRequest;

public class SearchCSVGenerateEvent extends Event {

	private final PeopleSearchRequest request;

	public SearchCSVGenerateEvent(PeopleSearchRequest request) {
		this.request = request;
	}

	public PeopleSearchRequest getRequest() {
		return this.request;
	}
}
