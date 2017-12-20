package com.workmarket.web.validators;

import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddressBookLocationValidatorTest extends BaseValidatorTest {

	@Mock InvariantDataService invariantService;
	@Mock CRMService crmService;
	@Mock MessageBundleHelper messageBundleHelper;
	@InjectMocks AddressBookLocationValidator validator;

	@Mock LocationDTO locationDTO;
	@Mock ClientCompany clientCompany;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS) PostalCode postalCode;

	@Before
	public void setUp() {
		when(messageBundleHelper.getMessage(any(String.class))).thenReturn("Some validation message goes here");
		when(locationDTO.getAddress1()).thenReturn(ANY_STRING);
		when(locationDTO.getAddress2()).thenReturn(ANY_STRING);
		when(locationDTO.getCity()).thenReturn(ANY_STRING);
		when(locationDTO.getPostalCode()).thenReturn(ANY_STRING);
		when(locationDTO.getState()).thenReturn(ANY_STRING);
		when(locationDTO.getCountry()).thenReturn(ANY_STRING);
		when(locationDTO.getClientCompanyId()).thenReturn(ANY_LONG);
		when(invariantService.findOrSavePostalCode(locationDTO)).thenReturn(postalCode);
		when(crmService.findClientCompanyById(ANY_LONG)).thenReturn(clientCompany);
		when(postalCode.getPostalCode()).thenReturn(ANY_STRING);
		when(postalCode.getStateProvince().getShortName()).thenReturn(ANY_STRING);
		when(postalCode.getStateProvince().getName()).thenReturn(ANY_STRING);
	}

	@Test
	public void validate_noErrors() throws Exception {
		assertFalse(validate(locationDTO).hasErrors());
	}

	@Test
	public void validate_invalidPostalCode_addError() throws Exception {
		when(invariantService.findOrSavePostalCode(locationDTO)).thenReturn(null);
		Errors errors = validate(locationDTO);
		assertTrue(hasFieldInError(errors, "postalCode"));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	@Test
	public void validate_postalCodeMismatch_addError() throws Exception {
		when(postalCode.getPostalCode()).thenReturn(ANY_STRING_2);
		Errors errors = validate(locationDTO);
		assertTrue(hasFieldInError(errors, "postalCode"));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	@Test
	public void validate_stateCodeMismatch_addError() throws Exception {
		when(postalCode.getStateProvince().getShortName()).thenReturn(ANY_STRING_2);
		when(postalCode.getStateProvince().getName()).thenReturn(ANY_STRING_2);
		Errors errors = validate(locationDTO);
		assertTrue(hasFieldInError(errors, "state"));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	@Test
	public void validate_invalidClientCompany_addError() throws Exception {
		when(crmService.findClientCompanyById(ANY_LONG)).thenReturn(null);
		Errors errors = validate(locationDTO);
		assertTrue(hasFieldInError(errors, "clientCompanyId"));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	protected Validator getValidator() {
		return validator;
	}

}