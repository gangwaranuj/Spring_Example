package com.workmarket.api.v2.employer.settings.services;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GetCompanyPublicProfileUseCase
	extends AbstractGetCompanyProfileUseCase {

	public GetCompanyPublicProfileUseCase(String companyNumber) {
		super(companyNumber);
	}

	public GetCompanyPublicProfileUseCase() {}

	@Override
	public GetCompanyPublicProfileUseCase execute() {
		populateBaseCompanyProfile();
		return this;
	}
}
