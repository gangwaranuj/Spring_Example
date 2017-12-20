package com.workmarket.web.validators;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static com.workmarket.utility.StringUtilities.equalsAny;

@Component
public class AddressValidator implements Validator {

	@Autowired MessageBundleHelper messageBundleHelper;

	protected String postalCodeAttribute = "postalCode";


	@Override
	public boolean supports(Class<?> aClass) {
		return AddressDTO.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		AddressDTO a = (AddressDTO)o;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address1", "NotNull",
			messageBundleHelper.getMessage("userRegistration.validation.address.empty_address"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "NotNull",
			messageBundleHelper.getMessage("userRegistration.validation.address.empty_city"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "NotNull",
			messageBundleHelper.getMessage("userRegistration.validation.address.empty_country"));

		if (StringUtils.isEmpty(a.getCountry()) || a.getCountry().equals(Country.WITHOUTCOUNTRY)) {
			errors.rejectValue("country", "NotNull", messageBundleHelper.getMessage("userRegistration.validation.address.empty_country"));
		}

		// if the address has geo coordinates, then state and postal code, which not all locations have, can be empty
		if (a.getLatitude() == null ||
			a.getLongitude() == null ||
			(BigDecimal.ZERO.equals(a.getLatitude()) && BigDecimal.ZERO.equals(a.getLongitude()))) {
			if (!Constants.COUNTRIES_WITH_NO_STATE.contains(a.getCountry())) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "NotNull",
					messageBundleHelper.getMessage("userRegistration.validation.address.empty_state"));
			}
			if (!Constants.COUNTRIES_WITH_NO_POSTALCODE.contains(a.getCountry())) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, postalCodeAttribute, "NotNull",
					messageBundleHelper.getMessage("userRegistration.validation.address.empty_postalCode"));
			}
		}

		if (StringUtils.length(a.getAddress1()) > Constants.ADDRESS_LINE_1_MAX_LENGTH) {
			errors.rejectValue("address1", "Max", CollectionUtilities.newArray("address1", Constants.ADDRESS_LINE_1_MAX_LENGTH), "");
		}
		if (StringUtils.length(a.getAddress2()) > Constants.ADDRESS_LINE_2_MAX_LENGTH) {
			errors.rejectValue("address2", "Max");
		}
		if (StringUtils.length(a.getCity()) > Constants.CITY_MAX_LENGTH) {
			errors.rejectValue("city", "Max");
		}
		if (StringUtils.length(a.getState()) > Constants.STATE_MAX_LENGTH) {
			errors.rejectValue("state", "Max");
		}
		if (StringUtils.length(a.getPostalCode()) > Constants.POSTAL_CODE_MAX_LENGTH) {
			errors.rejectValue(postalCodeAttribute, "Max");
		}
		if (StringUtils.length(a.getCountry()) > Constants.COUNTRY_MAX_LENGTH) {
			errors.rejectValue("country", "Max");
		}

		// Special case: if DTO has Puerto Rico set as country, change country to USA, and state to PR
		if (equalsAny(a.getCountry(), Country.PR, Country.PUERTO_RICO)) {
			a.setCountry(Country.USA);
			a.setState(State.PR);
		}

	}

	/**
	 * Utility method for unit tests
	 */
	protected void setMessageBundleHelper(MessageBundleHelper messageBundleHelper) {
		this.messageBundleHelper = messageBundleHelper;
	}
}