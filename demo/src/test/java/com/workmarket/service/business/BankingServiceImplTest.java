package com.workmarket.service.business;


import com.google.common.collect.Lists;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.GlobalCashCardTransactionResponse;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.model.banking.GlobalCashCardAccount;
import com.workmarket.domains.model.banking.PayPalAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BankingServiceImpl;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.infra.payment.GCCPaymentAdapterImpl;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.services.VaultHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankingServiceImplTest {

	private static final String BRANCH_NUMBER = "12345";
	private static final String INSTITUTION_NUMBER = "056";
	private static final String ROUTING_NUMBER = "0" + INSTITUTION_NUMBER + BRANCH_NUMBER;
	private static final String ACCOUNT_NUMBER = "9671111";
	private static final Long USA_USER_ID = new Long(1l);
	private static final Long CAN_USER_ID = new Long(2l);
	private static final String EMAIL = "user@email.com";
	private static final String VAULT_KEY = "accountNumber";
	private static final String VAULT_VALUE = "blah";

	@Mock UserDAO userDAO;
	@Mock BankAccountDAO bankAccountDAO;
	@Mock GCCPaymentAdapterImpl globalCashCardService;
	@Mock EmailTemplateFactory emailTemplateFactory;
	@Mock NotificationService notificationService;
	@Mock TaxService taxService;
	@Mock InvariantDataService invariantDataService;
	@Mock AccountRegisterService accountRegisterServicePrefundImpl;
	@Mock ExtendedUserDetailsOptionsService extendedUserDetailsOptionsService;
	@Mock VaultHelper vaultHelper;

	@InjectMocks BankingServiceImpl bankingService;

	GlobalCashCardTransactionResponse gccSuccessResponse;
	GlobalCashCardTransactionResponse gccFailedResponse;
	BankAccountDTO bankAccountDTO;
	BankAccountDTO bankAccountCheckingDTO;
	BankAccount bankAccount;
	List<AbstractBankAccount> accounts;

	BankRouting bankRouting;
	User user;
	VaultKeyValuePair vaultKeyValuePair;
	ExtendedUserDetails extendedUserDetails;

	@Before
	public void setup() throws Exception {
		gccSuccessResponse = new GlobalCashCardTransactionResponse();
		gccSuccessResponse.setStatus("success");

		gccFailedResponse = new GlobalCashCardTransactionResponse();
		gccFailedResponse.setStatus("failure");

		doReturn(null).when(emailTemplateFactory).buildGlobalCashCardCreatedTemplate(anyLong());
		doNothing().when(notificationService).sendNotification(any(EmailTemplate.class));

		bankAccountDTO = new BankAccountDTO();
		bankAccountDTO.setType(BankAccount.GCC);

		user = mock(User.class);
		when(userDAO.get(anyLong())).thenReturn(user);
		when(user.getEmail()).thenReturn(EMAIL);

		bankAccount = mock(BankAccount.class);
		when(bankAccount.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);

		accounts = Lists.newArrayList();
		accounts.add(bankAccount);

		vaultKeyValuePair = new VaultKeyValuePair(VAULT_KEY, VAULT_VALUE);
		when(vaultHelper.get(any(AbstractBankAccount.class), anyString(), anyString())).thenReturn(vaultKeyValuePair);

		// used by our country-related tests
		bankRouting = mock(BankRouting.class);

		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		doReturn(usaTaxEntity).when(taxService).findActiveTaxEntity(USA_USER_ID);
		CanadaTaxEntity canTaxEntity = new CanadaTaxEntity();
		doReturn(canTaxEntity).when(taxService).findActiveTaxEntity(CAN_USER_ID);

		bankAccountCheckingDTO = new BankAccountDTO();
		bankAccountCheckingDTO.setRoutingNumber(ROUTING_NUMBER);
		bankAccountCheckingDTO.setType(BankAccount.ACH);

		extendedUserDetails = mock(ExtendedUserDetails.class);
		when(extendedUserDetailsOptionsService.loadUserByEmail(EMAIL, ExtendedUserDetailsOptionsService.ALL_OPTIONS)).thenReturn(extendedUserDetails);

	}


	@Test
	public void testSuccessSaveOfGCCAccount() throws Exception {

		doReturn(gccSuccessResponse).when(globalCashCardService).addSignatureCard(anyString(), any(BankAccountDTO.class));
		try {
			bankingService.saveBankAccount(System.currentTimeMillis(), bankAccountDTO);
		} catch (Exception ex) {
			fail(String.format("exception: %s", ExceptionUtils.getRootCause(ex)));
		}
		verify(bankAccountDAO, times(1)).saveOrUpdate(any(GlobalCashCardAccount.class));

	}

	@Test
	public void testSuccessSaveOfACHAccountUSASeller() throws Exception {

		try {
			when(extendedUserDetails.isSeller()).thenReturn(Boolean.TRUE);
			Country country = Country.USA_COUNTRY;
			bankAccountCheckingDTO.setCountry(country.getId());
			when(bankRouting.getCountry()).thenReturn(country);

			when(invariantDataService.getBankRouting(ROUTING_NUMBER, country.getId())).thenReturn(bankRouting);

			bankingService.saveBankAccount(USA_USER_ID, bankAccountCheckingDTO);
		} catch (Exception ex) {
			fail(String.format("exception: %s", ExceptionUtils.getRootCause(ex)));
		}
		verify(bankAccountDAO, times(1)).saveOrUpdate(any(BankAccount.class));
		verify(accountRegisterServicePrefundImpl, times(1)).createACHVerificationTransactions(anyLong(), any(BankAccount.class));

	}

	@Test
	public void testSuccessSaveOfACHAccountBuyer() throws Exception {

		try {
			when(extendedUserDetails.isSeller()).thenReturn(Boolean.FALSE);
			Country country = Country.USA_COUNTRY;
			bankAccountCheckingDTO.setCountry(country.getId());
			when(bankRouting.getCountry()).thenReturn(country);

			when(invariantDataService.getBankRouting(ROUTING_NUMBER, country.getId())).thenReturn(bankRouting);

			bankingService.saveBankAccount(USA_USER_ID, bankAccountCheckingDTO);
		} catch (Exception ex) {
			fail(String.format("exception: %s", ExceptionUtils.getRootCause(ex)));
		}
		verify(bankAccountDAO, times(1)).saveOrUpdate(any(BankAccount.class));
		verify(accountRegisterServicePrefundImpl, times(1)).createACHVerificationTransactions(anyLong(), any(BankAccount.class));

	}

	@Test
	public void testSuccessSaveOfACHAccountCanadaSeller() throws Exception {

		try {
			when(extendedUserDetails.isSeller()).thenReturn(Boolean.TRUE);

			Country country = Country.CANADA_COUNTRY;
			bankAccountCheckingDTO.setCountry(country.getId());
			bankAccountCheckingDTO.setInstitutionNumber(INSTITUTION_NUMBER);
			bankAccountCheckingDTO.setBranchNumber(BRANCH_NUMBER);
			bankAccountCheckingDTO.setInstitutionNumber(INSTITUTION_NUMBER);
			when(bankRouting.getCountry()).thenReturn(country);

			when(invariantDataService.getBankRouting(ROUTING_NUMBER, country.getId())).thenReturn(bankRouting);

			bankingService.saveBankAccount(CAN_USER_ID, bankAccountCheckingDTO);
		} catch (Exception ex) {
			fail(String.format("exception: %s", ExceptionUtils.getRootCause(ex)));
		}
		verify(bankAccountDAO, times(1)).saveOrUpdate(any(BankAccount.class));
		verifyZeroInteractions(accountRegisterServicePrefundImpl);

	}

	@Test(expected = RuntimeException.class)
	public void testFailureSaveOfGCCAccount() throws Exception {
		doReturn(gccFailedResponse).when(globalCashCardService).addSignatureCard(anyString(), any(BankAccountDTO.class));
		bankingService.saveBankAccount(System.currentTimeMillis(), bankAccountDTO);
		verify(bankAccountDAO, times(0)).saveOrUpdate(any(GlobalCashCardAccount.class));
	}

	@Test
	public void testBankLastFourDigit() throws Exception {
		assertNotNull(StringUtilities.getBankAccountLastFourDigits("123456"));
		assertEquals(StringUtilities.getBankAccountLastFourDigits("123456"), "3456");
		assertNotNull(StringUtilities.getBankAccountLastFourDigits("123"));
		assertEquals(StringUtilities.getBankAccountLastFourDigits("123"), "3");
		assertNotNull(StringUtilities.getBankAccountLastFourDigits("3"));
		assertEquals(StringUtilities.getBankAccountLastFourDigits("3"), "");
		assertNotNull(StringUtilities.getBankAccountLastFourDigits(""));
		assertEquals(StringUtilities.getBankAccountLastFourDigits(""), "");
	}

	@Test(expected = RuntimeException.class)
	public void testUserAccountBankRoutingCountryMismatchSeller () throws Exception {
		when(extendedUserDetails.isSeller()).thenReturn(Boolean.TRUE);

		Country country = Country.CANADA_COUNTRY;
		bankAccountCheckingDTO.setCountry(country.getId());
		bankAccountCheckingDTO.setBranchNumber(BRANCH_NUMBER);
		bankAccountCheckingDTO.setInstitutionNumber(INSTITUTION_NUMBER);
		when(bankRouting.getCountry()).thenReturn(country);

		when(invariantDataService.getBankRouting(ROUTING_NUMBER, country.getId())).thenReturn(bankRouting);

		bankingService.saveBankAccount(USA_USER_ID, bankAccountCheckingDTO);
	}

	@Test(expected = RuntimeException.class)
	public void testUserAccountBankRoutingTryCanadianAccountBuyer() throws Exception {
		when(extendedUserDetails.isSeller()).thenReturn(Boolean.FALSE);

		Country country = Country.CANADA_COUNTRY;
		bankAccountCheckingDTO.setCountry(country.getId());
		bankAccountCheckingDTO.setBranchNumber(BRANCH_NUMBER);
		bankAccountCheckingDTO.setInstitutionNumber(INSTITUTION_NUMBER);
		when(bankRouting.getCountry()).thenReturn(country);

		when(invariantDataService.getBankRouting(ROUTING_NUMBER, country.getId())).thenReturn(bankRouting);

		bankingService.saveBankAccount(USA_USER_ID, bankAccountCheckingDTO);
	}

	@Test
	public void testUserAccountBankRoutingCountryMatchSeller () throws Exception {
		when(extendedUserDetails.isSeller()).thenReturn(Boolean.TRUE);

		Country country = Country.USA_COUNTRY;
		bankAccountCheckingDTO.setCountry(country.getId());
		when(bankRouting.getCountry()).thenReturn(country);

		when(invariantDataService.getBankRouting(ROUTING_NUMBER, country.getId())).thenReturn(bankRouting);

		bankingService.saveBankAccount(USA_USER_ID, bankAccountCheckingDTO);

		verify(bankAccountDAO, times(1)).saveOrUpdate(any(BankAccount.class));
		verify(accountRegisterServicePrefundImpl, times(1)).createACHVerificationTransactions(anyLong(), any(BankAccount.class));
	}

	@Test
	public void testGetUnobfuscatedAccountNumbersWithNullAccountsList () throws Exception {
		accounts = null;

		final List<String> result = bankingService.getUnobfuscatedAccountNumbers(accounts);

		assertTrue(CollectionUtils.isEmpty(result));
		verify(vaultHelper, never()).get(any(BankAccount.class), anyString(), anyString());
	}

	@Test
	public void testGetUnobfuscatedAccountNumbersWithEmptyAccountsList () throws Exception {
		accounts = Lists.newArrayList();

		final List<String> result = bankingService.getUnobfuscatedAccountNumbers(accounts);

		assertTrue(CollectionUtils.isEmpty(result));
		verify(vaultHelper, never()).get(any(BankAccount.class), anyString(), anyString());
	}

	@Test
	public void testGetUnobfuscatedAccountNumbersWithNonBankAccountAccountsList () throws Exception {
		accounts = Lists.newArrayList();
		PayPalAccount payPalAccount = new PayPalAccount();
		accounts.add(payPalAccount);

		final List<String> result = bankingService.getUnobfuscatedAccountNumbers(accounts);

		assertTrue(CollectionUtils.isEmpty(result));
		verify(vaultHelper, never()).get(any(BankAccount.class), anyString(), anyString());
	}

	@Test
	public void testGetUnobfuscatedAccountNumbersWithOneAccountAndVaultValueFound() throws Exception {
		final List<String> result = bankingService.getUnobfuscatedAccountNumbers(accounts);

		assertTrue(CollectionUtils.isNotEmpty(result));
		assertEquals(accounts.size(), result.size());
		assertEquals(VAULT_VALUE, result.get(0));
		verify(vaultHelper, times(1)).get(any(BankAccount.class), anyString(), anyString());
	}

	@Test
	public void testGetUnobfuscatedAccountNumbersWithOneAccountAndVaultValueNotFound() throws Exception {
		when(vaultHelper.get(any(AbstractBankAccount.class), anyString(), anyString())).thenReturn(new VaultKeyValuePair());

		final List<String> result = bankingService.getUnobfuscatedAccountNumbers(accounts);

		assertTrue(CollectionUtils.isNotEmpty(result));
		assertEquals(accounts.size(), result.size());
		assertEquals(ACCOUNT_NUMBER, result.get(0));
		verify(vaultHelper, times(1)).get(any(BankAccount.class), anyString(), anyString());
	}

	@Test
	public void testDeactivateAccountWithNoAccountFound() throws Exception {
		final long bankAccountId = 13L;
		when(bankAccountDAO.get(anyLong())).thenReturn(null);
		AbstractBankAccount account = null;
		try {
			account = bankingService.deactivateBankAccount(bankAccountId, 12L);
			fail("Expected Runtime Exception");
		} catch (EntityNotFoundException ex) {
			assertEquals(ex.getMessage(), String.format("account: %d was not found", bankAccountId));
		}
		assertNull(account);
	}

	@Test
	public void testDeactivateAccountWithValidAccount() throws Exception {
		final long bankAccountId = 13L;
		final long companyId = 14L;
		final PayPalAccount bankAccount = new PayPalAccount();
		final Company company = new Company();
		company.setId(companyId);
		bankAccount.setCompany(company);
		assertTrue(bankAccount.getActiveFlag());

		when(bankAccountDAO.get(anyLong())).thenReturn(bankAccount);
		AbstractBankAccount account = bankingService.deactivateBankAccount(bankAccountId, companyId);
		assertFalse(account.getActiveFlag());
	}

	@Test
	public void testDeactivateAccountWithInvalidCompanyId() throws Exception {
		final long bankAccountId = 13L;
		final long companyId = 14L;
		final long invalidCompanyId = 15L;
		final PayPalAccount bankAccount = new PayPalAccount();
		final Company company = new Company();
		company.setId(companyId);
		bankAccount.setCompany(company);
		assertTrue(bankAccount.getActiveFlag());

		when(bankAccountDAO.get(anyLong())).thenReturn(bankAccount);
		try {
			AbstractBankAccount account = bankingService.deactivateBankAccount(bankAccountId, invalidCompanyId);
			fail("Expected IllegalArgumentException for company ID not being equal");
		} catch (IllegalArgumentException ex) {

		} catch (Exception e) {
			fail("Expected IllegalArgumentException for company ID not being equal");
		} finally {
			assertTrue(bankAccount.getActiveFlag());
		}
	}
}
