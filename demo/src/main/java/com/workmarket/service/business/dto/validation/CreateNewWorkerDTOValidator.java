package com.workmarket.service.business.dto.validation;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.validators.PasswordValidator;
import com.workmarket.web.validators.UserEmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component("createNewWorkerDTOValidator")
public class CreateNewWorkerDTOValidator implements Validator {
	private static final Pattern NAME_REGEX = Pattern.compile(
		"[a-zA-Z0-9'àáâäãåèéêëìíîïòóôöõøùúûüÿýñçčšžÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕØÙÚÛÜŸÝÑßÇŒÆČŠŽ∂ð.,\\-\\p{Space}]{2,50}");
	private static final Pattern ADDRESS1_REGEX = Pattern.compile(
		"[a-zA-Z0-9'àáâäãåèéêëìíîïòóôöõøùúûüÿýñçčšžÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕØÙÚÛÜŸÝÑßÇŒÆČŠŽ∂ð#.,/\\-\\p{Space}]*");
	private static final Pattern ADDRESS2_REGEX = Pattern.compile(
		"[a-zA-Z0-9'àáâäãåèéêëìíîïòóôöõøùúûüÿýñçčšžÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕØÙÚÛÜŸÝÑßÇŒÆČŠŽ∂ð#.,/\\-\\p{Space}]*");
	public static final String BLANK_USERNAME_TO_PREVENT_WEIRD_PREDICTABLE_PASSWORD_ERRORS = "";

	private final UserService userService;
	private final UserEmailValidator userEmailValidator;
	private final PasswordValidator passwordValidator;

	@Autowired
	public CreateNewWorkerDTOValidator(
		final UserEmailValidator userEmailValidator,
		final PasswordValidator passwordValidator,
		final UserService userService) {
		this.userEmailValidator = userEmailValidator;
		this.passwordValidator = passwordValidator;
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return CreateNewWorkerRequest.class.isAssignableFrom(clazz);
	}


	@Override
	public void validate(Object target, Errors errors) {
		CreateNewWorkerRequest dto = (CreateNewWorkerRequest) target;

		validateBasicCompanyFields(errors, dto);
		validateEmailAddress(errors, dto);

		validatePassword(errors, dto);

		validateAddress(errors, dto);
	}

	private void validatePassword(Errors errors, CreateNewWorkerRequest dto) {
		if (StringUtils.isBlank(dto.getPassword())) {
			errors.rejectValue("password", "mysettings.password.newPassword.invalid", "Password required.");
		}

		errors.pushNestedPath("password");
		passwordValidator.validate(dto.getPassword(),
			BLANK_USERNAME_TO_PREVENT_WEIRD_PREDICTABLE_PASSWORD_ERRORS, errors);
		errors.popNestedPath();
	}

	private void validateEmailAddress(Errors errors, CreateNewWorkerRequest dto) {
		errors.pushNestedPath("email");
		userEmailValidator.validate(dto.getEmail(), errors);
		errors.popNestedPath();
		if (userService.findUserByEmail(dto.getEmail()) != null) {
			errors.rejectValue("email", "user.validation.emailExists", "The email address you provided is unavailable.");
		}
	}

	private void validateBasicCompanyFields(Errors errors, CreateNewWorkerRequest dto) {
		if (StringUtils.isNotBlank(dto.getFirstName()) && !NAME_REGEX.matcher(dto.getFirstName()).matches()) {
			errors.rejectValue("firstName",
				"user.validation.firstNameFormat",
				"First Name may be 2-50 letters long.  Some special characters and spaces are allowed.");
		}

		if (StringUtils.isNotBlank(dto.getLastName()) && !NAME_REGEX.matcher(dto.getLastName()).matches()) {
			errors.rejectValue("lastName",
				"user.validation.lastNameFormat",
				"Last Name may be 2-50 letters long.  Some special characters and spaces are allowed.");
		}
	}

	private void validateAddress(Errors errors, CreateNewWorkerRequest dto) {
		if (StringUtils.isBlank(dto.getPostalCode())) {
			return;
		}

		if (org.apache.commons.lang.StringUtils.length(dto.getAddress1()) > Constants.ADDRESS_LINE_1_MAX_LENGTH) {
			errors.rejectValue("address1", "address.address1.too_long", "Address line one is too long");
		}
		if (StringUtils.isNotBlank(dto.getAddress1()) && !ADDRESS1_REGEX.matcher(dto.getAddress1()).matches()) {
			errors.rejectValue("address1", "address.address1.invalid_chars", "Address line one has invalid characters.  Only alphanumeric, space, and '-.,/#' are valid");
		}
		if (org.apache.commons.lang.StringUtils.length(dto.getAddress2()) > Constants.ADDRESS_LINE_2_MAX_LENGTH) {
			errors.rejectValue("address2", "address.address2.too_long", "Address line two is too long");
		}
		if (StringUtils.isNotBlank(dto.getAddress2()) && !ADDRESS2_REGEX.matcher(dto.getAddress2()).matches()) {
			errors.rejectValue("address2", "address.address2.invalid_chars", "Address line one has invalid characters.  Only alphanumeric, space, and '-.,/#' are valid");
		}
		if (StringUtils.isBlank(dto.getCity())) {
			errors.rejectValue("city", "onboarding.validation.mustDefine", "City is required");
		}
		if (StringUtils.isBlank(dto.getCountry())) {
			errors.rejectValue("country", "onboarding.validation.mustDefine", "Country is required");
		}

		if (dto.getLatitude() == null && dto.getLongitude() != null) {
			errors.rejectValue("latitude", "onboarding.validation.mustDefine", "Latitude is required");
		}
		if (dto.getLongitude() == null && dto.getLatitude() != null) {
			errors.rejectValue("longitude", "onboarding.validation.mustDefine", "Longitude is required");
		}

		if (dto.getPostalCode() == null) {
			errors.rejectValue("postalCode", "locations.manage.invalid.postalcode", "Postal Code is required");
		} else {
			if (StringUtilities.equalsAny(dto.getCountry(), Country.USA, Country.newInstance(Country.USA).getName())) {
				if (!PostalCodeUtilities.isValidPostalCode(dto.getPostalCode())) {
					errors.rejectValue("postalCode",
						"locations.manage.invalid.postalcode",
						"Please provide a valid Postal Code");
				}
			} else if (StringUtilities.equalsAny(dto.getCountry(),
				Country.CANADA,
				Country.newInstance(Country.CANADA).getName())) {
				try {
					PostalCodeUtilities.formatCanadianPostalCode(dto.getPostalCode());
				} catch (Exception e) {
					errors.rejectValue("postalCode",
						"locations.manage.invalid.postalcode",
						"Please provide a valid Postal Code");
				}
			} else {
				if (org.apache.commons.lang.StringUtils.length(dto.getPostalCode()) > Constants.POSTAL_CODE_MAX_LENGTH) {
					errors.rejectValue("postalCode", "locations.manage.invalid.postalcode", "Postal code is too long");
				}
			}
		}
	}
}
