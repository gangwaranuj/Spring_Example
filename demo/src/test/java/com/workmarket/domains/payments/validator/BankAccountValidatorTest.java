package com.workmarket.domains.payments.validators;

import com.workmarket.BaseUnitTest;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.validator.BankAccountValidator;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.validators.BaseValidatorTest;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankAccountValidatorTest extends BaseValidatorTest {
	private static final String INSTITUTION_NUMBER = "678";
	private static final String BRANCH_NUMBER = "12345";
	private static final String NAME_ON_ACCOUNT = "NO NAME";
	private static final String BANK_NAME = "Acme Bank";
	private static final String ROUTING_NUMBER = "067812345";

	@Mock InvariantDataService dataService;
	@Mock SecurityContextFacade securityContext;
	@Mock FeatureEvaluator featureEvaluator;

	Errors errors;
	BankAccountDTO dto;
	BankAccountValidator validator;
	ExtendedUserDetails userDetails;

	@Captor ArgumentCaptor<Object[]> captor;

	@Before
	public void setUp() throws Exception {
		dto = mock(BankAccountDTO.class);
		errors = mock(Errors.class);

		userDetails = mock(ExtendedUserDetails.class);
		when(userDetails.getId()).thenReturn(new Long(12345l));
		when(userDetails.getTaxCountry()).thenReturn(Country.USA_COUNTRY);
		when(securityContext.getCurrentUser()).thenReturn(userDetails);
		when(dto.getCountry()).thenReturn(Country.USA);
		when(dto.getBranchNumber()).thenReturn(BRANCH_NUMBER);
		when(dto.getInstitutionNumber()).thenReturn(INSTITUTION_NUMBER);
		when(dto.getNameOnAccount()).thenReturn(NAME_ON_ACCOUNT);
		when(dto.getBankName()).thenReturn(BANK_NAME);

		when(featureEvaluator.hasFeature(any(UserDetails.class), anyString())).thenReturn(Boolean.TRUE);

		validator = new BankAccountValidator(dataService, securityContext);
	}

	@Test
	public void validate_GetsTheBankAccountDTOsType() throws Exception {
		validator.validate(dto, errors);
		verify(dto).getType();
	}

	@Test
	public void validate_WhenTypeIsACH_GetsTheBankNameFieldValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		validator.validate(dto, errors);
		verify(errors).getFieldValue("bankName");
	}

	@Test
	public void validate_WhenTypeIsACHAndBankNameIsNull_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("bankName")).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue("bankName", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndBankNameIsEmpty_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("bankName")).thenReturn(BaseUnitTest.EMPTY_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("bankName", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndBankNameIsBlank_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("bankName")).thenReturn(BaseUnitTest.WHITE_SPACE_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("bankName", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACH_GetsTheNameOnAccountFieldValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		validator.validate(dto, errors);
		verify(errors).getFieldValue("nameOnAccount");
	}

	@Test
	public void validate_WhenTypeIsACHAndNameOnAccountIsNull_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("nameOnAccount")).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue("nameOnAccount", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndNameOnAccountIsEmpty_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("nameOnAccount")).thenReturn(BaseUnitTest.EMPTY_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("nameOnAccount", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndNameOnAccountIsBlank_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("nameOnAccount")).thenReturn(BaseUnitTest.WHITE_SPACE_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("nameOnAccount", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndRoutingNumberIsNull_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("routingNumber")).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue("routingNumber", "banking.routingNumber.invalid", CollectionUtilities.newArray("Routing Number"), "Invalid Routing Number");
	}

	@Test
	public void validate_WhenTypeIsACHAndRoutingNumberIsEmpty_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("routingNumber")).thenReturn(BaseUnitTest.EMPTY_TOKEN);
		when(dto.getRoutingNumber()).thenReturn(BaseUnitTest.EMPTY_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("routingNumber", "banking.routingNumber.invalid", CollectionUtilities.newArray("Routing Number"), "Invalid Routing Number");
	}

	@Test
	public void validate_WhenTypeIsACHAndRoutingNumberIsBlank_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("routingNumber")).thenReturn(BaseUnitTest.WHITE_SPACE_TOKEN);
		when(dto.getRoutingNumber()).thenReturn(BaseUnitTest.WHITE_SPACE_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("routingNumber", "banking.routingNumber.invalid", CollectionUtilities.newArray("Routing Number"), "Invalid Routing Number");
	}

	@Test
	public void validate_WhenTypeIsACH_GetsTheAccountNumberFieldValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		validator.validate(dto, errors);
		verify(errors).getFieldValue("accountNumber");
	}

	@Test
	public void validate_WhenTypeIsACHAndAccountNumberIsNull_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("accountNumber")).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue("accountNumber", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndAccountNumberIsEmpty_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("accountNumber")).thenReturn(BaseUnitTest.EMPTY_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("accountNumber", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndAccountNumberIsBlank_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("accountNumber")).thenReturn(BaseUnitTest.WHITE_SPACE_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("accountNumber", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACH_GetsTheAccountNumberConfirmFieldValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		validator.validate(dto, errors);
		verify(errors).getFieldValue("accountNumberConfirm");
	}

	@Test
	public void validate_WhenTypeIsACHAndAccountNumberConfirmIsNull_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("accountNumberConfirm")).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue("accountNumberConfirm", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndAccountNumberConfirmIsEmpty_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("accountNumberConfirm")).thenReturn(BaseUnitTest.EMPTY_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("accountNumberConfirm", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndAccountNumberConfirmIsBlank_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(errors.getFieldValue("accountNumberConfirm")).thenReturn(BaseUnitTest.WHITE_SPACE_TOKEN);
		validator.validate(dto, errors);
		verify(errors).rejectValue("accountNumberConfirm", "NotNull", null, null);
	}

	@Test
	public void validate_WhenTypeIsACHAndBankNameIsTooShort_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankName()).thenReturn(BaseUnitTest.EMPTY_TOKEN);
		validator.validate(dto, errors);

		verify(errors).rejectValue(eq("bankName"), eq("Size"), captor.capture(), eq("Invalid length"));

		Object[] args = captor.getValue();
		assertThat(args[0], Matchers.<Object>is("bank name"));
		assertThat(args[1], Matchers.<Object>is(100));
		assertThat(args[2], Matchers.<Object>is(1));
	}

	@Test
	public void validate_WhenTypeIsACHAndBankNameIsTooLong_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankName()).thenReturn(StringUtils.repeat("A", 101));
		validator.validate(dto, errors);
		verify(errors).rejectValue("bankName", "Size", CollectionUtilities.newArray("bank name", 100, 1), "Invalid length");
	}

	@Test
	public void validate_WhenTypeIsACHAndBankNameIsJustRight_NeverRejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankName()).thenReturn(StringUtils.repeat("A", 50));
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue("bankName", "Size", CollectionUtilities.newArray("bank name", 100, 1), "Invalid length");
	}


	@Test
	public void validate_WhenTypeIsACHAndNameOnAccountIsTooShort_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getNameOnAccount()).thenReturn(BaseUnitTest.EMPTY_TOKEN);
		validator.validate(dto, errors);

		verify(errors).rejectValue(eq("nameOnAccount"), eq("Size"), captor.capture(), eq("Invalid length"));

		Object[] args = captor.getValue();
		assertThat(args[0], Matchers.<Object>is("name on account"));
		assertThat(args[1], Matchers.<Object>is(45));
		assertThat(args[2], Matchers.<Object>is(1));
	}

	@Test
	public void validate_WhenTypeIsACHAndNameOnAccountIsTooLong_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getNameOnAccount()).thenReturn(StringUtils.repeat("A", 46));
		validator.validate(dto, errors);
		verify(errors).rejectValue("nameOnAccount", "Size", CollectionUtilities.newArray("name on account", 45, 1), "Invalid length");
	}

	@Test
	public void validate_WhenTypeIsACHAndNameOnAccountIsJustRight_NeverRejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getNameOnAccount()).thenReturn(StringUtils.repeat("A", 25));
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue("nameOnAccount", "Size", CollectionUtilities.newArray("name on account", 45, 1), "Invalid length");
	}

	@Test
	public void validate_WhenTypeIsACHAndRoutingNumberIsTooShort_RejectsTheValue() throws Exception {
		String routingNumber = StringUtils.repeat("8", 8);
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getRoutingNumber()).thenReturn(routingNumber);
		validator.validate(dto, errors);
		verify(errors).rejectValue("routingNumber", "banking.routingNumber.invalid", CollectionUtilities.newArray("Routing Number"), "Invalid Routing Number");
	}

	@Test
	public void validate_WhenTypeIsACHAndRoutingNumberIsTooLong_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getNameOnAccount()).thenReturn(StringUtils.repeat("A", 10));
		validator.validate(dto, errors);
		verify(errors).rejectValue("routingNumber", "banking.routingNumber.invalid", CollectionUtilities.newArray("Routing Number"), "Invalid Routing Number");
	}

	@Test
	public void validate_WhenTypeIsACHAndRoutingNumberIsJustRight_NeverRejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getNameOnAccount()).thenReturn(StringUtils.repeat("A", 25));
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue("routingNumber", "Size", CollectionUtilities.newArray("routing number", 45, 1), "Invalid length");
	}

	@Test
	public void validate_WhenTypeIsACHAndBankAccountTypeIsNotCheckingOrSavings_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankAccountTypeCode()).thenReturn(BankAccountType.PAY_PAL);
		when(dto.getRoutingNumber()).thenReturn(ROUTING_NUMBER);
		validator.validate(dto, errors);
		verify(errors).rejectValue("bankAccountTypeCode", "Pattern");
	}

	@Test
	public void validate_WhenTypeIsACHAndBankAccountTypeIsChecking_NeverRejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankAccountTypeCode()).thenReturn(BankAccountType.CHECKING);
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue("bankAccountTypeCode", "Pattern");
	}

	@Test
	public void validate_WhenTypeIsACHAndBankAccountTypeIsSavings_NeverRejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankAccountTypeCode()).thenReturn(BankAccountType.SAVINGS);
		validator.validate(dto, errors);
		verify(errors, never()).rejectValue("bankAccountTypeCode", "Pattern");
	}

	@Test
	public void validate_WhenTypeIsACHAndBankAccountNumberAndConfirmationDoNotMatch_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getAccountNumber()).thenReturn("12345");
		when(dto.getAccountNumberConfirm()).thenReturn("98765");

		validator.validate(dto, errors);
		verify(errors).rejectValue("accountNumber", "FieldMatch", CollectionUtilities.newArray("Account Number", "Confirm Account Number"), "Does not match");
	}

	@Test
	public void validate_WhenTypeIsACHAndBankAccountNumberAndConfirmationMatches_NeverRejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getAccountNumber()).thenReturn("12345");
		when(dto.getAccountNumberConfirm()).thenReturn("12345");

		validator.validate(dto, errors);
		verify(errors, never()).rejectValue("accountNumber", "FieldMatch", CollectionUtilities.newArray("Account Number", "Confirm Account Number"), "Does not match");
	}

	@Test
	public void validate_WhenTypeIsACHAndRoutingNumberIsInvalid_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getRoutingNumber()).thenReturn("987654321");
		when(dataService.getBankRouting("987654321", Country.USA)).thenReturn(null);

		validator.validate(dto, errors);
		verify(errors).rejectValue("routingNumber", "banking.routingNumber.noBankFound.usa");
	}

	@Test
	public void validate_WhenTypeIsACHAndRoutingNumberIsValid_NeverRejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getRoutingNumber()).thenReturn("987654321");
		when(dataService.getBankRouting("987654321", Country.USA)).thenReturn(mock(BankRouting.class));

		validator.validate(dto, errors);
		verify(errors, never()).rejectValue("routingNumber", "banking.routingNumber.noBankFound.usa");
	}

	@Test
	public void validate_WhenTypeIsACHAndBankCountryDoesNotMatchUsersTaxCountry_RejectsTheValue() throws Exception {
		BankRouting bankRouting = mock(BankRouting.class);
		when(bankRouting.getCountry()).thenReturn(Country.CANADA_COUNTRY);

		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getRoutingNumber()).thenReturn("987654321");
		when(dto.getCountry()).thenReturn(Country.USA);
		when(dataService.getBankRouting("987654321", Country.USA)).thenReturn(bankRouting);
		when(userDetails.isSeller()).thenReturn(Boolean.TRUE);
		validator.validate(dto, errors);
		verify(errors).rejectValue("routingNumber", "routingNumber.invalidCountry");
	}

	@Test
	public void validate_WhenTypeIsACHAndBankCountryMatchesUsersTaxCountry_AcceptsTheValue() throws Exception {
		BankRouting bankRouting = mock(BankRouting.class);
		when(bankRouting.getCountry()).thenReturn(Country.USA_COUNTRY);

		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getRoutingNumber()).thenReturn("987654321");
		when(dto.getCountry()).thenReturn(Country.USA);
		when(dataService.getBankRouting("987654321", Country.USA)).thenReturn(bankRouting);

		validator.validate(dto, errors);
		verify(errors, never()).rejectValue("routingNumber.invalidCountry", "Invalid country");
	}

	@Test
	public void validate_WhenTypeIsACHAndNoMatchingBankDetails_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankAccountTypeCode()).thenReturn(BankAccountType.CHECKING);
		when(dataService.getBankRouting(anyString(), eq(Country.USA))).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue("routingNumber", "banking.routingNumber.noBankFound.usa");
	}

	@Test
	public void validate_WhenCountryIsCanadaAndTypeIsACHAndTransitNumberIsNull_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getCountry()).thenReturn(Country.CANADA);
		when(dto.getBranchNumber()).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue("branchNumber", "banking.branchNumber.invalid", CollectionUtilities.newArray("Transit Branch Number"), "Invalid Branch Number");
	}

	@Test
	public void validate_WhenCountryIsCanadaAndTypeIsACHAndInstitutionNumberIsNull_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getCountry()).thenReturn(Country.CANADA);
		when(dto.getInstitutionNumber()).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue("institutionNumber", "banking.institutionNumber.invalid", CollectionUtilities.newArray("Financial Institution Number"), "Invalid Financial Institution Number");
	}

	@Test
	public void validate_WhenCountryIsCanadaAndTypeIsACHAndNoMatchingBankDetails_RejectsTheValue() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankAccountTypeCode()).thenReturn(BankAccountType.CHECKING);
		when(dto.getCountry()).thenReturn(Country.CANADA);
		when(dataService.getBankRouting(anyString(), eq(Country.CANADA))).thenReturn(null);
		validator.validate(dto, errors);
		verify(errors).rejectValue("branchNumber", "banking.routingNumber.noBankFound.can");
	}

	@Test
	public void validate_WhenCountryIsCanadaAndTypeIsACHAndNullCountryInBankDetails_RejectsTheValue() throws Exception {
		BankRouting mockBank = mock(BankRouting.class);
		when(mockBank.getCountry()).thenReturn(null);
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankAccountTypeCode()).thenReturn(BankAccountType.CHECKING);
		when(dto.getCountry()).thenReturn(Country.CANADA);
		when(dto.getBankName()).thenReturn(BANK_NAME);
		when(dataService.getBankRouting(anyString(), eq(Country.CANADA))).thenReturn(mockBank);
		when(userDetails.isSeller()).thenReturn(Boolean.TRUE);
		validator.validate(dto, errors);
		verify(errors).rejectValue("branchNumber", "routingNumber.invalidCountry");
	}

	@Test
	public void validate_WhenCountryIsCanadaAndTypeIsACHAndCountryInBankDetailsIsUSA_RejectsTheValue() throws Exception {
		BankRouting mockBank = mock(BankRouting.class);
		when(mockBank.getCountry()).thenReturn(Country.USA_COUNTRY);
		when(dto.getType()).thenReturn(AbstractBankAccount.ACH);
		when(dto.getBankAccountTypeCode()).thenReturn(BankAccountType.CHECKING);
		when(dto.getCountry()).thenReturn(Country.CANADA);
		when(userDetails.getTaxCountry()).thenReturn(Country.CANADA_COUNTRY);
		when(dataService.getBankRouting(anyString(), eq(Country.CANADA))).thenReturn(mockBank);
		when(userDetails.isSeller()).thenReturn(Boolean.TRUE);
		validator.validate(dto, errors);
		verify(errors).rejectValue("branchNumber", "routingNumber.invalidCountry");
	}

	@Test
	public void validate_WhenPayPalAccountHasInvalidEmail() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.PAYPAL);
		when(dto.getEmailAddress()).thenReturn("foobar");
		when(dto.getCountryCode()).thenReturn(Country.CANADA);

		validator.validate(dto, errors);
		verify(errors).rejectValue("emailAddress", "banking.paypal.email.invalid");
	}

	@Test
	public void validate_WhenPayPalAccountHasInvalidCountryCode() throws Exception {
		when(dto.getType()).thenReturn(AbstractBankAccount.PAYPAL);
		when(dto.getEmailAddress()).thenReturn("foo@bar.com");
		when(dto.getCountryCode()).thenReturn("FOO");

		validator.validate(dto, errors);
		verify(errors).rejectValue("countryCode", "banking.paypal.country.invalid");
	}

	protected Validator getValidator() {
		return validator;
	}
}
