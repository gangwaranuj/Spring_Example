package com.workmarket.api.v2.worker.fulfillment;


import com.workmarket.api.v2.model.ApiBankAccountDTO;
import com.workmarket.api.v2.worker.marshaller.BankAccountsMarshaller;
import com.workmarket.api.v2.worker.service.BankAccountsService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.web.exceptions.HttpException403;
import com.workmarket.web.exceptions.HttpException404;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BankAccountsFulfillmentProcessorTest {

	private BankAccountsFulfillmentProcessor fulfillmentProcessor;
	@Mock private BankAccountsService bankAccountsService;
  private BankAccountsMarshaller bankAccountsMarshaller;
	private ExtendedUserDetails user;

	@Before
	public void setup() {
		fulfillmentProcessor = new BankAccountsFulfillmentProcessor();

		bankAccountsService = mock(BankAccountsService.class);
		fulfillmentProcessor.setBankAccountsService(bankAccountsService);

		bankAccountsMarshaller = new BankAccountsMarshaller();
		fulfillmentProcessor.setBankAccountsMarshaller(bankAccountsMarshaller);

		user = new ExtendedUserDetails("TESTID", "TESTPASSWORD", CollectionUtils.EMPTY_COLLECTION);
		user.setId(9954L);
		user.setCompanyId(5555L);
		user.setCountry(Country.USA);

		BankAccount account = new BankAccount();
		account.setId(10L);
		account.setConfirmedFlag(true);
		account.setBankName("First Union Bank");
		account.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));
		account.setNameOnAccount("Gomer Pyle");
		Company company = new Company();
		company.setId(user.getCompanyId());
		account.setCompany(company);
		account.setAccountNumber("123456789");

		when(bankAccountsService.getBankAccount(10L)).thenReturn(account);
	}


	@Test
	public void getBankAccounts_goodData_goodResponse() {
		List<AbstractBankAccount> serviceAccounts = new ArrayList<AbstractBankAccount>();
		BankAccount account = new BankAccount();
		account.setId(10L);
		account.setConfirmedFlag(true);
		account.setAccountNumber("123456789");
		serviceAccounts.add(account);

		account = new BankAccount();
		account.setId(20L);
		account.setConfirmedFlag(true);
		account.setAccountNumber("123456789");
		serviceAccounts.add(account);

		when(bankAccountsService.getBankAccountsForUser(user.getId())).thenReturn(serviceAccounts);

		FulfillmentPayloadDTO response = fulfillmentProcessor.getBankAccounts(user);
		verify(bankAccountsService, times(1)).getBankAccountsForUser(user.getId());

		assertEquals(2, response.getPayload().size());
		assertNull(response.getPagination());
	}

	@Test
	public void getBankAccount_GoodParam_GoodResponse() {

		FulfillmentPayloadDTO response = fulfillmentProcessor.getBankAccount(10L, user.getCompanyId());
		verify(bankAccountsService, times(1)).getBankAccount(10L);

		assertEquals(1, response.getPayload().size());
		assertNotNull(response.getPayload().get(0));

		ApiBankAccountDTO dto = (ApiBankAccountDTO) response.getPayload().get(0);

		assertEquals(new Long(10), dto.getId());
	}


	@Test
	public void getBankAccount_BadParam_ThrowsException() {
		try {
			fulfillmentProcessor.getBankAccount(0L, user.getCompanyId());
			fail("Expected an Illegal Argument Exception to be thrown.");
		} catch (IllegalArgumentException iae) {
			assertEquals("A Non-valid account id was passed.", iae.getMessage());
		} catch (Exception e) {
			fail("Expected an Illegal Argument Exception to be thrown. Exception was " + e);
		} finally {
			verify(bankAccountsService, never()).getBankAccount(10L);
		}
	}


	@Test
	public void getBankAccount_NotFound_ThrowsException () {
		when(bankAccountsService.getBankAccount(20L)).thenReturn(null);

		try {
			fulfillmentProcessor.getBankAccount(20L, user.getCompanyId());
			fail("Expected an HttpException404 to be thrown.");
		} catch (HttpException404 he404) {
			assertEquals("No bank account found with id : 20", he404.getMessage());
		} catch (Exception e) {
			fail("Expected an HttpException404 to be thrown. Exception was " + e);
		} finally {
			verify(bankAccountsService, times(1)).getBankAccount(20L);
		}
	}

	@Test
	public void getBankAccount_NotSameCompanyId () {
		BankAccount account = new BankAccount();
		account.setId(20L);
		account.setConfirmedFlag(true);
		account.setAccountNumber("123456789");
		Company company = new Company();
		company.setId(33L);
		account.setCompany(company);
		assertFalse(company.getId().equals(user.getCompanyId()));

		when(bankAccountsService.getBankAccount(20L)).thenReturn(account);

		try {
			fulfillmentProcessor.getBankAccount(20L, user.getCompanyId());
			fail("Expected an assert to be thrown.");
		} catch (HttpException403 e) {
			assertEquals("Invalid user access", e.getMessage());
		} finally {
			verify(bankAccountsService, times(1)).getBankAccount(20L);
		}
	}
}
