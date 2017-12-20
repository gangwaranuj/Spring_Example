package com.workmarket.web.validators;

import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.dto.ClientContactDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddressBookContactValidatorTest extends BaseValidatorTest {

	@Mock CRMService crmService;
	@Mock DirectoryService directoryService;
	@InjectMocks AddressBookContactValidator validator;

	@Mock ClientContactDTO clientContactDTO;
	@Mock Location location;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS) ClientLocation clientLocation;
	@Mock ClientContact clientContact;
	@Mock ClientCompany clientCompany;
	@Mock ClientCompany clientCompany2;

	@Before
	public void setUp() {
		when(clientContactDTO.getFirstName()).thenReturn(ANY_STRING);
		when(clientContactDTO.getLastName()).thenReturn(ANY_STRING);
		when(clientContactDTO.getWorkPhone()).thenReturn(ANY_STRING);
		when(clientContactDTO.getContactId()).thenReturn(ANY_LONG);
		when(clientContactDTO.getClientCompanyId()).thenReturn(ANY_LONG);
		when(clientContactDTO.getClientLocationId()).thenReturn(ANY_LONG);

		when(crmService.findClientContactById(ANY_LONG)).thenReturn(clientContact);
		when(crmService.findClientCompanyById(ANY_LONG)).thenReturn(clientCompany);
		when(directoryService.findLocationById(ANY_LONG)).thenReturn(location);
		when(directoryService.findClientLocationById(ANY_LONG)).thenReturn(clientLocation);

		when(location.getId()).thenReturn(ANY_LONG);
		when(clientLocation.getClientCompany()).thenReturn(clientCompany2);
		when(clientCompany2.getId()).thenReturn(ANY_LONG);
		when(clientCompany.getId()).thenReturn(ANY_LONG);
	}

	@Test
	public void validate_success() throws Exception {
		assertFalse(validate(clientContactDTO).hasErrors());
	}

	@Test
	public void validate_firstName_empty_failure() throws Exception {
		when(clientContactDTO.getFirstName()).thenReturn(EMPTY_TOKEN);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "firstName"));
		assertTrue(hasErrorCode(errors, "NotNull"));
	}

	@Test
	public void validate_firstName_whiteSpace_failure() throws Exception {
		when(clientContactDTO.getFirstName()).thenReturn(WHITE_SPACE_TOKEN);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "firstName"));
		assertTrue(hasErrorCode(errors, "NotNull"));
	}

	@Test
	public void validate_firstName_null_failure() throws Exception {
		when(clientContactDTO.getFirstName()).thenReturn(null);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "firstName"));
		assertTrue(hasErrorCode(errors, "NotNull"));
	}

	@Test
	public void validate_lastName_empty_failure() throws Exception {
		when(clientContactDTO.getLastName()).thenReturn(EMPTY_TOKEN);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "lastName"));
		assertTrue(hasErrorCode(errors, "NotNull"));
	}

	@Test
	public void validate_lastName_whiteSpace_failure() throws Exception {
		when(clientContactDTO.getLastName()).thenReturn(WHITE_SPACE_TOKEN);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "lastName"));
		assertTrue(hasErrorCode(errors, "NotNull"));
	}

	@Test
	public void validate_lastName_null_failure() throws Exception {
		when(clientContactDTO.getLastName()).thenReturn(null);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "lastName"));
		assertTrue(hasErrorCode(errors, "NotNull"));
	}

	@Test
	public void validate_workPhone_empty_failure() throws Exception {
		when(clientContactDTO.getWorkPhone()).thenReturn(EMPTY_TOKEN);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "workPhone"));
		assertTrue(hasErrorCode(errors, "NotNull"));
	}

	@Test
	public void validate_workPhone_whiteSpace_failure() throws Exception {
		when(clientContactDTO.getWorkPhone()).thenReturn(WHITE_SPACE_TOKEN);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "workPhone"));
		assertTrue(hasErrorCode(errors, "NotNull"));
	}

	@Test
	public void validate_workPhone_null_failure() throws Exception {
		when(clientContactDTO.getWorkPhone()).thenReturn(null);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "workPhone"));
		assertTrue(hasErrorCode(errors, "NotNull"));
	}

	@Test
	public void validate_clientContactIdPresent_clientContactNotFound_failure() throws Exception {
		when(crmService.findClientContactById(ANY_LONG)).thenReturn(null);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "contactId"));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	@Test
	public void validate_clientCompanyIdPresent_clientCompanyNotFound_failure() throws Exception {
		when(crmService.findClientCompanyById(ANY_LONG)).thenReturn(null);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "clientCompanyId"));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	@Test
	public void validate_locationIdPresent_locationNotFound_failure() throws Exception {
		when(directoryService.findLocationById(ANY_LONG)).thenReturn(null);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "clientLocationId"));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	@Test
	public void validate_clientCompanyMismatch_clientCompany_failure() throws Exception {
		when(clientLocation.getClientCompany().getId()).thenReturn(ANY_LONG_2);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "clientCompanyId"));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	@Test
	public void validate_clientCompanyMismatch_locationClientCompany_failure() throws Exception {
		when(clientCompany.getId()).thenReturn(ANY_LONG_2);

		Errors errors = validate(clientContactDTO);
		assertTrue(hasFieldInError(errors, "clientCompanyId"));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	protected Validator getValidator() {
		return validator;
	}
}
