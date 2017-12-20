package com.workmarket.service.business.upload.parser;

import com.workmarket.dao.LocationDAO;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.thrift.LocationTimeZoneHelper;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * User: jasonpendrey
 * Date: 7/9/13
 * Time: 5:19 PM
 */
@Service
public class ParsingStrategyNoAddressNumberImpl implements ParsingStrategyNoAddress {

	@Autowired private LocationDAO locationDAO;
	@Autowired @Qualifier("locationTimeZoneHelperInternationalImpl") private LocationTimeZoneHelper locationTimeZoneHelper;

	public boolean parseLocation(Map<String, String> types, WorkUploaderBuildResponse response, List<WorkRowParseError> errors, Long clientId) {
		String number = WorkUploadColumn.get(types, WorkUploadColumn.LOCATION_NUMBER);

		List<ClientLocation> locations;
		if (clientId != null) {
			locations = locationDAO.findLocationsByNumberAndClientCompany(number, clientId);
		} else {
			locations = locationDAO.findLocationByNumberAndCompany(number, response.getWork().getCompany().getId());
		}

		if (locations.size() == 0) {
			String clientNameInUpload = WorkUploadColumn.get(types, WorkUploadColumn.CLIENT_NAME);
			if (clientNameInUpload != null) {
				errors.add(LocationErrorsHelper.newClientNotAssignedToLocationNumber(number, clientNameInUpload));
			} else {
				errors.add(LocationErrorsHelper.newLocationNumberNotFoundError(number));
			}
			response.addToRowParseErrors(errors);
			return false;

		} else if (locations.size() > 1) {
			response.addToRowParseErrors(LocationErrorsHelper.newMultipleLocationsFoundError(number));
			return false;
		} else if (locations.size() == 1) {
			locationTimeZoneHelper.setLocationAndTimeZoneByLocationId(response, locations.get(0), number);
			return true;
		}
		return false;
	}

}
