package com.workmarket.service.business.dto.validation;

import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.web.validators.PasswordValidator;
import com.workmarket.web.validators.UserEmailValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateNewWorkerRequestValidatorTest {
	private static final String EMAIL = "taken@taken.com";

	@Mock
	private UserService userService;

	@Mock
	private UserEmailValidator emailValidator;

	@Mock
	private PasswordValidator passwordValidator;

	@InjectMocks
	private CreateNewWorkerDTOValidator createNewWorkerDTOValidator;

	@Before
	public void setup() {
		createNewWorkerDTOValidator = new CreateNewWorkerDTOValidator(emailValidator, passwordValidator, userService);
	}

	@Test
	public void testFirstName_blank() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setFirstName(
			null).build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertFalse("Expected blank first name is allowed", bindingResult.hasFieldErrors("firstName"));

		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testFirstName_tooShort() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setFirstName(
			"1").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected first name error", bindingResult.hasFieldErrors("firstName"));
		assertTrue("Expected first name format error",
			bindingResult.getFieldError("firstName").getCode().equals("user.validation.firstNameFormat"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testFirstName_tooLong() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setFirstName(
			"123456789012345678901234567890123456789012345678901").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected first name error", bindingResult.hasFieldErrors("firstName"));
		assertTrue("Expected first name format error",
			bindingResult.getFieldError("firstName").getCode().equals("user.validation.firstNameFormat"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testLastName_blank() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setLastName(
			null).build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertFalse("Expected blank last name is allowed", bindingResult.hasFieldErrors("lastName"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testLastName_tooShort() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setLastName(
			"1").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected last name error", bindingResult.hasFieldErrors("lastName"));
		assertTrue("Expected last name format error",
			bindingResult.getFieldError("lastName").getCode().equals("user.validation.lastNameFormat"));
	}

	@Test
	public void testLastName_tooLong() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setLastName(
			"123456789012345678901234567890123456789012345678901").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected last name error", bindingResult.hasFieldErrors("lastName"));
		assertTrue("Expected last name format error",
			bindingResult.getFieldError("lastName").getCode().equals("user.validation.lastNameFormat"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testLastName_legitSpecialCharsOk() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setLastName(
			"Parker, Jr.").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertFalse("Expected no last name error", bindingResult.hasFieldErrors("lastName"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testEmail_notTaken() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder()
			.setEmail(EMAIL).build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createCompanyRequest");
		when(userService.findUserByEmail(EMAIL)).thenReturn(new User());

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected email taken error", bindingResult.hasFieldErrors("email"));
		assertTrue("Expected email taken error to match code", bindingResult.getFieldError("email").getCode().equals("user.validation.emailExists"));
		verify(userService, times(1)).findUserByEmail(eq(EMAIL));
	}

	@Test
	public void testPassword_required() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setPassword(
			null).build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected password error", bindingResult.hasFieldErrors("password"));
		assertTrue("Expected password invalid",
			bindingResult.getFieldError("password").getCode().equals("mysettings.password.newPassword.invalid"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testPassword_validated() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setPassword(
			"password1").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
		verify(passwordValidator, times(1)).validate(eq(createNewWorkerRequest.getPassword()),
			eq(""),
			eq(bindingResult));
	}


	@Test
	public void testInvalidAddress() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setPostalCode(
			"abc123").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createCompanyRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected address City error", bindingResult.hasFieldErrors("city"));
		assertFalse("Expected address Postal Code error", bindingResult.hasFieldErrors("postalCode"));
		assertTrue("Expected address Country error", bindingResult.hasFieldErrors("country"));

		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}


	@Test
	public void testAddress1_tooLong() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setPostalCode("11218")
			.setAddress1(
				"12345678901234567890123456789012345678901234567890" +
				"12345678901234567890123456789012345678901234567890" +
				"12345678901234567890123456789012345678901234567890" +
				"123456789012345678901234567890123456789012345678901").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected address1 error", bindingResult.hasFieldErrors("address1"));
		assertTrue("Expected address1 format error",
			bindingResult.getFieldError("address1").getCode().equals("address.address1.too_long"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testAddress1and2_legitSpecialCharacters_ok() {
		final CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setPostalCode("11218")
			.setAddress1("1 Main St., - #5")
			.setAddress2("c/o Bob")
			.build();

		final BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertFalse("Expected no address1 error", bindingResult.hasFieldErrors("address1"));
		assertFalse("Expected no address2 error", bindingResult.hasFieldErrors("address2"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}


	@Test
	public void testAddress2_tooLong() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setPostalCode("11218")
			.setAddress2(
				"12345678901234567890123456789012345678901234567890" +
				"123456789012345678901234567890123456789012345678901").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected address2 error", bindingResult.hasFieldErrors("address2"));
		assertTrue("Expected address2 format error",
			bindingResult.getFieldError("address2").getCode().equals("address.address2.too_long"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testAddress1_ok() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setPostalCode("11218")
			.setAddress1(
				"12345678901234567890123456789012345678901234567890" +
				"12345678901234567890123456789012345678901234567890" +
				"12345678901234567890123456789012345678901234567890" +
				"12345678901234567890123456789012345678901234567890").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertFalse("Expected address1 error", bindingResult.hasFieldErrors("address1"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testAddress2_tooOk() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder().setPostalCode("11218")
			.setAddress2(
				"12345678901234567890123456789012345678901234567890" +
				"12345678901234567890123456789012345678901234567890").build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createNewWorkerRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertFalse("Expected address2 error", bindingResult.hasFieldErrors("address2"));
		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}


	@Test
	public void testInvalid_lat_no_lon() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder()
			.setPostalCode("sefg089u")
			.setLongitude(new BigDecimal(-73.992391)).build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createCompanyRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected address Latitude error", bindingResult.hasFieldErrors("latitude"));

		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testInvalid_lon_no_lat() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder()
			.setPostalCode("sefg089u")
			.setLatitude(new BigDecimal(40.740256)).build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createCompanyRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertTrue("Expected address Longitude error", bindingResult.hasFieldErrors("longitude"));

		verify(emailValidator, times(1)).validate(eq(createNewWorkerRequest.getEmail()), eq(bindingResult));
	}

	@Test
	public void testValidWithAddress() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder()
			.setFirstName("Josh")
			.setLastName("Levine")
			.setEmail("jlevine+" + System.currentTimeMillis() + "@workmarket.com")
			.setPassword(System.currentTimeMillis() + "L")
			.setAddress1("Address 1")
			.setAddress2("Address 2")
			.setCity("Brooklyn")
			.setState("NY")
			.setPostalCode("11218")
			.setCountry("USA")
			.setLongitude(new BigDecimal(-73.992391))
			.setLatitude(new BigDecimal(40.740256))
			.build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createCompanyRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertEquals("Expect no errors", 0, bindingResult.getErrorCount());
	}

	@Test
	public void testValidWithoutAddress() {
		CreateNewWorkerRequest createNewWorkerRequest = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder()
			.setFirstName("Josh")
			.setLastName("Levine")
			.setEmail("jlevine+" + System.currentTimeMillis() + "@workmarket.com")
			.setPassword(System.currentTimeMillis() + "L")
			.build();

		BindingResult bindingResult = new BindException(createNewWorkerRequest, "createCompanyRequest");

		createNewWorkerDTOValidator.validate(createNewWorkerRequest, bindingResult);

		assertEquals("Expect no errors", 0, bindingResult.getErrorCount());
	}

}
