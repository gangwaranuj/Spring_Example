package com.workmarket.web.controllers.onboarding.exceptions;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.ImageCoordinates;
import com.workmarket.domains.model.ImageDTO;
import com.workmarket.domains.onboarding.model.PhoneInfoDTO;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import com.workmarket.service.business.UserService;
import com.workmarket.web.controllers.onboarding.WorkerOnboardingDTOValidator;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by ianha on 6/25/14
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkerOnboardingDTOValidatorTest {
	private static final String CITY = "New York";
	private static final String STATE = "NY";
	private static final String POSTAL_CODE = "10003";
	private static final String COUNTRY_ISO = "50";
	private static final String AVATAR_FILENAME = "profile.jpg";
	private static final String BASE64 = "lsdkjfLKEG93f3";

	private Errors errors;
	private WorkerOnboardingDTO dto;
	private ImageDTO image;
	private ImageDTO logo;
	private ImageCoordinates coordinates;
	private MessageBundleHelper messageBundleHelper;
	private UserService userService;
	private WorkerOnboardingDTOValidator validator;
	private PhoneInfoDTO phone_validUS;
	private PhoneInfoDTO phone_validCA;
	private PhoneInfoDTO phone_invalidUS;
	private PhoneInfoDTO phone_null;

	@Before
	public void setup() {
		messageBundleHelper = mock(MessageBundleHelper.class);
		when(messageBundleHelper.getMessage("onboarding.validation.mustDefine")).thenReturn("%s must be defined");
		when(messageBundleHelper.getMessage("onboarding.validation.greaterThan")).thenReturn("%s must be greater than %s");
		when(messageBundleHelper.getMessage("onboarding.validation.greaterThanLessThan")).thenReturn("%s must be greater than %s and less than %s");
		when(messageBundleHelper.getMessage("onboarding.validation.invalidPhone")).thenReturn("onboarding.validation.invalidPhone");

		dto = mock(WorkerOnboardingDTO.class);
		image = mock(ImageDTO.class);
		logo = mock(ImageDTO.class);
		coordinates = mock(ImageCoordinates.class);
		userService = mock(UserService.class);
		when(dto.getAvatar()).thenReturn(image);
		when(dto.getLogo()).thenReturn(logo);
		when(dto.getCity()).thenReturn(CITY);
		when(dto.getStateShortName()).thenReturn(STATE);
		when(dto.getPostalCode()).thenReturn(POSTAL_CODE);
		when(dto.getCountryIso()).thenReturn(COUNTRY_ISO);

		when(image.getFilename()).thenReturn(AVATAR_FILENAME);
		when(image.getImage()).thenReturn(BASE64);
		when(image.getCoordinates()).thenReturn(coordinates);
		when(logo.getFilename()).thenReturn(AVATAR_FILENAME);
		when(logo.getImage()).thenReturn(BASE64);

		when(coordinates.getX()).thenReturn(0);
		when(coordinates.getY()).thenReturn(0);
		when(coordinates.getX2()).thenReturn(1);
		when(coordinates.getY2()).thenReturn(1);

		phone_validUS = mock(PhoneInfoDTO.class);
		when(phone_validUS.getType()).thenReturn("work");
		when(phone_validUS.getCode()).thenReturn("1");
		when(phone_validUS.getNumber()).thenReturn("6316730001");
		phone_validCA = mock(PhoneInfoDTO.class);
		when(phone_validCA.getType()).thenReturn("work");
		when(phone_validCA.getCode()).thenReturn("1");
		when(phone_validCA.getNumber()).thenReturn("4164534534");
		phone_invalidUS = mock(PhoneInfoDTO.class);
		when(phone_invalidUS.getType()).thenReturn("mobile");
		when(phone_invalidUS.getCode()).thenReturn("1");
		when(phone_invalidUS.getNumber()).thenReturn("1234567890");
		phone_null = mock(PhoneInfoDTO.class);
		when(phone_null.getType()).thenReturn("sms");
		when(phone_null.getCode()).thenReturn(null);
		when(phone_null.getNumber()).thenReturn(null);

		errors = mock(BeanPropertyBindingResult.class);

		validator = new WorkerOnboardingDTOValidator(messageBundleHelper, userService);
	}

	@Test
	public void validate_emptyDto_true() {
		validator.validate(dto, errors);
		assertTrue(errors.getAllErrors().size() == 0);
	}

	@Test
	public void validate_missingCity_false() {
		when(dto.getCity()).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("city"), any(String.class), any(String.class));
	}

	@Test
	public void validate_missingState_true() {
		when(dto.getStateShortName()).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue(eq("stateShortName"), any(String.class), any(String.class));
	}

	@Test
	public void validate_missingPostalCode_true() {
		when(dto.getPostalCode()).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors, times(1)).rejectValue(eq("postalCode"), any(String.class), any(String.class));
	}

	@Test
	public void validate_missingCountryIso_false() {
		when(dto.getCountryIso()).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("countryIso"), any(String.class), any(String.class));
	}

	@Test
	public void validate_missingLongitude_false() {
		when(dto.getLatitude()).thenReturn("40.0");
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("longitude"), any(String.class), any(String.class));
	}

	@Test
	public void validate_missingLatitude_false() {
		when(dto.getLongitude()).thenReturn("40.0");
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("latitude"), any(String.class), any(String.class));
	}

	@Test
	public void validate_missingMaxTravelDistance_false() {
		when(dto.getMaxTravelDistance()).thenReturn(null);
		String expectedErrorMessage = String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Work Radius");

		validator.validate(dto, errors);

		verify(errors).rejectValue(eq("maxTravelDistance"), any(String.class), eq(expectedErrorMessage));
	}

	@Test
	public void validate_nonNumericalMaxTravelDistance_false() {
		when(dto.getMaxTravelDistance()).thenReturn(null);
		String expectedErrorMessage = String.format(messageBundleHelper.getMessage("onboarding.validation.mustDefine"), "Work Radius");

		validator.validate(dto, errors);

		verify(errors).rejectValue(eq("maxTravelDistance"), any(String.class), eq(expectedErrorMessage));
	}

	@Test
	public void validate_tooSmallMaxTravelDistance_false() {
		when(dto.getMaxTravelDistance()).thenReturn(WorkerOnboardingDTOValidator.WORK_DISTANCE_LOWER_BOUND - 1);
		String expectedErrorMessage = String.format(
			messageBundleHelper.getMessage("onboarding.validation.greaterThanLessThan"),
			"Work Radius",
			WorkerOnboardingDTOValidator.WORK_DISTANCE_LOWER_BOUND,
			WorkerOnboardingDTOValidator.WORK_DISTANCE_UPPER_BOUND
		);

		validator.validate(dto, errors);

		verify(errors).rejectValue(eq("maxTravelDistance"), any(String.class), eq(expectedErrorMessage));
	}

	@Test
	public void validate_tooLargeMaxTravelDistance_false() {
		when(dto.getMaxTravelDistance()).thenReturn(WorkerOnboardingDTOValidator.WORK_DISTANCE_UPPER_BOUND + 1);
		String expectedErrorMessage = String.format(
			messageBundleHelper.getMessage("onboarding.validation.greaterThanLessThan"),
			"Work Radius",
			WorkerOnboardingDTOValidator.WORK_DISTANCE_LOWER_BOUND,
			WorkerOnboardingDTOValidator.WORK_DISTANCE_UPPER_BOUND
		);

		validator.validate(dto, errors);

		verify(errors).rejectValue(eq("maxTravelDistance"), any(String.class), eq(expectedErrorMessage));
	}

	@Test
	public void validate_missingX_false() {
		when(coordinates.getX()).thenReturn(null);
		when(dto.hasAvatar()).thenReturn(true);
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("avatar.coordinates.x"), any(String.class), any(String.class));
	}

	@Test
	public void validate_missingX2_false() {
		when(coordinates.getX2()).thenReturn(null);
		when(dto.hasAvatar()).thenReturn(true);
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("avatar.coordinates.x2"), any(String.class), any(String.class));
	}

	@Test
	public void validate_missingY_false() {
		when(coordinates.getY()).thenReturn(null);
		when(dto.hasAvatar()).thenReturn(true);
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("avatar.coordinates.y"), any(String.class), any(String.class));
	}

	@Test
	public void validate_missingY2_false() {
		when(coordinates.getY2()).thenReturn(null);
		when(dto.hasAvatar()).thenReturn(true);
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("avatar.coordinates.y2"), any(String.class), any(String.class));
	}

	@Test
	public void validate_Y2LessThanY_false() {
		when(coordinates.getY()).thenReturn(1);
		when(coordinates.getY2()).thenReturn(0);
		when(dto.hasAvatar()).thenReturn(true);
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("avatar.coordinates.y2"), any(String.class), eq("y2 must be greater than y"));
	}

	@Test
	public void validate_X2LessThanX_false() {
		when(coordinates.getX()).thenReturn(1);
		when(coordinates.getX2()).thenReturn(0);
		when(dto.hasAvatar()).thenReturn(true);
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("avatar.coordinates.x2"), any(String.class), eq("x2 must be greater than x"));
	}

	@Test
	public void validate_logoWithoutFilename_false() {
		when(logo.getFilename()).thenReturn(null);
		when(dto.hasCompanyLogo()).thenReturn(true);
		when(dto.hasAvatar()).thenReturn(true);
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("logo.filename"), any(String.class), any(String.class));
	}

	@Test
	public void validate_phone_US_invalid_area_code() {
		// area codes may not begin with 0 or 1
		when(dto.getMaxTravelDistance()).thenReturn(6);
		when(dto.getPhones()).thenReturn(ImmutableList.of(phone_invalidUS));
		validator.validate(dto, errors);
		verify(errors).rejectValue(eq("phones[0].number"), any(String.class), any(String.class));
	}

	@Test
	public void validate_phone_US_valid() {
		when(dto.getPhones()).thenReturn(ImmutableList.of(phone_validUS));
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue(eq("phones[0].number"), any(String.class), any(String.class));
	}

	@Test
	public void validate_phone_CA_valid() {
		when(dto.getPhones()).thenReturn(ImmutableList.of(phone_validCA));
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue(eq("phones[0].number"), any(String.class), any(String.class));
	}

	@Test
	public void validate_phones_one_valid() {
		when(dto.getPhones()).thenReturn(ImmutableList.of(phone_validUS, phone_invalidUS));
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue(eq("phones[0].number"), any(String.class), any(String.class));
	}

	@Test
	public void validate_phones_one_valid_one_null() {
		when(dto.getPhones()).thenReturn(ImmutableList.of(phone_validUS, phone_null, phone_invalidUS));
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue(eq("phones[0].number"), any(String.class), any(String.class));
	}
}
