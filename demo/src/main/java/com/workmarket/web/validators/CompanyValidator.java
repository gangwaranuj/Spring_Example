package com.workmarket.web.validators;

import com.workmarket.domains.model.Company;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CompanyValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return Company.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Company company = (Company)target;

		//cannot use ValidationUtils because Company is not bound to errors in controller
		if (StringUtils.isEmpty(company.getName())) {
			errors.rejectValue("name", "company.name.required");
		} else if (StringUtils.length(company.getName()) >  Company.COMPANY_NAME_MAX_LENGTH) {
			errors.rejectValue("name", "company.name.max.length");
		}

		if (StringUtils.isNotEmpty(company.getOverview()) && (company.getOverview().length() > Company.COMPANY_OVERVIEW_MAX_LENGTH)) {
			errors.rejectValue("overview", "company.overview.max.length");
		}

		if (StringUtils.isNotEmpty(company.getWebsite()) && (company.getWebsite().length() > Company.COMPANY_WEBSITE_MAX_LENGTH)) {
			errors.rejectValue("website", "company.website.max.length");
		}
	}
}
