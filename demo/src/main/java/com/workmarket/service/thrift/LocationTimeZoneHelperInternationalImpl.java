package com.workmarket.service.thrift;

import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.dto.AddressDTO;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.work.Work;
import com.workmarket.service.business.upload.parser.LocationErrorsHelper;
import com.workmarket.service.business.upload.parser.WorkUploadLocation;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.utility.SerializationUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Service
public class LocationTimeZoneHelperInternationalImpl implements LocationTimeZoneHelper {

	@Autowired private InvariantDataService invariantDataService;
	@Autowired private CountryAssignmentHelper countryAssignmentHelper;

	public void setLocationAndTimeZone(
			WorkUploaderBuildResponse response,
			Map<String, String> columns,
			int lineNum) {

		Location location = new Location();
		if (WorkUploadColumn.isNotEmpty(columns, WorkUploadColumn.LOCATION_NUMBER)) {
			location.setNumber(WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_NUMBER));
		}
		if (WorkUploadColumn.isNotEmpty(columns, WorkUploadColumn.LOCATION_NAME)) {
			location.setName(WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_NAME));
		}
		if (WorkUploadColumn.isNotEmpty(columns, WorkUploadColumn.LOCATION_INSTRUCTIONS)) {
			location.setInstructions(WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_INSTRUCTIONS));
		}
		Address address = new Address();
		address.setAddressLine1(WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_ADDRESS_1));
		if (WorkUploadColumn.isNotEmpty(columns, WorkUploadColumn.LOCATION_ADDRESS_2)) {
			address.setAddressLine2(WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_ADDRESS_2));
		}
		address.setCity(WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_CITY));
		address.setState(WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_STATE));

		String country = WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_COUNTRY);
		String postalCodeFromColumns = WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_POSTAL_CODE);

		if (country == null && Country.isCanada(postalCodeFromColumns)) { country = Country.CANADA; }

		String countryKeyWord = countryAssignmentHelper.getCountryForAssignments(WorkUploadColumn.LOCATION_COUNTRY, response, country);
		address.setCountry(countryKeyWord);

		Work work = response.getWork();

		String normalizedPostalCode = PostalCodeUtilities.normalizePostalCodeInternational(postalCodeFromColumns, countryKeyWord);

		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1(address.getAddressLine1());
		addressDTO.setAddress2(address.getAddressLine2());
		addressDTO.setCity(address.getCity());
		addressDTO.setState(address.getState());
		addressDTO.setCountry(address.getCountry());
		addressDTO.setPostalCode(normalizedPostalCode);

		PostalCode postalCode = invariantDataService.findOrCreatePostalCode(addressDTO);
		if (postalCode != null) {
			address.setZip(addressDTO.getPostalCode());
			address.setCountry(addressDTO.getCountry());
			address.setCity(addressDTO.getCity());
			address.setState(addressDTO.getState());
			work.changeTimeZone(postalCode.getTimeZone());
		} else {
			response.addToRowParseErrors(
				LocationErrorsHelper.newZipCodeNotFoundError(
					"Failed to find assignment location. Please verify that google maps can find the specified address.",
					postalCodeFromColumns)
			);
		}

		if (WorkUploadColumn.isNotEmpty(columns, WorkUploadColumn.LOCATION_DRESS_CODE)) {
			address.setDressCode(WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_DRESS_CODE));
		}
		if (WorkUploadColumn.isNotEmpty(columns, WorkUploadColumn.LOCATION_TYPE)) {
			address.setType(WorkUploadColumn.get(columns, WorkUploadColumn.LOCATION_TYPE));
		}
		location.setAddress(address);
		location.setCompany(response.getWork().getCompany());

		WorkUploadLocation loc = (work.isSetClientCompany()) ?
				new WorkUploadLocation(location, work.getClientCompany().getId()) :
				new WorkUploadLocation(location);

		response.addNewLocation(loc, lineNum);
		response.getWork().setLocation((Location) SerializationUtilities.clone(location));
		response.getWork().setOffsiteLocation(false);
		response.getWork().setNewLocation(true);
	}

	public void setLocationAndTimeZoneByLocationId(WorkUploaderBuildResponse workUploaderBuildResponse, ClientLocation location, String id) {

		Work work = workUploaderBuildResponse.getWork();
		work.setOffsiteLocation(false);
		work.setNewLocation(false);

		Location tLocation = new Location()
			.setId(location.getId());
		if (isNotBlank(location.getName())) { tLocation.setName(location.getName()); }
		if (isNotBlank(location.getLocationNumber())) { tLocation.setNumber(location.getLocationNumber()); }

		com.workmarket.domains.model.Address address = location.getAddress();
		String postalCode = address.getPostalCode();
		Address tAddress = new Address()
			.setAddressLine1(address.getAddress1())
			.setCity(address.getCity())
			.setState(address.getState().getShortName())
			.setZip(postalCode)
			.setCountry(address.getCountry().getId())
			.setType(address.getLocationType().getDescription())
			.setDressCode(address.getDressCode() == null ? "" : address.getDressCode().getDescription());

		if (isNotBlank(address.getAddress2())) {
			tAddress.setAddressLine2(address.getAddress2());
		}
		tLocation.setAddress(tAddress);
		work.setLocation(tLocation);

		if (postalCode != null) {
			PostalCode postalCodeEntity = invariantDataService.getPostalCodeByCodeCountryStateCity(
					postalCode, address.getCountry().getId(), address.getState().getShortName(), address.getCity()
			);
			if (postalCodeEntity != null) {
				work.setTimeZone(postalCodeEntity.getTimeZone().getTimeZoneId());
				work.setTimeZoneId(postalCodeEntity.getTimeZone().getId());
			} else {
				workUploaderBuildResponse.addToRowParseErrors(
					LocationErrorsHelper.newZipCodeNotFoundError(
						"Location \"" + id + "\" has an invalid Zip Code associated. "
						+ "Please check this location in your Location Manager.", postalCode
					)
				);
			}
		}
	}
}
