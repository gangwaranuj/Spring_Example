package com.workmarket.api.v2.employer.uploads.services;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.uploads.models.CellDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;
import java.util.regex.Pattern;

import static com.workmarket.domains.work.service.upload.WorkUploadColumn.*;

@Service
public class CsvValidationServiceImpl implements CsvValidationService {
	private static final Pattern EMAIL_REGEX = Pattern.compile("([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4})?");
	private static final NumberFormat PRICE_FORMAT = NumberFormat.getNumberInstance(Locale.US);

	@Override
	public List<ApiBaseError> validate(Map<String, CellDTO> rowData, long rowNumber) {
		List<ApiBaseError> errors = Lists.newArrayList();
		validateGeneral(rowData, errors);
		validateResources(rowData, errors);
		validatePricing(rowData, errors);
		validateBundle(rowData, errors);
		validateSchedule(rowData, errors);
		return errors;
	}

	private boolean isResourceUserNumbersBelowLimit(String resourceUserNumbers) {
		if (StringUtils.isNotBlank(resourceUserNumbers)) {
			Set<String> userNumbers = Sets.newLinkedHashSet(Arrays.asList(resourceUserNumbers.trim().split("[\\s,]+")));
			if (userNumbers.size() > Constants.UPLOAD_SEND_RESOURCES_LIMIT) {
				return false;
			}
		}
		return true;
	}

	private Set<String> getInvalidResourceUserNumbers(String resourceUserNumbers) {
		Set<String> invalidUserNumbers = Sets.newLinkedHashSet();
		if (StringUtils.isNotBlank(resourceUserNumbers)) {
			Set<String> userNumbers = Sets.newLinkedHashSet(Arrays.asList(resourceUserNumbers.trim().split("[\\s,]+")));
			for (String userNumber : userNumbers) {
				if (!isNumberEmptyOrValidNumber(userNumber)) {
					invalidUserNumbers.add(userNumber);
				}
			}
		}
		return invalidUserNumbers;
	}

	private ApiBaseError validateDateTime(String dateTime, String date, String time, WorkUploadColumn dateTimeColumn,
																				WorkUploadColumn dateColumn, WorkUploadColumn timeColumn) {

		if(StringUtils.isNotBlank(dateTime)) {
			if (StringUtils.isNotBlank(date) || StringUtils.isNotBlank(time)) {
				// START_DATE_TIME and START_DATE or START_TIME given
				return new ApiBaseError("Multiple columns mapped for Start Date and Time");
			}
			else if (!isDateTimeValid(dateTime)) {
				// START_DATE_TIME invalid
				return new ApiBaseError(String.format("%s is invalid (%s)", dateTimeColumn.getUploadColumnDescription(), date));
			}
		}
		else if(StringUtils.isNotBlank(date) && StringUtils.isBlank(time)) {
			// START_DATE given but not START_TIME
			return new ApiBaseError(String.format("%s is missing", timeColumn.getUploadColumnDescription()));
		}
		else if(StringUtils.isNotBlank(time) && StringUtils.isBlank(date)) {
			// START_TIME given but not START_DATE
			return new ApiBaseError(String.format("%s is missing", dateColumn.getUploadColumnDescription()));
		}
		else if(StringUtils.isNotBlank(time) && StringUtils.isNotBlank(date)) {
			if (!isDateValid(date, time)) {
				// START_DATE not a valid date
				return new ApiBaseError(String.format("%s is invalid (%s)", dateColumn.getUploadColumnDescription(), date));
			}
			if (!isTimeValid(date, time)) {
				// START_TIME given but not START_DATE
				return new ApiBaseError(String.format("%s is invalid (%s)", timeColumn.getUploadColumnDescription(), time));
			}
		}
		return null;
	}

	private boolean isDateTimeValid(String dateTime) {
		Calendar c = DateUtilities.getCalendarFromDateTimeString(dateTime, Constants.DEFAULT_TIMEZONE);
		return c != null;
	}

	private boolean isDateValid(String date, String time) {
		Calendar c = DateUtilities.getCalendarFromDateTimeString(date, time, Constants.DEFAULT_TIMEZONE);
		return c != null;
	}

	private boolean isTimeValid(String date, String time) {
		Calendar c = DateUtilities.getCalendarFromDateTimeString(date, time, Constants.DEFAULT_TIMEZONE);
		return c.isSet(Calendar.HOUR);
	}

	private void validateNumber(Map<String, CellDTO> rowData, WorkUploadColumn column, List<ApiBaseError> errors) {
		String number = getValue(rowData,column.getUploadColumnName());
		if (!isNumberEmptyOrValidNumber(number)) {
			errors.add(new ApiBaseError(String.format("%s is invalid (%s)", column.getUploadColumnDescription(), number)));
		}
	}

	private boolean isNumberEmptyOrValidNumber(String number) {
		if (StringUtils.isNotBlank(number)) {
			if(!NumberUtils.isDigits(number)) {
				return false;
			}
		}
		return true;
	}

