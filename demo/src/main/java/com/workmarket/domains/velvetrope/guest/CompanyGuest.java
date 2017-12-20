package com.workmarket.domains.velvetrope.guest;

import com.workmarket.domains.model.Company;
import com.workmarket.velvetrope.AbstractGuest;

public class CompanyGuest extends AbstractGuest<Company> {

	public CompanyGuest(Company user) {
		super(user, EMPTY_TOKEN);
	}

	@Override
	public long getId() {
		return getUser().getId();
	}

	@Override
	public Long getCompanyId() {
		return getUser().getId();
	}
}
