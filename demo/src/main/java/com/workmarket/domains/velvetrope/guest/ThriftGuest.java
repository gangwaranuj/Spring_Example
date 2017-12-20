package com.workmarket.domains.velvetrope.guest;

import com.workmarket.thrift.core.User;
import com.workmarket.velvetrope.AbstractGuest;

public class ThriftGuest extends AbstractGuest<User> {

	public ThriftGuest(User user) {
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
