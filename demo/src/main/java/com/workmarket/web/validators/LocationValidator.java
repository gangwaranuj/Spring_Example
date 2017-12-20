package com.workmarket.web.validators;

import com.workmarket.configuration.Constants;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LocationValidator implements Validator {

	@Autowired MessageBundleHelper messageBundleHelper;

	private static final int LOCATION_NAME_MAX_LENGTH = Constants.NAME_MAX_LENGTH;
	private static final int LOCATION_INSTRUCTIONS_MAX_LENGTH = 500;
	private static final int LOCATION_NUMBER_MAX_LENGTH = 50;

	@Override
	public boolean supports(Class<?> aClass) {
		return LocationDTO.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		LocationDTO a = (LocationDTO)o;

		if (StringUtils.length(a.getName()) > LOCATION_NAME_MAX_LENGTH) {
			errors.rejectValue("name", "Max.location", CollectionUtilities.newArray(LOCATION_NAME_MAX_LENGTH), "");
		}
		if (StringUtils.length(a.getLocationNumber()) > LOCATION_NUMBER_MAX_LENGTH) {
			errors.rejectValue("locationNumber", "Max.location", CollectionUtilities.newArray(LOCATION_NUMBER_MAX_LENGTH), "");
		}
		if (StringUtils.length(a.getInstructions()) > LOCATION_INSTRUCTIONS_MAX_LENGTH) {
			errors.rejectValue("instructions", "Max.location", CollectionUtilities.newArray(LOCATION_INSTRUCTIONS_MAX_LENGTH), "");
		}
	}

	/**
	 * Utility method for unit tests
	 */
	protected void setMessageBundleHelper(MessageBundleHelper messageBundleHelper) {
		this.messageBundleHelper = messageBundleHelper;
	}
}