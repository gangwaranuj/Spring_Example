package com.workmarket.vault.services;

import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.ForeignTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import com.workmarket.vault.models.VaultKeyValuePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class VaultServerServiceRedisImplIT extends BaseServiceIT {

	private static final Long BANK_ID = 1L;
	private static final Long ID = 2L;
	private static final String TAX_NUMBER = "23423423423";
	private static final String ACCOUNT_NUMBER = "123";
	private static final String ACCOUNT_NUMBER_KEY = String.format("%s:%s:%s", "BankAccount", BANK_ID, "accountNumber");
	private static final String CANADIAN_TAX_KEY = String.format("%s:%s:%s", "CanadaTaxEntity", ID, "taxNumber");
	private static final String FOREIGN_TAX_KEY = String.format("%s:%s:%s", "ForeignTaxEntity", ID, "taxNumber");
	private static final String USA_TAX_KEY = String.format("%s:%s:%s", "UsaTaxEntity", ID, "taxNumber");
	private static final String KEY = "key";
	private static final String VALUE = "value";

	@Autowired VaultHelper vaultHelper;
	@Autowired @Qualifier("vaultServerServiceRedisImpl") VaultServerService vaultService;

	BankAccount bankAccount;
	CanadaTaxEntity canadaTaxEntity;
	ForeignTaxEntity foreignTaxEntity;
	UsaTaxEntity usaTaxEntity;

	@Before
	public void setup() {
		bankAccount = new BankAccount();
		bankAccount.setId(BANK_ID);
		bankAccount.setAccountNumber(ACCOUNT_NUMBER);

		foreignTaxEntity = new ForeignTaxEntity();
		foreignTaxEntity.setId(ID);
		foreignTaxEntity.setTaxNumber(TAX_NUMBER);

		canadaTaxEntity = new CanadaTaxEntity();
		canadaTaxEntity.setId(ID);
		canadaTaxEntity.setTaxNumber(TAX_NUMBER);

		usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setId(ID);
		usaTaxEntity.setTaxNumber(TAX_NUMBER);
	}

	@Test
	public void shouldPostKey() throws Exception {
		vaultService.remove(KEY);
		vaultService.post(new VaultKeyValuePair(KEY, VALUE));
		assertFalse(vaultService.get(KEY).isEmpty());
	}

	@Test
	public void shouldRemoveKey() throws Exception {
		vaultService.post(new VaultKeyValuePair(KEY, VALUE));
		vaultService.remove(KEY);
		assertTrue(vaultService.get(KEY).isEmpty());
	}

	@Test
	public void shouldGetCorrectKey() throws Exception {
		vaultService.post(new VaultKeyValuePair(KEY, VALUE));
		assertEquals(KEY, vaultService.get(KEY).getId());
	}

	@Test
	public void shouldGetCorrectValue() throws Exception {
		vaultService.post(new VaultKeyValuePair(KEY, VALUE));
		assertEquals(VALUE, vaultService.get(KEY).getValue());
	}

	@Test
	public void shouldSaveAccountNumberVaultedProperty() throws Exception {
		vaultService.post(vaultHelper.getVaultedValues(bankAccount));
		assertEquals(ACCOUNT_NUMBER, vaultService.get(ACCOUNT_NUMBER_KEY).getValue());
	}

	@Test
	public void shouldSaveCanadianTaxNumberVaultedProperty() throws Exception {
		vaultService.post(vaultHelper.getVaultedValues(canadaTaxEntity));
		assertEquals(TAX_NUMBER, vaultService.get(CANADIAN_TAX_KEY).getValue());
	}

	@Test
	public void shouldSaveForeignTaxNumberVaultedProperty() throws Exception {
		vaultService.post(vaultHelper.getVaultedValues(foreignTaxEntity));
		assertEquals(TAX_NUMBER, vaultService.get(FOREIGN_TAX_KEY).getValue());
	}

	@Test
	public void shouldSaveUsaTaxNumberVaultedProperty() throws Exception {
		vaultService.post(vaultHelper.getVaultedValues(usaTaxEntity));
		assertEquals(TAX_NUMBER, vaultService.get(USA_TAX_KEY).getValue());
	}
}
