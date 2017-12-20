package com.workmarket.web.builders;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.service.business.CompanyService;
import com.workmarket.web.forms.account.CompanyDetailsForm;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyDetailsFormBuilder {

	@Autowired private CompanyService companyService;
	
	public CompanyDetailsForm build(Company company, Address companyAddress) {

		CompanyDetailsForm companyDetailsForm = new CompanyDetailsForm();

		if (company != null) {
			companyDetailsForm.setName(StringEscapeUtils.unescapeHtml4(company.getName()));
			companyDetailsForm.setOverview(company.getOverview());
			companyDetailsForm.setWebsite(company.getWebsite());
			companyDetailsForm.setEmployees(company.getEmployees());
			companyDetailsForm.setYearfounded(company.getYearFounded());
			companyDetailsForm.setEmployedprofessionals(company.getEmployedProfessionals());

			CompanyAssetAssociation avatars = companyService.findCompanyAvatars(company.getId());
			if (avatars != null && avatars.getSmall() != null) {
				companyDetailsForm.setAvatar(avatars.getSmall().getCdnUri());
			}
		}

		if (companyAddress != null) {
			companyDetailsForm.setAddress1(companyAddress.getAddress1());
			companyDetailsForm.setAddress2(companyAddress.getAddress2());
			companyDetailsForm.setCity(companyAddress.getCity());
			companyDetailsForm.setState(companyAddress.getState() != null ? companyAddress.getState().getShortName() : null);
			companyDetailsForm.setPostalCode(companyAddress.getPostalCode());
			companyDetailsForm.setCountry(companyAddress.getCountry() != null ? companyAddress.getCountry().getId() : null);
			companyDetailsForm.setLongitude(companyAddress.getLongitude() != null ? companyAddress.getLongitude().toString() : null);
			companyDetailsForm.setLatitude(companyAddress.getLatitude() != null ? companyAddress.getLatitude().toString() : null);
			companyDetailsForm.setDress_code(companyAddress.getDressCode() != null ? companyAddress.getDressCode().getId() : null);
			companyDetailsForm.setLocation_type(companyAddress.getLocationType() != null ? companyAddress.getLocationType().getId() : null);
		}

		return companyDetailsForm;
	}
}
