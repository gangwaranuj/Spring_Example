package com.workmarket.service.validation.validators;

import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.domains.model.Company;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class CompanyProfileDTOValidator implements Validator{

	@Override
	public boolean supports(final Class<?> clazz) {
		return false;
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		CompanyProfileDTO companyProfileDTO = (CompanyProfileDTO) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "overview", "not_empty", new Object[] {"Overview"});

		if (StringUtils.isNotEmpty(companyProfileDTO.getOverview()) && (companyProfileDTO.getOverview().length() > Company.COMPANY_OVERVIEW_MAX_LENGTH)) {
			errors.rejectValue("overview", "company.overview.max.length");
		}

		if (StringUtils.isNotEmpty(companyProfileDTO.getWebsite()) && (companyProfileDTO.getWebsite().length() > Company.COMPANY_WEBSITE_MAX_LENGTH)) {
			errors.rejectValue("website", "company.website.max.length");
		}
	}
}
