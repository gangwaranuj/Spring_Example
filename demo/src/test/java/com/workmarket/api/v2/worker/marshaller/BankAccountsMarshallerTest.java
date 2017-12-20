package com.workmarket.api.v2.worker.marshaller;

import com.workmarket.api.v2.model.ApiBankAccountDTO;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.banking.GlobalCashCardAccount;
import com.workmarket.domains.model.banking.PayPalAccount;
import com.workmarket.domains.model.postalcode.Country;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BankAccountsMarshallerTest {

	private BankAccountsMarshaller bankAccountsMarshaller;

	@Before
	public void setup() {
		bankAccountsMarshaller = new BankAccountsMarshaller();
	}

	@Test
	public void marshallAccount_dataMarshalled() {
		BankAccount account = new BankAccount();
		account.setId(10L);
		account.setConfirmedFlag(true);
		account.setBankName("First Union Bank");
		account.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));
		account.setNameOnAccount("Gomer Pyle");
		account.setAccountNumber("56AIDK290");
		Country country = new Country();
		country.setId("DM");
		country.setName("Denmark");
		account.setCountry(country);
		account.setConfirmedFlag(true);

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		bankAccountsMarshaller.marshallBankServiceAccountResponse(account, response);

		assertEquals(1, response.getPayload().size());
		ApiBankAccountDTO payload = (ApiBankAccountDTO) response.getPayload().get(0);

		assertEquals(new Long(10), payload.getId());
		assertEquals("Gomer Pyle", payload.getAccountHolder());
		assertEquals("First Union Bank (K290)", payload.getName());
		assertEquals(ApiBankAccountDTO.Type.ACH, payload.getType());
		assertEquals("DM", payload.getCountry());
		assertTrue((Boolean) payload.getVerified());
	}


	@Test
	public void marshallAccounts_dataMarshalled() {
		List<AbstractBankAccount> accounts = new ArrayList<AbstractBankAccount>();

		BankAccount account = new BankAccount();
		account.setId(10L);
		account.setConfirmedFlag(true);
		account.setBankName("First Union Bank");
		account.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));
		account.setNameOnAccount("Gomer Pyle");
		account.setAccountNumber("56AIDK290");
		Country country = new Country();
		country.setId("DM");
		country.setName("Denmark");
		account.setCountry(country);
		accounts.add(account);

		GlobalCashCardAccount ccAccount = new GlobalCashCardAccount();
		ccAccount.setId(20L);
		ccAccount.setConfirmedFlag(false);
		ccAccount.setBankName("Second Union Bank");
		ccAccount.setBankAccountType(new BankAccountType(BankAccountType.GLOBAL_CASH_CARD));
		ccAccount.setAccountNumber("901029");
		country = new Country();
		country.setId("USA");
		country.setName("United States");
		ccAccount.setCountry(country);
		accounts.add(ccAccount);

		PayPalAccount ppAccount = new PayPalAccount();
		ppAccount.setId(30L);
		ppAccount.setConfirmedFlag(true);
		ppAccount.setBankName("PayPal Inc.");
		ppAccount.setBankAccountType(new BankAccountType(BankAccountType.PAY_PAL));
		ppAccount.setEmailAddress("gomer_pyle@gmail.com");
		country = new Country();
		country.setId("CAN");
		country.setName("Canada");
		ppAccount.setCountry(country);
		accounts.add(ppAccount);

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		bankAccountsMarshaller.marshallBankServiceAccountListResponse(accounts, response);

		assertEquals(3, response.getPayload().size());
		ApiBankAccountDTO payload = (ApiBankAccountDTO) response.getPayload().get(0);

		assertEquals(new Long(10), payload.getId());
		assertEquals("First Union Bank (K290)", payload.getName());
		assertEquals(ApiBankAccountDTO.Type.ACH, payload.getType());
		assertEquals("DM", payload.getCountry());
		assertTrue(payload.getVerified());

		payload = (ApiBankAccountDTO) response.getPayload().get(1);

		assertEquals(new Long(20), payload.getId());
		assertEquals("WM Card", payload.getName());
		assertEquals(ApiBankAccountDTO.Type.GCC, payload.getType());
		assertEquals("USA", payload.getCountry());
		assertFalse(payload.getVerified());

		payload = (ApiBankAccountDTO) response.getPayload().get(2);

		assertEquals(new Long(30), payload.getId());
		assertEquals("gomer_pyle@gmail.com", payload.getName());
		assertEquals(ApiBankAccountDTO.Type.PPA, payload.getType());
		assertEquals("CAN", payload.getCountry());
		assertTrue(payload.getVerified());
	}
}
