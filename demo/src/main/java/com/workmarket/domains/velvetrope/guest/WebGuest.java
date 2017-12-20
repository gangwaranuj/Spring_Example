package com.workmarket.domains.velvetrope.guest;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.velvetrope.AbstractGuest;

public class WebGuest extends AbstractGuest<ExtendedUserDetails> {
	public WebGuest(ExtendedUserDetails user, int token) {
		super(user, token);
	}

	public WebGuest(ExtendedUserDetails user) {
		this(user, EMPTY_TOKEN);
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
