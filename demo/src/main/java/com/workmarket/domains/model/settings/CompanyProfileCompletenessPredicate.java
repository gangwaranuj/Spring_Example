package com.workmarket.domains.model.settings;

import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import org.apache.commons.lang3.StringUtils;

public class CompanyProfileCompletenessPredicate implements CompletenessPredicate<CompanyProfileDTO> {

	@Override
	public boolean test(final CompanyProfileDTO companyProfileDTO) {
		return !StringUtils.isEmpty(companyProfileDTO.getOverview());
	}
}
