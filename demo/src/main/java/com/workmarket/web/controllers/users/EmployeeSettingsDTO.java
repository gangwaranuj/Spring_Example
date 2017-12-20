package com.workmarket.web.controllers.users;

import com.workmarket.domains.model.Company;

public class EmployeeSettingsDTO {
	private boolean hidePricing;

	public EmployeeSettingsDTO setHidePricing(boolean hidePricing) {
		this.hidePricing = hidePricing;
		return this;
	}

	public boolean isHidePricing() {
		return hidePricing;
	}

	public static EmployeeSettingsDTO newInstance(Company company) {
		return new EmployeeSettingsDTO()
			.setHidePricing(company.isHidePricing());
	}
}