	private void validatePrice(Map<String, CellDTO> rowData, WorkUploadColumn column, List<ApiBaseError> errors) {
		String price = getValue(rowData,column.getUploadColumnName());
		if (StringUtils.isNotBlank(price)) {
			ParsePosition parsePosition = new ParsePosition(0);
			Object object = PRICE_FORMAT.parse(price, parsePosition);
			if(object == null || parsePosition.getIndex() < price.length()) {
				errors.add(new ApiBaseError(String.format("%s is invalid (%s)", column.getUploadColumnDescription(), price)));
			}
		}
	}

	private void validateEmail(Map<String, CellDTO> rowData, WorkUploadColumn column, List<ApiBaseError> errors) {
		String email = getValue(rowData,column.getUploadColumnName());
		if (StringUtils.isNotBlank(email)) {
			if(!EMAIL_REGEX.matcher(email).matches()) {
				errors.add(new ApiBaseError(String.format("%s is invalid (%s)", column.getUploadColumnDescription(), email)));
			}
		}
	}

	private void validateResources(Map<String, CellDTO> rowData, List<ApiBaseError> errors) {
		String resourceUserNumbers = getValue(rowData,USER_NUMBER.getUploadColumnName());
		if (!isResourceUserNumbersBelowLimit(resourceUserNumbers)) {
			errors.add(new ApiBaseError("Resource number exceeds limit"));
		}
		else {
			Set<String> invalidResourceUserNumbers = getInvalidResourceUserNumbers(resourceUserNumbers);
			if (!invalidResourceUserNumbers.isEmpty()) {
				// USER_NUMBER given but one or more not found in DB
				errors.add(new ApiBaseError("The following Worker IDs are invalid: " +
					StringUtils.join(invalidResourceUserNumbers, ", ")));
			}
		}
	}

	private void validateBundle(Map<String, CellDTO> rowData, List<ApiBaseError> errors) {

		validateNumber(rowData, EXISTING_BUNDLE_ID, errors);

		String existingBundleId = getValue(rowData,EXISTING_BUNDLE_ID.getUploadColumnName());
		String newBundleName = getValue(rowData,NEW_BUNDLE_NAME.getUploadColumnName());
		String newBundleDescription = getValue(rowData,NEW_BUNDLE_DESCRIPTION.getUploadColumnName());
		if(StringUtils.isNotBlank(existingBundleId)) {
			if (StringUtils.isNotBlank(newBundleName) || StringUtils.isNotBlank(newBundleDescription)) {
				// EXISTING_BUNDLE_ID and NEW_BUNDLE_NAME or NEW_BUNDLE_DESCRIPTION given
				errors.add(new ApiBaseError("Cannot mix existing bundle id and new bundle information"));
			}
		}
		else if(StringUtils.isNotBlank(newBundleName) && StringUtils.isBlank(newBundleDescription)) {
			// NEW_BUNDLE_NAME given but not NEW_BUNDLE_DESCRIPTION
			errors.add(new ApiBaseError(String.format("%s is missing", NEW_BUNDLE_DESCRIPTION)));
		}
		else if(StringUtils.isNotBlank(newBundleDescription) && StringUtils.isBlank(newBundleName)) {
			// NEW_BUNDLE_DESCRIPTION given but not NEW_BUNDLE_NAME
			errors.add(new ApiBaseError(String.format("%s is missing", NEW_BUNDLE_NAME.getUploadColumnDescription())));
		}
	}

	private void validateSchedule(Map<String, CellDTO> rowData, List<ApiBaseError> errors) {

		String startDateTime = getValue(rowData,START_DATE_TIME.getUploadColumnName());
		String startDate = getValue(rowData,START_DATE.getUploadColumnName());
		String startTime = getValue(rowData,START_TIME.getUploadColumnName());
		ApiBaseError scheduleStartError = validateDateTime(startDateTime, startDate, startTime, START_DATE_TIME, START_DATE, START_TIME);
		if(scheduleStartError != null) {
			errors.add(scheduleStartError);
		}

		String endDateTime = getValue(rowData,END_DATE_TIME.getUploadColumnName());
		String endDate = getValue(rowData,END_DATE.getUploadColumnName());
		String endTime = getValue(rowData,END_TIME.getUploadColumnName());
		ApiBaseError scheduleEndError = validateDateTime(endDateTime, endDate, endTime, START_DATE_TIME, END_DATE, END_TIME);
		if(scheduleEndError != null) {
			errors.add(scheduleEndError);
		}
	}

