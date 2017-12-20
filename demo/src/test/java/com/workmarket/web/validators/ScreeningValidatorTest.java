package com.workmarket.web.validators;

import com.workmarket.domains.model.screening.Screening;
import com.workmarket.service.business.dto.ScreeningDTO;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class ScreeningValidatorTest extends BaseValidatorTest {

	private final ScreeningValidator validator = new ScreeningValidator();
	@Mock private MessageBundleHelper messageHelper;

	@Before
	public void setup() {
		messageHelper = mock(MessageBundleHelper.class);
		when(messageHelper.getMessage(anyString())).thenReturn("Error String");
		validator.setMessageBundleHelper(messageHelper);
	}

	@Test
	public void supports_ScreeningForm_true() {
		assertTrue(validator.supports(ScreeningDTO.class));
	}

	@Test
	public void supports_OtherClass_false() {
		assertFalse(validator.supports(List.class));
	}

	@Test
	public void validate_allUsaFieldsValid_success() {
		assertFalse(validate(validCanadaDto()).hasErrors());
	}

	@Test
	public void validate_allCanadaFieldsValid_success() {
		assertFalse(validate(validCanadaDto()).hasErrors());
	}

	@Test
	public void validate_allInternationalFieldsValid_success() {
		assertFalse(validate(validInternationalDto()).hasErrors());
	}

	@Test
	public void validate_EmptyBirthDay_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setBirthDay(0);
		assertTrue(hasErrorCode(validate(dto), "NotEmpty"));
	}

	@Test
	public void validate_NullBirthDay_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setBirthDay(null);
		assertTrue(hasErrorCode(validate(dto), "NotEmpty"));
	}

	@Test
	public void validate_EmptyBirthMonth_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setBirthMonth(0);
		assertTrue(hasErrorCode(validate(dto), "NotEmpty"));
	}

	@Test
	public void validate_NullBirthMonth_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setBirthMonth(null);
		assertTrue(hasErrorCode(validate(dto), "NotEmpty"));
	}

	@Test
	public void validate_EmptyBirthYear_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setBirthYear(0);
		assertTrue(hasErrorCode(validate(dto), "NotEmpty"));
	}

	@Test
	public void validate_NullBirthYear_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setBirthYear(null);
		assertTrue(hasErrorCode(validate(dto), "NotEmpty"));
	}

	@Test
	public void validate_EmptyAddress1_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setAddress1("");
		assertTrue(hasErrorCode(validate(dto), "NotNull"));
	}

	@Test
	public void validate_NullAddress1_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setAddress1(null);
		assertTrue(hasErrorCode(validate(dto), "NotNull"));
	}

	@Test
	public void validate_EmptyState_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setState("");
		assertTrue(hasErrorCode(validate(dto), "NotNull"));
	}

	@Test
	public void validate_NullState_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setState(null);
		assertTrue(hasErrorCode(validate(dto), "NotNull"));
	}

	@Test
	public void validate_USAEmptyCity_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setCity("");
		assertTrue(hasErrorCode(validate(dto), "NotEmpty"));
	}

	@Test
	public void validate_USANullCity_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setCity(null);
		assertTrue(hasErrorCode(validate(dto), "NotEmpty"));
	}

	@Test
	public void validate_USAInvalidSSN_fail() {
		ScreeningDTO dto = validUsaDto();
		dto.setWorkIdentificationNumber(ANY_STRING);
		assertTrue(hasErrorCode(validate(dto), "screening.background.ssn.invalid"));
	}

	@Test
	public void validate_CanadaInvalidSIN_fail() {
		ScreeningDTO dto = validCanadaDto();
		dto.setWorkIdentificationNumber(ANY_STRING);
		assertTrue(hasErrorCode(validate(dto), "screening.background.gid.invalid"));
	}

	@Test
	public void validate_InternationalEmptyID_fail() {
		ScreeningDTO dto = validInternationalDto();
		dto.setWorkIdentificationNumber(EMPTY_TOKEN);
		assertTrue(hasErrorCode(validate(dto), "screening.background.gid.invalid"));
	}

	@Test
	public void validate_DrugTestUSA_success() {
		ScreeningDTO dto = validUsaDto();
		dto.setScreeningType(Screening.DRUG_TEST_TYPE);
		assertFalse(validate(validUsaDto()).hasErrors());
	}

	@Test
	public void validate_DrugTestCanada_fail() {
		ScreeningDTO dto = validCanadaDto();
		dto.setScreeningType(Screening.DRUG_TEST_TYPE);
		assertTrue(hasErrorCode(validate(dto), "screening.drug.country.invalid"));
	}

	@Test
	public void validate_DrugTestInternational_fail() {
		ScreeningDTO dto = validInternationalDto();
		dto.setScreeningType(Screening.DRUG_TEST_TYPE);
		assertTrue(hasErrorCode(validate(dto), "screening.drug.country.invalid"));
	}

	private ScreeningDTO validUsaDto() {
		ScreeningDTO dto = new ScreeningDTO();
		dto.setFirstName("Roddy");
		dto.setLastName("Piper");
		dto.setBirthYear(2014);
		dto.setBirthMonth(12);
		dto.setBirthDay(5);
		dto.setAddress1("12 Vince McMahon Blvd");
		dto.setState("CT");
		dto.setCity("Greenwich");
		dto.setPostalCode("06831");
		dto.setCountry("USA");
		dto.setEmail("vince@wwe.com");
		dto.setWorkIdentificationNumber("602-39-2427");
		dto.setScreeningType(Screening.BACKGROUND_CHECK_TYPE);
		return dto;
	}

	private ScreeningDTO validCanadaDto() {
		ScreeningDTO dto = new ScreeningDTO();
		dto.setFirstName("Brett");
		dto.setLastName("Hart");
		dto.setBirthYear(2014);
		dto.setBirthMonth(12);
		dto.setBirthDay(5);
		dto.setAddress1("12 Hitman Blvd");
		dto.setState("AB");
		dto.setCity("Calgary");
		dto.setPostalCode("T2E 9A9");
		dto.setCountry("CAN");
		dto.setEmail("bret@wwe.com");
		dto.setWorkIdentificationNumber("144 362 639");
		dto.setScreeningType(Screening.BACKGROUND_CHECK_TYPE);
		return dto;
	}

	private ScreeningDTO validInternationalDto() {
		ScreeningDTO dto = new ScreeningDTO();
		dto.setFirstName("Rudolph");
		dto.setLastName("Schenker");
		dto.setBirthYear(2014);
		dto.setBirthMonth(12);
		dto.setBirthDay(5);
		dto.setAddress1("12 Schnitzel Blvd");
		dto.setState("BV");
		dto.setCity("Munich");
		dto.setPostalCode("123456");
		dto.setCountry("DE");
		dto.setEmail("orndorff@wwe.com");
		dto.setWorkIdentificationNumber("1362639");
		dto.setScreeningType(Screening.BACKGROUND_CHECK_TYPE);
		return dto;
	}

	protected Validator getValidator() {
		return validator;
	}
}