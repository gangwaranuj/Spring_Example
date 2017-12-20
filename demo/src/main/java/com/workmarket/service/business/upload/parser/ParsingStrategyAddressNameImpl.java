package com.workmarket.service.business.upload.parser;

import com.workmarket.dao.LocationDAO;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.thrift.CountryAssignmentHelper;
import com.workmarket.service.thrift.LocationTimeZoneHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ParsingStrategyAddressNameImpl implements ParsingStrategyHasAddress {

	@Autowired private InvariantDataService invariantDataService;
	@Autowired private LocationDAO locationDAO;
	@Autowired private CountryAssignmentHelper countryAssignmentHelper;
	@Autowired @Qualifier("locationTimeZoneHelperInternationalImpl") private LocationTimeZoneHelper locationTimeZoneHelper;

	public boolean parseLocation(Map<String, String> types, WorkUploaderBuildResponse response, long clientCompanyId, int lineNum) {
		String locationName = WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_NAME);
		String locationAddressLine1 = WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_ADDRESS_1);
		String locationCity = WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_CITY);
		String countryKeyWord = countryAssignmentHelper.getCountryForAssignments(
			WorkUploadColumn.LOCATION_COUNTRY, response, WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_COUNTRY)
		);
		String locationState = invariantDataService.findStateWithCountryAndState(countryKeyWord, WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_STATE)).getShortName();
		String locationPostalCode = PostalCodeUtilities.normalizePostalCodeInternational(WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_POSTAL_CODE), countryKeyWord);
		Long companyId = response.getWork().getCompany().getId();

		ClientLocation location = locationDAO.findLocationByClientCompanyAndName(
			companyId, clientCompanyId, locationName, locationAddressLine1, locationCity, locationState, locationPostalCode
		);

		if (location != null) {
			locationTimeZoneHelper.setLocationAndTimeZoneByLocationId(response, location, locationName);
		} else {
			locationTimeZoneHelper.setLocationAndTimeZone(response, types, lineNum);
		}
		return true;
	}


}
