package com.workmarket.web.validators;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserImportDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserImportValidatorTest extends BaseValidatorTest {

	@Mock AuthenticationService authenticationService;
	@Mock UserService userService;
	@InjectMocks UserImportValidator validator;
	@Mock UserImportDTO dto;
	@Mock MessageBundleHelper messageBundleHelper;
	@Mock User user;
	@Mock AclRole aclRole;


	@Override
	protected Validator getValidator() {
		return validator;
	}

	@Before
	public void setUp() {
		when(dto.getFirstName()).thenReturn(ANY_STRING);
		when(dto.getLastName()).thenReturn(ANY_STRING);
		when(dto.getEmail()).thenReturn("joe.smith@workmarket.com");
		when(dto.getWorkPhone()).thenReturn("(212) 123-4567");
		when(dto.getRole()).thenReturn("Administrator");
		when(userService.findUserByEmail(dto.getEmail())).thenReturn(user);
		when(authenticationService.findSystemRoleByName(dto.getRole())).thenReturn(aclRole);
		when(messageBundleHelper.getMessage("users.upload.row_emailExists")).thenReturn(String.format("Email address %s you entered is associated with another user. Each user must have a unique email address. Please update this record and upload the file again.", ANY_STRING));
	}

	@Test
	public void validate_success() throws Exception {
		when(userService.findUserByEmail(dto.getEmail())).thenReturn(null);
		assertFalse(validate(dto).hasErrors());
	}

	@Test
	public void validate_firstName_empty_failure() throws Exception {
		when(dto.getFirstName()).thenReturn(EMPTY_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "firstName"));
		assertTrue(hasErrorCode(errors, "firstName"));
	}

	@Test
	public void validate_firstName_whitespace_failure() throws Exception {
		when(dto.getFirstName()).thenReturn(WHITE_SPACE_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "firstName"));
		assertTrue(hasErrorCode(errors, "firstName"));
	}

	@Test
	public void validate_firstName_null_failure() throws Exception {
		when(dto.getFirstName()).thenReturn(null);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "firstName"));
		assertTrue(hasErrorCode(errors, "firstName"));
	}

	@Test
	public void validate_lastName_empty_failure() throws Exception {
		when(dto.getLastName()).thenReturn(EMPTY_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "lastName"));
		assertTrue(hasErrorCode(errors, "lastName"));
	}

	@Test
	public void validate_lastName_whiteSpace_failure() throws Exception {
		when(dto.getLastName()).thenReturn(WHITE_SPACE_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "lastName"));
		assertTrue(hasErrorCode(errors, "lastName"));
	}

	@Test
	public void validate_lastName_null_failure() throws Exception {
		when(dto.getLastName()).thenReturn(null);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "lastName"));
		assertTrue(hasErrorCode(errors, "lastName"));
	}

	@Test
	public void validate_workPhone_empty_failure() throws Exception {
		when(dto.getWorkPhone()).thenReturn(EMPTY_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "workPhone"));
		assertTrue(hasErrorCode(errors, "workPhone"));
	}

	@Test
	public void validate_workPhone_whiteSpace_failure() throws Exception {
		when(dto.getWorkPhone()).thenReturn(WHITE_SPACE_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "workPhone"));
		assertTrue(hasErrorCode(errors, "workPhone"));
	}

	@Test
	public void validate_workPhone_null_failure() throws Exception {
		when(dto.getWorkPhone()).thenReturn(null);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "workPhone"));
		assertTrue(hasErrorCode(errors, "workPhone"));
	}

	@Test
	public void validate_workPhone_alphanumeric_failure() throws Exception {
		when(dto.getWorkPhone()).thenReturn("(800)999-HELP");
		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "workPhone"));
		assertTrue(hasErrorCode(errors, "invalid_workPhone"));
	}

	@Test
	public void validate_email_empty_failure() throws Exception {
		when(dto.getEmail()).thenReturn(EMPTY_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "email"));
		assertTrue(hasErrorCode(errors, "email"));
	}

	@Test
	public void validate_email_whiteSpace_failure() throws Exception {
		when(dto.getEmail()).thenReturn(WHITE_SPACE_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "email"));
		assertTrue(hasErrorCode(errors, "email"));
	}

	@Test
	public void validate_email_null_failure() throws Exception {
		when(dto.getEmail()).thenReturn(null);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "email"));
		assertTrue(hasErrorCode(errors, "email"));
	}

	@Test
	public void validate_email_format_noAtSymbol_failure() throws Exception {
		when(dto.getEmail()).thenReturn("john.smithdomain.com");
		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "email"));
		assertTrue(hasErrorCode(errors, "invalid_email"));
	}

	@Test
	public void validate_email_format_noLocalPart_failure() throws Exception {
		when(dto.getEmail()).thenReturn("@domain.com");
		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "email"));
		assertTrue(hasErrorCode(errors, "invalid_email"));
	}

	@Test
	public void validate_email_format_noDomainPart_failure() throws Exception {
		when(dto.getEmail()).thenReturn("local@");
		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "email"));
		assertTrue(hasErrorCode(errors, "invalid_email"));
	}

	@Test
	public void validate_email_format_nonDomain_success() throws Exception {
		when(dto.getEmail()).thenReturn("local@domain");
		Errors errors = validate(dto);
		assertFalse(hasFieldInError(errors, "email"));
		assertFalse(hasErrorCode(errors, "invalid_email"));
	}

	@Test
	public void validate_email_existed_failure() throws Exception {
		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "email"));
		assertTrue(hasErrorCode(errors, "email_exists"));
	}

	@Test
	public void validate_role_empty_failure() throws Exception {
		when(dto.getRole()).thenReturn(EMPTY_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "role"));
		assertTrue(hasErrorCode(errors, "role"));
	}

	@Test
	public void validate_role_whiteSpace_failure() throws Exception {
		when(dto.getRole()).thenReturn(WHITE_SPACE_TOKEN);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "role"));
		assertTrue(hasErrorCode(errors, "role"));
	}

	@Test
	public void validate_role_null_failure() throws Exception {
		when(dto.getRole()).thenReturn(null);

		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "role"));
		assertTrue(hasErrorCode(errors, "role"));
	}

	@Test
	public void validate_role_existed_failure() throws Exception {
		when(dto.getRole()).thenReturn(ANY_STRING);
		when(authenticationService.findSystemRoleByName(dto.getRole())).thenReturn(null);
		Errors errors = validate(dto);
		assertTrue(hasFieldInError(errors, "role"));
		assertTrue(hasErrorCode(errors, "invalid_role"));
	}
}
