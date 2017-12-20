package com.workmarket.web.validators;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddressValidatorTest extends BaseValidatorTest {

	@InjectMocks AddressValidator validator;
	@Mock AddressDTO addressDTO;
	@Mock MessageBundleHelper messageBundleHelper;

	@Before
	public void setUp() {
		when(messageBundleHelper.getMessage(any(String.class))).thenReturn("Some validation message goes here");
		when(addressDTO.getAddress1()).thenReturn(ANY_STRING);
		when(addressDTO.getAddress2()).thenReturn(ANY_STRING);
		when(addressDTO.getCity()).thenReturn(ANY_STRING);
		when(addressDTO.getPostalCode()).thenReturn(ANY_STRING);
		when(addressDTO.getState()).thenReturn(ANY_STRING);
		when(addressDTO.getCountry()).thenReturn(ANY_STRING);
	}

	@Test
	public void validate_noErrors() {
		Errors errors = validate(addressDTO);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void validate_noStateOrPostalCode_hasGeoCoord_success() {
		when(addressDTO.getState()).thenReturn(null);
		when(addressDTO.getPostalCode()).thenReturn(null);
		when(addressDTO.getLatitude()).thenReturn(BigDecimal.ONE);
		when(addressDTO.getLongitude()).thenReturn(BigDecimal.ONE);
		Errors errors = validate(addressDTO);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void validate_noStateOrPostalCodeOrGeoCoord_Error() {
		when(addressDTO.getState()).thenReturn(null);
		when(addressDTO.getPostalCode()).thenReturn(null);
		Errors errors = validate(addressDTO);
		assertTrue(hasErrorCode(errors, "NotNull.state"));
		assertTrue(hasErrorCode(errors, "NotNull.postalCode"));
	}

	@Test
	public void validate_lengthyAddress1_characterMaxError() {
		when(addressDTO.getAddress1()).thenReturn(generateMaxExceededString(Constants.ADDRESS_LINE_1_MAX_LENGTH));
		Errors errors = validate(addressDTO);
		assertTrue(hasFieldInError(errors, "address1"));
		assertTrue(hasErrorCode(errors, "Max"));
	}

	@Test
	public void validate_lengthyAddress2_characterMaxError() {
		when(addressDTO.getAddress2()).thenReturn(generateMaxExceededString(Constants.ADDRESS_LINE_2_MAX_LENGTH));
		Errors errors = validate(addressDTO);
		assertTrue(hasFieldInError(errors, "address2"));
		assertTrue(hasErrorCode(errors, "Max"));
	}

	@Test
	public void validate_lengthyCity_characterMaxError() {
		when(addressDTO.getCity()).thenReturn(generateMaxExceededString(Constants.CITY_MAX_LENGTH));
		Errors errors = validate(addressDTO);
		assertTrue(hasFieldInError(errors, "city"));
		assertTrue(hasErrorCode(errors, "Max"));
	}

	@Test
	public void validate_lengthyState_characterMaxError() {
		when(addressDTO.getState()).thenReturn(generateMaxExceededString(Constants.STATE_MAX_LENGTH));
		Errors errors = validate(addressDTO);
		assertTrue(hasFieldInError(errors, "state"));
		assertTrue(hasErrorCode(errors, "Max"));
	}

	@Test
	public void validate_lengthyCountry_characterMaxError() {
		when(addressDTO.getCountry()).thenReturn(generateMaxExceededString(Constants.COUNTRY_MAX_LENGTH));
		Errors errors = validate(addressDTO);
		assertTrue(hasFieldInError(errors, "country"));
		assertTrue(hasErrorCode(errors, "Max"));
	}

	@Test
	public void validate_lengthyPostalCode_characterMaxError() {
		when(addressDTO.getPostalCode()).thenReturn(generateMaxExceededString(Constants.POSTAL_CODE_MAX_LENGTH));
		Errors errors = validate(addressDTO);
		assertTrue(hasFieldInError(errors, "postalCode"));
		assertTrue(hasErrorCode(errors, "Max"));
	}

	@Test
	public void validate_countryPuertoRico_stateChangedToPR() {
		when(addressDTO.getCountry()).thenReturn(Country.PUERTO_RICO);
		validate(addressDTO);
		verify(addressDTO).setState(State.PR);
	}


	@Test
	public void validate_countryPuertoRico_countryChangedToUSA() {
		when(addressDTO.getCountry()).thenReturn(Country.PUERTO_RICO);
		validate(addressDTO);
		verify(addressDTO).setCountry(Country.USA);
	}

	@Test
	public void validate_countryPR_stateChangedToPR() {
		when(addressDTO.getCountry()).thenReturn(Country.PR);
		validate(addressDTO);
		verify(addressDTO).setState(State.PR);
	}

	@Test
	public void validate_countryPR_countryChangedToUSA() {
		when(addressDTO.getCountry()).thenReturn(Country.PR);
		validate(addressDTO);
		verify(addressDTO).setCountry(Country.USA);
	}

	@Test
	public void supports_nonAddressDTODerivedClass_notSupported() {
		boolean isSupported = validator.supports(Error.class);
		assertFalse(isSupported);
	}

	@Test
	public void supports_addressDTODerivedClass_supported() {
		boolean isSupported = validator.supports(AddressDTO.class);
		assertTrue(isSupported);
	}

	protected Validator getValidator() {
		return validator;
	}

}
