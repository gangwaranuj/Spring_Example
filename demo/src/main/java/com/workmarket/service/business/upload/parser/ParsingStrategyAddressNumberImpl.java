package com.workmarket.service.business.upload.parser;

import com.workmarket.dao.LocationDAO;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.thrift.LocationTimeZoneHelper;
import com.workmarket.utility.StringUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ParsingStrategyAddressNumberImpl implements ParsingStrategyHasAddress {

	@Autowired private InvariantDataService invariantDataService;
	@Autowired private LocationDAO locationDAO;
	@Autowired @Qualifier("locationTimeZoneHelperInternationalImpl") private LocationTimeZoneHelper locationTimeZoneHelper;

	public boolean parseLocation(Map<String, String> types, WorkUploaderBuildResponse response, long clientCompanyId, int lineNum) {
		String locationNumber = WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_NUMBER);
		String locationAddressLine1 = WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_ADDRESS_1);
		String locationCity = WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_CITY);
		String locationState = invariantDataService.getStateCode(WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_STATE));
		String locationPostalCode = StringUtilities.normalizePostalCode(WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_POSTAL_CODE));
		Long companyId = response.getWork().getCompany().getId();

		ClientLocation location = locationDAO.findLocationByClientCompanyAndLocationNumber(
			companyId, clientCompanyId, locationNumber, locationAddressLine1, locationCity, locationState, locationPostalCode
		);

		if (location != null) {
			locationTimeZoneHelper.setLocationAndTimeZoneByLocationId(response, location, locationNumber);
		} else {
			locationTimeZoneHelper.setLocationAndTimeZone(response, types, lineNum);
		}
		return true;
	}
}
