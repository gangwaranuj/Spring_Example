package com.workmarket.domains.onboarding.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.dto.IndustryDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OnboardCompleteValidatorImpl implements OnboardCompleteValidator {

	@Autowired private IndustryService industryService;
	@Autowired private AddressService addressService;

	@Override
	public boolean validateMobile(Profile profile, Company company) {
		return validateWebOrMobile(profile, company, true, true, true, true);
	}

	@Override
	public boolean validateWeb(Profile profile, Company company, Boolean isLastStep) {
		return validateWebOrMobile(profile, company, isLastStep, false, false, false);
	}
	private boolean validateWebOrMobile(Profile profile, Company company, Boolean isLastStep, boolean allowEmptyPhoneNumber, boolean allowEmptyIndustry, boolean allowIncompleteCompany) {

		return hasRequiredUserFields(profile.getUser())
			&& (allowEmptyPhoneNumber || hasRequiredPhoneNumberFields(profile))
			&& (allowIncompleteCompany || hasRequiredCompanyFields(company))
			&& (allowEmptyIndustry || hasAtLeastOneValidIndustry(industryService.getIndustryDTOsForProfile(profile.getId())))
			&& hasRequiredAddressFields(addressService.findById(profile.getAddressId()))
			&& isLastStep;
	}

	private boolean hasAtLeastOneValidIndustry(Set<IndustryDTO> industries) {
		if (CollectionUtils.isEmpty(industries)) {
			return false;
		}

		IndustryDTO industry = Iterables.find(industries, new Predicate<IndustryDTO>() {
			@Override
			public boolean apply(IndustryDTO industry) {
			return Industry.NONE.getId().equals(industry.getId());
			}
		}, null);

		return (industry == null) || (industries.size() > 1);
	}

	private boolean hasRequiredAddressFields(Address address) {
		if (address == null) {
			return false;
		}

		return (StringUtils.isNotBlank(address.getAddress1()) && StringUtils.isNotBlank(address.getCity()) &&
				address.getState() != null && StringUtils.isNotBlank(address.getPostalCode()) && address.getCountry() != null)
			||
			(address.getLatitude() != null && address.getLongitude() != null);
	}

	private boolean hasRequiredCompanyFields(Company company) {
		if (company.getOperatingAsIndividualFlag()) {
			return true;
		}

		return StringUtils.isNotBlank(company.getName())
			&& StringUtils.isNotBlank(company.getWebsite())
			&& company.getYearFounded() != null
			&& StringUtils.isNotBlank(company.getOverview());
	}

	private boolean hasRequiredPhoneNumberFields(Profile profile) {
		return (StringUtils.isNotBlank(profile.getMobilePhone())
				|| StringUtils.isNotBlank(profile.getSmsPhone())
				|| StringUtils.isNotBlank(profile.getWorkPhone())
			);
	}

	private boolean hasRequiredUserFields(User user) {
		return StringUtils.isNotBlank(user.getEmail())
			&& StringUtils.isNotBlank(user.getFirstName())
			&& StringUtils.isNotBlank(user.getLastName());
	}
}