	private void validatePricing(Map<String, CellDTO> rowData, List<ApiBaseError> errors) {

		validateNumber(rowData, MAX_NUMBER_OF_UNITS, errors);
		validateNumber(rowData, MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE, errors);
		validateNumber(rowData, MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE, errors);

		validatePrice(rowData, FLAT_PRICE_CLIENT_FEE, errors);
		validatePrice(rowData, FLAT_PRICE_RESOURCE_FEE, errors);
		validatePrice(rowData, PER_HOUR_PRICE_CLIENT_FEE, errors);
		validatePrice(rowData, PER_HOUR_PRICE_RESOURCE_FEE, errors);
		validatePrice(rowData, INITIAL_PER_HOUR_PRICE_CLIENT_FEE, errors);
		validatePrice(rowData, INITIAL_PER_HOUR_PRICE_RESOURCE_FEE, errors);
		validatePrice(rowData, ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE, errors);
		validatePrice(rowData, ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE, errors);
		validatePrice(rowData, PER_UNIT_PRICE_CLIENT_FEE, errors);
		validatePrice(rowData, PER_UNIT_PRICE_RESOURCE_FEE, errors);

		// FLAT
		String flatPriceClientFee = getValue(rowData,FLAT_PRICE_CLIENT_FEE.getUploadColumnName());
		String flatPriceResourceFee = getValue(rowData,FLAT_PRICE_RESOURCE_FEE.getUploadColumnName());
		// PER HOUR
		String perHourPriceClientFee = getValue(rowData,PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName());
		String perHourPriceResourceFee = getValue(rowData,PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName());
		String maxHours = getValue(rowData,MAX_NUMBER_OF_HOURS.getUploadColumnName());
		// BLENDED
		String initialPerHourPriceClientFee = getValue(rowData,INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName());
		String initialPerHourPriceResourceFee = getValue(rowData,INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName());
		String maxHoursInitialPrice = getValue(rowData,MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE.getUploadColumnName());
		String maxHoursAdditionalPrice = getValue(rowData,MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE.getUploadColumnName());
		String additionalPerHourPriceClientFee = getValue(rowData,ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName());
		String additionalPerHourPriceResourceFee = getValue(rowData,ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName());
		// PER UNIT
		String perUnitPriceClientFee = getValue(rowData,PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnName());
		String perUnitPriceResourceFee = getValue(rowData,PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnName());
		String maxUnits = getValue(rowData,MAX_NUMBER_OF_UNITS.getUploadColumnName());

		// validate single pricing strategy
		boolean hasPricingStrategy = false;
		if (StringUtils.isNotBlank(flatPriceClientFee) || StringUtils.isNotBlank(flatPriceResourceFee)) {
			hasPricingStrategy = true;
		}
		if (StringUtils.isNotBlank(perHourPriceClientFee) || StringUtils.isNotBlank(perHourPriceResourceFee) || StringUtils.isNotBlank(maxHours)) {
			if (hasPricingStrategy) {
				errors.add(new ApiBaseError("Multiple pricing strategies"));
			}
			hasPricingStrategy = true;
		}
		if (StringUtils.isNotBlank(initialPerHourPriceClientFee) || StringUtils.isNotBlank(initialPerHourPriceResourceFee) || StringUtils.isNotBlank(maxHoursInitialPrice) ||
			StringUtils.isNotBlank(maxHoursAdditionalPrice) || StringUtils.isNotBlank(additionalPerHourPriceClientFee) || StringUtils.isNotBlank(additionalPerHourPriceResourceFee)) {
			if (hasPricingStrategy) {
				errors.add(new ApiBaseError("Multiple pricing strategies"));
			}
			hasPricingStrategy = true;
		}
		if (StringUtils.isNotBlank(perUnitPriceClientFee) || StringUtils.isNotBlank(perUnitPriceResourceFee) || StringUtils.isNotBlank(maxUnits)) {
			if (hasPricingStrategy) {
				errors.add(new ApiBaseError("Multiple pricing strategies"));
			}
			hasPricingStrategy = true;
		}

		if (StringUtils.isNotBlank(flatPriceClientFee) && StringUtils.isNotBlank(flatPriceResourceFee)) {
			errors.add(new ApiBaseError("Client and resource flat prices given"));
		}
		if (StringUtils.isNotBlank(perHourPriceClientFee) && StringUtils.isNotBlank(perHourPriceResourceFee)) {
			errors.add(new ApiBaseError("Client and resource per hour prices"));
		}
		if (StringUtils.isNotBlank(perUnitPriceClientFee) && StringUtils.isNotBlank(perUnitPriceResourceFee)) {
			errors.add(new ApiBaseError("Client and resource per unit prices"));
		}
		if ((StringUtils.isNotBlank(initialPerHourPriceClientFee) || StringUtils.isNotBlank(additionalPerHourPriceClientFee))
			&& (StringUtils.isNotBlank(initialPerHourPriceResourceFee) || StringUtils.isNotBlank(additionalPerHourPriceResourceFee))) {
			errors.add(new ApiBaseError("Client and resource blended per hour prices"));
		}
	}

	private void validateGeneral(Map<String, CellDTO> rowData, List<ApiBaseError> errors) {
		validateNumber(rowData, OWNER_USER_NUMBER, errors);
		validateEmail(rowData, OWNER_EMAIL, errors);
		validateNumber(rowData, SUPPORT_CONTACT_USER_NUMBER, errors);
		validateEmail(rowData, SUPPORT_CONTACT_EMAIL, errors);
		validateNumber(rowData, INDUSTRY_ID, errors);
		validateNumber(rowData, LOCATION_NUMBER, errors);
		validateNumber(rowData, TEMPLATE_NUMBER, errors);
	}

	private String getValue(Map<String, CellDTO> rowData, String field) {
		Optional<CellDTO> dto = Optional.fromNullable(rowData.get(field));
		return dto.isPresent() ? dto.get().getValue() : null;
	}
}
