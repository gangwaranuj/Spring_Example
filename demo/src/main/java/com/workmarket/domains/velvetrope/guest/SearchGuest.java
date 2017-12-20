package com.workmarket.domains.velvetrope.guest;

import com.workmarket.search.model.SearchUser;
import com.workmarket.velvetrope.AbstractGuest;

public class SearchGuest extends AbstractGuest<SearchUser> {

	public SearchGuest(SearchUser user) {
		super(user, EMPTY_TOKEN);
	}

	@Override
	public long getId() {
		return getUser().getId();
	}

	@Override
	public Long getCompanyId() {
		return getUser().getCompanyId();
	}
}
