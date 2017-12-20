package com.workmarket.domains.velvetrope.guest;

import com.workmarket.domains.model.User;
import com.workmarket.velvetrope.AbstractGuest;

public class UserGuest extends AbstractGuest<User> {

	public UserGuest(User user) {
		super(user, EMPTY_TOKEN);
	}

	@Override
	public long getId() {
		return getUser().getId();
	}

	@Override
	public Long getCompanyId() {
		return getUser().getCompany().getId();
	}
}
