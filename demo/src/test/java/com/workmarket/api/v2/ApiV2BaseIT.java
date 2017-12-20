package com.workmarket.api.v2;

import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.web.controllers.ControllerIT;

public class ApiV2BaseIT extends ControllerIT {

	protected void enableUniqueExternalId(){
		CompanyPreference companyPreference = user.getCompany().getCompanyPreference();
		companyPreference.setExternalIdActive(true);
		companyPreference.setExternalIdDisplayName("Work Unique ID");
		companyPreference.setExternalIdVersion(1);
		companyService.updateCompanyPreference(companyPreference);
	}
}
