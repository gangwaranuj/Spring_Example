package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.PayPalAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisterTransactionFactoryTest {

	@InjectMocks RegisterTransactionFactoryImpl registerTransactionFactory;

	private AbstractBankAccount bankAccount;
	private AbstractBankAccount payPalAccount;

	@Before
	public void setUp() throws Exception {
		bankAccount = mock(BankAccount.class);
		payPalAccount = mock(PayPalAccount.class);
	}

	@Test
	public void newBankAccountRegisterTransactionType_withPayPal_success() throws Exception {
		RegisterTransactionType registerTransactionType = registerTransactionFactory.newBankAccountRegisterTransactionType(payPalAccount);
		assertNotNull(registerTransactionType);
		assertEquals(registerTransactionType.getCode(), RegisterTransactionType.PAYPAL_ACCOUNT_TRANSACTION);
	}

	@Test
	public void newBankAccountRegisterTransactionType_withRegularBankAccount_success() throws Exception {
		RegisterTransactionType registerTransactionType = registerTransactionFactory.newBankAccountRegisterTransactionType(bankAccount);
		assertNotNull(registerTransactionType);
		assertEquals(registerTransactionType.getCode(), RegisterTransactionType.BANK_ACCOUNT_TRANSACTION);
	}

	@Test
	public void newRemoveFundsRegisterTransactionType_withPayPal_success() throws Exception {
		RegisterTransactionType registerTransactionType = registerTransactionFactory.newRemoveFundsRegisterTransactionType(payPalAccount);
		assertNotNull(registerTransactionType);
		assertEquals(registerTransactionType.getCode(), RegisterTransactionType.REMOVE_FUNDS_PAYPAL);
	}

	@Test
	public void newRemoveFundsRegisterTransactionType_withRegularBankAccount_success() throws Exception {
		RegisterTransactionType registerTransactionType = registerTransactionFactory.newRemoveFundsRegisterTransactionType(bankAccount);
		assertNotNull(registerTransactionType);
		assertEquals(registerTransactionType.getCode(), RegisterTransactionType.REMOVE_FUNDS);
	}

	@Test
	public void newBackgroundCheckRegisterTransactionType_withUSA_success() throws Exception {
		RegisterTransactionType registerTransactionType = registerTransactionFactory.newBackgroundCheckRegisterTransactionType(Country.USA);
		assertNotNull(registerTransactionType);
		assertEquals(registerTransactionType.getCode(), RegisterTransactionType.BACKGROUND_CHECK);
	}

	@Test
	public void newBackgroundCheckRegisterTransactionType_withCanada_success() throws Exception {
		RegisterTransactionType registerTransactionType = registerTransactionFactory.newBackgroundCheckRegisterTransactionType(Country.CANADA);
		assertNotNull(registerTransactionType);
		assertEquals(registerTransactionType.getCode(), RegisterTransactionType.BACKGROUND_CHECK_INTERNATIONAL);
	}

	@Test
	public void newBackgroundCheckRegisterTransactionType_withOtherCountry_success() throws Exception {
		RegisterTransactionType registerTransactionType = registerTransactionFactory.newBackgroundCheckRegisterTransactionType("AU");
		assertNotNull(registerTransactionType);
		assertEquals(registerTransactionType.getCode(), RegisterTransactionType.BACKGROUND_CHECK_INTERNATIONAL);
	}
}
