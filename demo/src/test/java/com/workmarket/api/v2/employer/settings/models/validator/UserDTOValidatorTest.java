package com.workmarket.api.v2.employer.settings.models.validator;

import com.workmarket.api.v2.employer.settings.controllers.support.UserMaker;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.api.v2.employer.settings.models.validator.UserDTOValidator;
import com.workmarket.web.validators.BaseValidatorTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static com.natpryce.makeiteasy.MakeItEasy.withNull;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.email;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.firstName;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.industryId;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.lastName;
import static com.workmarket.api.v2.employer.settings.controllers.support.UserMaker.spendLimit;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDTOValidatorTest extends BaseValidatorTest {

	@InjectMocks private UserDTOValidator validator;
	@Mock private User existingUser;
	@Mock private UserService userService;

	@Override
	protected Validator getValidator() {
		return validator;
	}

	@Before
	public void setUp() {
		when(userService.findUserByEmail(any(String.class))).thenReturn(existingUser);
		when(existingUser.getId()).thenReturn(1L);
		when(existingUser.getEmail()).thenReturn("someone@workmarket");
	}

	@Test
	public void test_User_Null_FirstName() {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(firstName)));
		assertTrue(hasErrorCode(validate(employee), "user.validation.firstNameRequired"));
		assertTrue(hasFieldInError(validate(employee), "firstName"));
	}

	@Test
	public void test_User_Null_LastName() {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(lastName)));
		assertTrue(hasErrorCode(validate(employee), "user.validation.lastNameRequired"));
		assertTrue(hasFieldInError(validate(employee), "lastName"));
	}

	@Test
	public void test_User_Null_Email() {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(email)));
		assertTrue(hasErrorCode(validate(employee), "user.validation.emailRequired"));
		assertTrue(hasFieldInError(validate(employee), "email"));
	}

	@Test
	public void test_User_Invalid_Email() {
		UserDTO employee = make(a(UserMaker.UserDTO, with(email, "abc@.com")));
		assertTrue(hasErrorCode(validate(employee), "user.validation.emailInvalid"));
		assertTrue(hasFieldInError(validate(employee), "email"));
	}

	@Test
	public void test_User_Duplicate_Email() {
		UserDTO employee = make(a(UserMaker.UserDTO, with(email, "someone@workmarket")));
		assertTrue(hasErrorCode(validate(employee), "user.validation.emailExists"));
		assertTrue(hasFieldInError(validate(employee), "email"));
	}

	@Test
	public void test_User_Null_SpendLimit() {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(spendLimit)));
		assertTrue(hasErrorCode(validate(employee), "user.validation.spendLimit"));
		assertTrue(hasFieldInError(validate(employee), "spendLimit"));
	}

	@Test
	public void test_User_Negative_SpendLimit() {
		UserDTO employee = make(a(UserMaker.UserDTO, with(spendLimit, new BigDecimal(-1))));
		assertTrue(hasErrorCode(validate(employee), "user.validation.spendLimit"));
		assertTrue(hasFieldInError(validate(employee), "spendLimit"));
	}

	@Test
	public void test_User_Null_IndustryId() {
		UserDTO employee = make(a(UserMaker.UserDTO, withNull(industryId)));
		assertTrue(hasErrorCode(validate(employee), "user.validation.industryRequired"));
		assertTrue(hasFieldInError(validate(employee), "industryId"));
	}

	@Test
	public void test_User_No_Roles() {
		UserDTO employee = make(a(UserMaker.UserDTOWithNoRole));
		assertTrue(hasErrorCode(validate(employee), "user.validation.notWorkerRolesRequired"));
		assertTrue(hasFieldInError(validate(employee), "roleSettings"));
	}
}
