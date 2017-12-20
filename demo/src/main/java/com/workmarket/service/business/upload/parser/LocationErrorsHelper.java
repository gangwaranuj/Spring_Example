package com.workmarket.service.business.upload.parser;

import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;

/**
 * User: jasonpendrey
 * Date: 7/3/13
 * Time: 5:21 PM
 */
public class LocationErrorsHelper {

	public static WorkRowParseError newLocationNameNotFoundError(String name) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage(String.format("Location Name %s not found!", name));
		e.setColumn(WorkUploadColumn.LOCATION_NAME);
		e.setData(name);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newLocationNumberNotFoundError(String number) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage(String.format("Location Number %s not found!", number));
		e.setColumn(WorkUploadColumn.LOCATION_NUMBER);
		e.setData(number);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newZipCodeNotFoundError(String msg, String zip) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage(msg);
		e.setColumn(WorkUploadColumn.LOCATION_POSTAL_CODE);
		e.setData(zip);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newStateNotFoundError(String state) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage(String.format("Assignment Location State %s was not found in our database. If this is a legitimate state, please contact Client Service.",state));
		e.setColumn(WorkUploadColumn.LOCATION_STATE);
		e.setData(state);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newStateNotFoundInCountryError(String state, String country) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage(String.format("Assignment Location State %s in country %s was not found in our database. If this is a legitimate state, please contact Client Service.",state,country));
		e.setColumn(WorkUploadColumn.LOCATION_STATE);
		e.setData(state);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newMultipleLocationsFoundError(String locName) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage(String.format("Error: More than 1 location matches the provided Location %s.",locName));
		e.setColumn(WorkUploadColumn.LOCATION_NAME);
		e.setData(locName);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newClientNotAssignedToLocationNumber(String locationNumber, String wrongClient) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage(String.format("Error: Location number %s is not assigned to %s or location number does not exist.", locationNumber, wrongClient));
		e.setColumn(WorkUploadColumn.LOCATION_NUMBER);
		e.setData(locationNumber);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newClientNotAssignedToLocationName(String locationName, String wrongClient) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage(String.format("Error: Location name %s is not assigned to %s or location name does not exist.", locationName, wrongClient));
		e.setColumn(WorkUploadColumn.LOCATION_NAME);
		e.setData(locationName);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newLocationCountryNotFoundError(String country) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage("Assignment Location Country cannot be determined, please include country or update profile address");
		e.setColumn(WorkUploadColumn.LOCATION_COUNTRY);
		e.setData(country);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newPartsCountryNotFoundError(String country) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage("Assignment Parts Destination Country cannot be determined, please include country or update profile address");
		e.setColumn(WorkUploadColumn.RETURN_LOCATION_COUNTRY);
		e.setData(country);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}

	public static WorkRowParseError newReturnPartsCountryNotFoundError(String country) {
		WorkRowParseError e = new WorkRowParseError();
		e.setMessage("Assignment Parts Returned Country cannot be determined, please include country or update profile address");
		e.setColumn(WorkUploadColumn.PICKUP_LOCATION_COUNTRY);
		e.setData(country);
		e.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		return e;
	}
}
