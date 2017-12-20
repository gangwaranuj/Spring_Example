package com.workmarket.service.thrift;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.business.upload.parser.LocationErrorsHelper;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang.StringUtils.isEmpty;

@Service
public class CountryAssignmentHelperImpl implements CountryAssignmentHelper {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private ProfileService profileService;

	public String getCountryForAssignments(WorkUploadColumn type, WorkUploaderBuildResponse response, String country) {
		if (isEmpty(country)) {
			Address address = profileService.findAddress(authenticationService.getCurrentUser().getId());
			if (address != null) {
				if (address.getCountry() != null && !Country.WITHOUTCOUNTRY.equals(address.getCountry().getId())) {
					return address.getCountry().getId();
				}
				if (WorkUploadColumn.LOCATION_COUNTRY.equals(type)) {
					response.addToRowParseErrors(LocationErrorsHelper.newLocationCountryNotFoundError(country));
				}
				if (WorkUploadColumn.RETURN_LOCATION_COUNTRY.equals(type)) {
					response.addToRowParseErrors(LocationErrorsHelper.newPartsCountryNotFoundError(country));
				}
				if (WorkUploadColumn.PICKUP_LOCATION_COUNTRY.equals(type)) {
					response.addToRowParseErrors(LocationErrorsHelper.newReturnPartsCountryNotFoundError(country));
				}
			}
		}
		return Country.valueOf(country).getId();
	}
}
