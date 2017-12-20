package com.workmarket.web.controllers.onboarding;


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.ImageCoordinates;
import com.workmarket.domains.model.ImageDTO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.domains.onboarding.model.PhoneInfoDTO;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import com.workmarket.service.business.UserService;
import com.workmarket.utility.EmailUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Created by ianha on 6/4/14
 */
public class WorkerOnboardingDTOValidator implements Validator {

	private MessageBundleHelper messageBundleHelper;
	private UserService userService;
	private String userNumber;

	public static final int WORK_DISTANCE_LOWER_BOUND = 5;
	public static final int WORK_DISTANCE_UPPER_BOUND = 250;

	public WorkerOnboardingDTOValidator(MessageBundleHelper messageBundleHelper, UserService userService) {
		this(messageBundleHelper, userService, null);
	}
	public WorkerOnboardingDTOValidator(MessageBundleHelper messageBundleHelper, UserService userService, String userNumber) {
		super();
		this.messageBundleHelper = messageBundleHelper;
		this.userService = userService;
		this.userNumber = userNumber;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return WorkerOnboardingDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		WorkerOnboardingDTO dto = (WorkerOnboardingDTO) target;
		User existingUser = userService.findUserByEmail(dto.getEmail());
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

		//cannot use ValidationUtils because User is not bound to errors in controller
		if (StringUtils.isEmpty(dto.getFirstName())) {
			errors.reject("user.validation.firstNameRequired");
		} else if (StringUtils.length(dto.getFirstName()) > Constants.FIRST_NAME_MAX_LENGTH) {
			errors.reject("user.validation.firstNameMaxLength");
		}

		if (StringUtils.isEmpty(dto.getLastName())) {
			errors.reject("user.validation.lastNameRequired");
		} else if (StringUtils.length(dto.getLastName()) > Constants.LAST_NAME_MAX_LENGTH) {
			errors.reject("user.validation.lastNameMaxLength");
		}

		if (StringUtils.isEmpty(dto.getEmail())) {
			errors.rejectValue("email", "user.validation.emailRequired");
		} else if (!EmailUtilities.isValidEmailAddress(dto.getEmail())) {
			errors.rejectValue("email", "user.validation.emailInvalid");
		} else {
			if (existingUser != null && !existingUser.getUserNumber().equals(userNumber)) {
				errors.rejectValue("email", "email_exists", messageBundleHelper.getMessage("user.validation.emailExists", dto.getEmail()));
			}
		}

		if (dto.getCity() != null || dto.getStateShortName() != null ||
				dto.getPostalCode() != null || dto.getCountryIso() != null ||
				dto.getLatitude() != null || dto.getLongitude() != null) {
			if (dto.getCity() == null) {
				errors.rejectValue("city", "city", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "City"));
			}
			if (dto.getCountryIso() == null) {
				errors.rejectValue("countryIso", "countryIso", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Country ISO"));
			}

			if (dto.getLatitude() == null && dto.getLongitude() != null) {
				errors.rejectValue("latitude", "latitude", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Latitude"));
			}
			if (dto.getLongitude() == null && dto.getLatitude() != null) {
				errors.rejectValue("longitude", "longitude", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Longitude"));
			}

			if(dto.getPostalCode() == null) {
				errors.rejectValue("postalCode", "locations.manage.invalid.postalcode", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Postal Code"));
			}
			else {
				if (StringUtilities.equalsAny(dto.getCountryIso(), Country.USA, Country.newInstance(Country.USA).getName())){
					if(!PostalCodeUtilities.isValidPostalCode(dto.getPostalCode())) {
						errors.rejectValue("postalCode", "locations.manage.invalid.postalcode", "Please provide a valid Postal Code");
					}
				}
				else if (StringUtilities.equalsAny(dto.getCountryIso(), Country.CANADA, Country.newInstance(Country.CANADA).getName())) {
					try {
						PostalCodeUtilities.formatCanadianPostalCode(dto.getPostalCode());
					}
					catch(Exception e) {
						errors.rejectValue("postalCode", "locations.manage.invalid.postalcode", messageBundleHelper.getMessage("locations.manage.invalid.postalcode"));
					}
				}
				else {
					if (org.apache.commons.lang.StringUtils.length(dto.getPostalCode()) > Constants.POSTAL_CODE_MAX_LENGTH) {
						errors.rejectValue("postalCode", "locations.manage.invalid.postalcode","Postal code is too long");
					}
				}
			}

			if (dto.getMaxTravelDistance() == null) {
				errors.rejectValue("maxTravelDistance", "maxTravelDistance", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Work Radius"));
			} else {
				int maxDistance = dto.getMaxTravelDistance();
				if (maxDistance < WORK_DISTANCE_LOWER_BOUND || maxDistance > WORK_DISTANCE_UPPER_BOUND) {
					errors.rejectValue(
						"maxTravelDistance",
						"maxTravelDistance",
						String.format(messageBundleHelper.getMessage("onboarding.validation.greaterThanLessThan"), "Work Radius", WORK_DISTANCE_LOWER_BOUND, WORK_DISTANCE_UPPER_BOUND)
					);
				}
			}
		}

		if (dto.hasAvatar()) {
			validateAvatar(dto.getAvatar(), errors);
		}

		List<PhoneInfoDTO> phones = dto.getPhones();
		if (isNotEmpty(phones)) {
			boolean anyValidPhone = false;
			List <String> phoneErrors = new ArrayList<>();
			List<Integer> phoneErrorIndexes = new ArrayList<>();
			for (int i = 0; i < phones.size(); i++) {
				PhoneInfoDTO phone = phones.get(i);
				boolean validPhone;
				String type = phone.getType();
				String code = phone.getCode();
				String number = phone.getNumber();
				String region = phoneUtil.getRegionCodeForCountryCode(code == null ? 1 : Integer.parseInt(code));
				if (number == null) { continue; }; // ignore empty numbers
				String formattedNumber = number;
				try {
					Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, region);
					formattedNumber = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
					validPhone = phoneUtil.isValidNumber(phoneNumber);
				}
				catch (NumberParseException e) {
					validPhone = false;
				}
				if (validPhone) {
					anyValidPhone = true;
				} else {
					phoneErrors.add(String.format(messageBundleHelper.getMessage("onboarding.validation.invalidPhone"), type, formattedNumber, region));
					phoneErrorIndexes.add(i);
				}
			}
			if (!anyValidPhone) {
				for (int j = 0; j < phoneErrorIndexes.size(); j++) {
					errors.rejectValue("phones[" + phoneErrorIndexes.get(j) + "].number", "phones", phoneErrors.get(j));
				}
			}
		}

		if (dto.hasCompanyLogo()) {
			ImageDTO image = dto.getLogo();

			if (!image.hasFilename()) {
				errors.rejectValue("logo.filename", "logo.filename", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Logo filename"));
			}
		}
	}

	public void validateAvatar(ImageDTO image, Errors errors) {
		validateAvatar(image, "avatar.", errors);
	}
	public void validateAvatar(ImageDTO image, String fieldPrefix, Errors errors) {
		ImageCoordinates coordinates = image.getCoordinates();

		if (image.getFilename() == null) {
			errors.rejectValue(fieldPrefix + "filename", "avatar.filename", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Avatar filename"));
		}

		if (image.getImage() == null) {
			errors.rejectValue(fieldPrefix + "image", "avatar.image", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Avatar image"));
		}

/*
		if (coordinates == null) {
			errors.rejectValue(fieldPrefix + "coordinates", "avatar.coordinates", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Avatar coordinates"));
		}
*/

		if (coordinates != null) {
			if (coordinates.getX() == null) {
				errors.rejectValue(fieldPrefix + "coordinates.x", "avatar.coordinates.x", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "x"));
			}
			if (coordinates.getY() == null) {
				errors.rejectValue(fieldPrefix + "coordinates.y", "avatar.coordinates.y", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "y"));
			}
			if (coordinates.getX2() == null) {
				errors.rejectValue(fieldPrefix + "coordinates.x2", "avatar.coordinates.x2", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "x2"));
			}
			if (coordinates.getY2() == null) {
				errors.rejectValue(fieldPrefix + "coordinates.y2", "avatar.coordinates.y2", String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "y2"));
			}

			if (coordinates.getY() != null && coordinates.getY2() != null && coordinates.getY() >= coordinates.getY2()) {
				errors.rejectValue(fieldPrefix + "coordinates.y2", "avatar.coordinates.y2", String.format(messageBundleHelper.getMessage("onboarding.validation.greaterThan"), "y2", "y"));
			}

			if (coordinates.getX() != null && coordinates.getX2() != null && coordinates.getX() >= coordinates.getX2()) {
				errors.rejectValue(fieldPrefix + "coordinates.x2", "avatar.coordinates.x2", String.format(messageBundleHelper.getMessage("onboarding.validation.greaterThan"), "x2", "x"));
			}
		}
	}
}
