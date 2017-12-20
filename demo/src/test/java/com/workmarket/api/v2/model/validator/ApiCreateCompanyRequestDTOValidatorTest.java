package com.workmarket.api.v2.model.validator;

import com.workmarket.api.v2.model.AddressApiDTO;
import com.workmarket.api.v2.model.ApiCreateCompanyRequestDTO;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.web.validators.PasswordValidator;
import com.workmarket.web.validators.UserEmailValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


/**
 * Created by joshlevine on 4/25/17.
 */
@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ApiCreateCompanyRequestDTOValidatorTest {
	public static final String EMAIL = "taken@taken.com";
	@Mock
	private UserService userService;

	@Mock
	private UserEmailValidator emailValidator;

	@Mock
	private PasswordValidator passwordValidator;

	@InjectMocks
	private ApiCreateCompanyRequestDTOValidator apiCreateCompanyRequestDTOValidator;

	@Before
	public void setup() {
		apiCreateCompanyRequestDTOValidator = new ApiCreateCompanyRequestDTOValidator(emailValidator, passwordValidator, userService);
	}

	@Test
	public void testFirstName_blank() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.firstName(null).build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected first name error", bindingResult.hasFieldErrors("firstName"));
		assertTrue("Expected first name required", bindingResult.getFieldError("firstName").getCode().equals("user.validation.firstNameRequired"));

		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testFirstName_tooShort() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.firstName("1").build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected first name error", bindingResult.hasFieldErrors("firstName"));
		assertTrue("Expected first name format error", bindingResult.getFieldError("firstName").getCode().equals("user.validation.firstNameFormat"));
		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testFirstName_tooLong() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.firstName("123456789012345678901234567890123456789012345678901").build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected first name error", bindingResult.hasFieldErrors("firstName"));
		assertTrue("Expected first name format error", bindingResult.getFieldError("firstName").getCode().equals("user.validation.firstNameFormat"));
		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testLastName_blank() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.lastName(null).build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected last name error", bindingResult.hasFieldErrors("lastName"));
		assertTrue("Expected last name required", bindingResult.getFieldError("lastName").getCode().equals("user.validation.lastNameRequired"));
		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testLastName_tooShort() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.lastName("1").build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected last name error", bindingResult.hasFieldErrors("lastName"));
		assertTrue("Expected last name format error", bindingResult.getFieldError("lastName").getCode().equals("user.validation.lastNameFormat"));
	}

	@Test
	public void testLastName_tooLong() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.lastName("123456789012345678901234567890123456789012345678901").build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected last name error", bindingResult.hasFieldErrors("lastName"));
		assertTrue("Expected last name format error", bindingResult.getFieldError("lastName").getCode().equals("user.validation.lastNameFormat"));
		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testPassword_required() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.password(null).build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected password error", bindingResult.hasFieldErrors("password"));
		assertTrue("Expected password invalid", bindingResult.getFieldError("password").getCode().equals("mysettings.password.newPassword.invalid"));
		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testEmail_notTaken() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.userEmail(EMAIL).build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");
		when(userService.findUserByEmail(EMAIL)).thenReturn(new User());

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected email taken error", bindingResult.hasFieldErrors("userEmail"));
		assertTrue("Expected email taken error to match code", bindingResult.getFieldError("userEmail").getCode().equals("user.validation.emailExists"));
		verify(userService, times(1)).findUserByEmail(eq(EMAIL));
	}

	@Test
	public void testCompanyName_blank() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.companyName(null).build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected companyName error", bindingResult.hasFieldErrors("companyName"));
		assertTrue("Expected companyName required", bindingResult.getFieldError("companyName").getCode().equals("company.name.required"));
		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testCompanyName_tooLong() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.companyName("123456789012345678901234567890123456789012345678901").build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected company name error", bindingResult.hasFieldErrors("companyName"));
		assertTrue("Expected company name length error", bindingResult.getFieldError("companyName").getCode().equals("company.name.max.length"));
		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testIndustryId_blank() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.industryId(null).build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected industryId error", bindingResult.hasFieldErrors("industryId"));
		assertTrue("Expected industryId required", bindingResult.getFieldError("industryId").getCode().equals("user.validation.industryRequired"));
		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testInvalidAddress() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder().address(new AddressApiDTO.Builder().build()).build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected address line 1 error", bindingResult.hasFieldErrors("address.addressLine1"));
		assertTrue("Expected address City error", bindingResult.hasFieldErrors("address.city"));
		assertTrue("Expected address Postal Code error", bindingResult.hasFieldErrors("address.postalCode"));
		assertTrue("Expected address Country error", bindingResult.hasFieldErrors("address.country"));
		assertTrue("Expected industryId required", bindingResult.getFieldError("industryId").getCode().equals("user.validation.industryRequired"));

		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testInvalid_lat_no_lon() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder().address(
				new AddressApiDTO.Builder().setLongitude(new BigDecimal(-73.992391)).build()).build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected address Latitude error", bindingResult.hasFieldErrors("address.latitude"));

		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testInvalid_lon_no_lat() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder().address(
				new AddressApiDTO.Builder().setLatitude(new BigDecimal(40.740256)).build()).build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertTrue("Expected address Longitude error", bindingResult.hasFieldErrors("address.longitude"));

		verify(emailValidator, times(1)).validate(eq(apiCreateCompanyRequestDTO.getUserEmail()), eq(bindingResult));
	}

	@Test
	public void testValidWithAddress() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.firstName("Josh")
				.lastName("Levine")
				.industryId(22L)
				.companyName("Company")
				.userEmail("jlevine+" + System.currentTimeMillis() + "@workmarket.com")
				.password(System.currentTimeMillis() + "L")
				.address(new AddressApiDTO.Builder()
						.setAddressLine1("653 19th Street")
						.setCity("Brooklyn")
						.setState("NY")
						.setPostalCode("11218")
						.setCountry("USA")
						.setLongitude(new BigDecimal(-73.992391))
						.setLatitude(new BigDecimal(40.740256)).build())
				.build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertEquals("Expect no errors", 0, bindingResult.getErrorCount());
	}

	@Test
	public void testValidWithoutAddress() {
		ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
				.firstName("Josh")
				.industryId(22L)
				.lastName("Levine")
				.companyName("Company")
				.userEmail("jlevine+" + System.currentTimeMillis() + "@workmarket.com")
				.password(System.currentTimeMillis() + "L")
				.build();

		BindingResult bindingResult = new BindException(apiCreateCompanyRequestDTO, "createCompanyRequest");

		apiCreateCompanyRequestDTOValidator.validate(apiCreateCompanyRequestDTO, bindingResult);

		assertEquals("Expect no errors", 0, bindingResult.getErrorCount());
	}

}
