package com.workmarket.vault.aop;

import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.dao.tax.TaxEntityDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.ForeignTaxEntity;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.exceptions.VaultDuplicateTaxNumberException;
import com.workmarket.vault.models.Secured;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.services.VaultServerService;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@Transactional
public class VaultableAspectIT extends BaseServiceIT {
	private static final String US_TAX_NUMBER_FMT = "UsaTaxEntity:%s:taxNumber";
	private static final String CAN_TAX_NUMBER_FMT = "CanadaTaxEntity:%s:taxNumber";
	private static final String FOR_TAX_NUMBER_FMT = "ForeignTaxEntity:%s:taxNumber";
	private static final String BANK_ACCOUNT_NUMBER_FMT = "BankAccount:%s:accountNumber";
	private static String TAX_NUMBER;
	private static final String ACCOUNT_NUMBER = "678910";
	private static final Long BANK_ID = 1L;
	private static final Long ID = 2L;
	private static final String ACCOUNT_NUMBER_KEY = String.format("%s:%s:%s", "BankAccount", BANK_ID, "accountNumber");
	private static final String CANADIAN_TAX_KEY = String.format("%s:%s:%s", "CanadaTaxEntity", ID, "taxNumber");
	private static final String FOREIGN_TAX_KEY = String.format("%s:%s:%s", "ForeignTaxEntity", ID, "taxNumber");
	private static final String USA_TAX_KEY = String.format("%s:%s:%s", "UsaTaxEntity", ID, "taxNumber");

	@Autowired BankAccountDAO bankAccountDAO;
	@Autowired TaxEntityDAO taxEntityDAO;
	@Autowired VaultServerService vaultServerService;
	@Autowired VaultHelper vaultHelper;

	Company company;

	@Before
	public void setup() {
		TAX_NUMBER = UUID.randomUUID().toString().substring(0, 24);
		company = newCompany();

		featureEvaluatorConfiguration.remove("vaultWrite");
		featureEvaluatorConfiguration.remove("vaultRead");
		featureEvaluatorConfiguration.remove("vaultObfuscate");
		featureEvaluatorConfiguration.remove("vaultObfuscatePrepend");
		featureEvaluatorConfiguration.put("vaultWrite", null, "true");
		featureEvaluatorConfiguration.put("vaultRead", null, "true");
		featureEvaluatorConfiguration.put("vaultObfuscate", null, "true");
	}

	@Test
	public void shouldSaveVaultedBankAccountProperties() throws Exception {
		vaultServerService.remove(ACCOUNT_NUMBER_KEY);
		BankAccount bankAccount = createBankAccount();
		assertEquals(ACCOUNT_NUMBER,
				vaultServerService.get(String.format(BANK_ACCOUNT_NUMBER_FMT, bankAccount.getId())).getValue());
	}

	@Test
	public void shouldSaveVaultedCanadianTaxEntityPropertiese() throws Exception {
		vaultServerService.remove(CANADIAN_TAX_KEY);
		CanadaTaxEntity taxEntity = createCanadianTaxEntity(null);
		assertEquals(TAX_NUMBER, vaultServerService.get(String.format(CAN_TAX_NUMBER_FMT, taxEntity.getId())).getValue());

		VaultKeyValuePair pair = vaultHelper.buildDuplicateTaxNumberCheckPair(taxEntity, TAX_NUMBER);
		assertEquals(pair.getValue(), vaultServerService.get(pair.getId()).getValue());
	}

	@Test(expected = VaultDuplicateTaxNumberException.class)
	public void shouldNotAllowDuplicateCanadianTaxNumberInSameCountryForDifferentCompany() throws Exception {
		vaultServerService.remove(CANADIAN_TAX_KEY);
		Company differentCountry = newCompany();
		createCanadianTaxEntity(null, true);
		createCanadianTaxEntity(null, true, differentCountry);
	}

	@Test
	public void shouldAllowSameCanadianTaxNumberInSameCountryForSameCompany() throws Exception {
		vaultServerService.remove(CANADIAN_TAX_KEY);
		createCanadianTaxEntity(null);
		createCanadianTaxEntity(new Random().nextLong());
	}

	@Test
	public void shouldSaveVaultedForeignTaxEntityProperties() throws Exception {
		vaultServerService.remove(FOREIGN_TAX_KEY);
		ForeignTaxEntity taxEntity = createForeignTaxEntity(null);
		assertEquals(TAX_NUMBER, vaultServerService.get(String.format(FOR_TAX_NUMBER_FMT, taxEntity.getId())).getValue());

		VaultKeyValuePair pair = vaultHelper.buildDuplicateTaxNumberCheckPair(taxEntity, TAX_NUMBER);
		assertEquals(pair.getValue(), vaultServerService.get(pair.getId()).getValue());
	}

	@Test(expected = VaultDuplicateTaxNumberException.class)
	public void shouldNotAllowDuplicateForiegnTaxNumberInSameCountryForDifferentCompany() throws Exception {
		vaultServerService.remove(FOREIGN_TAX_KEY);
		Company differentCountry = newCompany();
		createForeignTaxEntity(null, true);
		createForeignTaxEntity(null, true, differentCountry);
	}

	@Test
	public void shouldAllowSameForeignTaxNumberInSameCountryForSameCompany() throws Exception {
		vaultServerService.remove(FOREIGN_TAX_KEY);
		createForeignTaxEntity(null);
		createForeignTaxEntity(new Random().nextLong());
	}

	@Test
	public void shouldSaveVaultedUsaTaxEntityProperties() throws Exception {
		vaultServerService.remove(USA_TAX_KEY);
		UsaTaxEntity taxEntity = createUsaTaxEntity(null);
		assertEquals(TAX_NUMBER, vaultServerService.get(String.format(US_TAX_NUMBER_FMT, taxEntity.getId())).getValue());

		VaultKeyValuePair pair = vaultHelper.buildDuplicateTaxNumberCheckPair(taxEntity, TAX_NUMBER);
		assertEquals(pair.getValue(), vaultServerService.get(pair.getId()).getValue());
	}

	@Test(expected = VaultDuplicateTaxNumberException.class)
	public void shouldNotAllowDuplicateUsaTaxNumberInSameCountryForDifferentCompany() throws Exception {
		vaultServerService.remove(USA_TAX_KEY);
		Company differentCountry = newCompany();
		createUsaTaxEntity(null, true);
		createUsaTaxEntity(null, true, differentCountry);
	}

	@Test
	public void shouldAllowSameUsaTaxNumberInSameCountryForSameCompany() throws Exception {
		vaultServerService.remove(USA_TAX_KEY);
		createUsaTaxEntity(null);
		createUsaTaxEntity(new Random().nextLong());
	}

	@Test
	public void shouldCreateDuplicateEntryIfTaxEntityIsActive() throws Exception {
		UsaTaxEntity taxEntity = createUsaTaxEntity(null, true);
		assertFalse(vaultHelper.getDuplicateKeyValueEntry(taxEntity.getIsoCountry().getISO3(), TAX_NUMBER).isEmpty());
	}

	@Test
	public void shouldNotRemoveDuplicateEntryIfTaxEntityIsActiveThenChangedToInactive() throws Exception {
		UsaTaxEntity taxEntity = createUsaTaxEntity(null, true);
		taxEntity.setActiveFlag(false);
		taxEntity.setTaxNumber(TAX_NUMBER);
		taxEntityDAO.saveOrUpdate(taxEntity);
		assertFalse(vaultHelper.getDuplicateKeyValueEntry(taxEntity.getIsoCountry().getISO3(), TAX_NUMBER).isEmpty());
	}

	@Test
	public void shouldCreateDuplicateEntryIfTaxEntityIsInactive() throws Exception {
		UsaTaxEntity taxEntity = createUsaTaxEntity(null, false);
		assertFalse(vaultHelper.getDuplicateKeyValueEntry(taxEntity.getIsoCountry().getISO3(), TAX_NUMBER).isEmpty());
	}

	@Test
	public void shouldNotUpdateTaxNumberInVaultOnTaxEntityUpdate() throws ServiceUnavailableException {
		// create entity in db
		final UsaTaxEntity taxEntity = createUsaTaxEntity(null, true);

		final String valueFromVault =
				vaultServerService.get(String.format(US_TAX_NUMBER_FMT, taxEntity.getId())).getValue();
		// check vault has unobfuscated value after the create
		assertEquals(TAX_NUMBER, valueFromVault);

		final String obfuscatedValue = StringUtilities.showLastNCharacters(TAX_NUMBER, Secured.MASKING_PATTERN, 0);
		taxEntity.setTaxNumber(obfuscatedValue);
		// update with obfuscated value
		taxEntityDAO.saveOrUpdate(taxEntity);

		final UsaTaxEntity taxEntityFromDb = (UsaTaxEntity) taxEntityDAO.get(taxEntity.getId());
		final String valueFromVaultAfterUpdate =
				vaultServerService.get(String.format(US_TAX_NUMBER_FMT, taxEntity.getId())).getValue();
		// check vault is unchanged after an update
		assertEquals(TAX_NUMBER, valueFromVaultAfterUpdate);
		// verify db is obfuscated
		assertEquals(obfuscatedValue, taxEntityFromDb.getTaxNumber());
	}

	@Test
	public void shouldNotUpdateAccountNumberInVaultOnBankAccountUpdate() throws ServiceUnavailableException {
		final BankAccount bankAccount = createBankAccount();

		final String valueFromVault =
				vaultServerService.get(String.format(BANK_ACCOUNT_NUMBER_FMT, bankAccount.getId())).getValue();
		// value from vault should be unobfuscated
		assertEquals(ACCOUNT_NUMBER, valueFromVault);

		final String obfuscatedValue = StringUtilities.showLastNCharacters(ACCOUNT_NUMBER, Secured.MASKING_PATTERN, 0);
		bankAccount.setAccountNumber(obfuscatedValue);
		// update with obfuscated value
		bankAccountDAO.saveOrUpdate(bankAccount);

		final BankAccount bankAccountFromDb = (BankAccount) bankAccountDAO.get(bankAccount.getId());
		final String valueFromVaultAfterUpdate =
				vaultServerService.get(String.format(BANK_ACCOUNT_NUMBER_FMT, bankAccountFromDb.getId())).getValue();
		// check vault is unchanged after an update
		assertEquals(ACCOUNT_NUMBER, valueFromVaultAfterUpdate);
		// verify db is obfuscated
		assertEquals(obfuscatedValue, bankAccountFromDb.getAccountNumber());
	}

	private BankAccount createBankAccount() {
		final BankAccount bankAccount = new BankAccount();
		bankAccount.setAccountNumber(ACCOUNT_NUMBER);
		bankAccount.setActiveFlag(true);
		bankAccount.setBankName("Acme Inc.");
		bankAccount.setNameOnAccount("Acme Inc.");
		bankAccount.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));
		bankAccount.setCompany(company);
		bankAccount.setConfirmedFlag(true);
		bankAccount.setCreatedOn(Calendar.getInstance());
		bankAccount.setCreatorId(1L);
		bankAccount.setModifierId(1L);
		bankAccount.setCountry(Country.USA_COUNTRY);
		bankAccountDAO.saveOrUpdate(bankAccount);
		return bankAccount;
	}

	private ForeignTaxEntity createForeignTaxEntity(Long id) {
		return createForeignTaxEntity(id, true);
	}
	private ForeignTaxEntity createForeignTaxEntity(Long id, Boolean active) {
		return createForeignTaxEntity(id, active, company);
	}

	private ForeignTaxEntity createForeignTaxEntity(Long id, Boolean active, Company company) {
		ForeignTaxEntity taxEntity = new ForeignTaxEntity();
		taxEntity.setId(id);
		taxEntity.setTaxNumber(TAX_NUMBER);
		taxEntity.setCompany(company);
		taxEntity.setActiveFlag(active);
		taxEntity.setAddress("303 east 13th street, new york, NY");
		taxEntity.setBusinessFlag(false);
		taxEntity.setCity("New York");
		taxEntity.setState("NY");
		taxEntity.setPostalCode("11111");
		taxEntity.setTaxEntityType(new TaxEntityType(TaxEntityType.INDIVIDUAL));
		taxEntity.setActiveDate(Calendar.getInstance());
		taxEntityDAO.saveOrUpdate(taxEntity);
		return taxEntity;
	}

	private UsaTaxEntity createUsaTaxEntity(Long id) {
		return createUsaTaxEntity(id, true);
	}
	private UsaTaxEntity createUsaTaxEntity(Long id, Boolean active) {
		return createUsaTaxEntity(id, active, company);
	}
	private UsaTaxEntity createUsaTaxEntity(Long id, Boolean active, Company company) {
		UsaTaxEntity taxEntity = new UsaTaxEntity();
		taxEntity.setId(id);
		taxEntity.setTaxNumber(TAX_NUMBER);
		taxEntity.setCompany(company);
		taxEntity.setActiveFlag(active);
		taxEntity.setAddress("303 east 13th street, new york, NY");
		taxEntity.setBusinessFlag(false);
		taxEntity.setCity("New York");
		taxEntity.setState("NY");
		taxEntity.setPostalCode("11111");
		taxEntity.setTaxEntityType(new TaxEntityType(TaxEntityType.INDIVIDUAL));
		taxEntity.setActiveDate(Calendar.getInstance());
		taxEntityDAO.saveOrUpdate(taxEntity);
		return taxEntity;
	}

	private CanadaTaxEntity createCanadianTaxEntity(Long id) {
		return createCanadianTaxEntity(id, true);
	}
	private CanadaTaxEntity createCanadianTaxEntity(Long id, Boolean active) {
		return createCanadianTaxEntity(id, active, company);
	}
	private CanadaTaxEntity createCanadianTaxEntity(Long id, Boolean active, Company company) {
		CanadaTaxEntity taxEntity = new CanadaTaxEntity();
		taxEntity.setId(id);
		taxEntity.setTaxNumber(TAX_NUMBER);
		taxEntity.setCompany(company);
		taxEntity.setActiveFlag(active);
		taxEntity.setAddress("303 east 13th street, new york, NY");
		taxEntity.setBusinessFlag(false);
		taxEntity.setCity("New York");
		taxEntity.setState("NY");
		taxEntity.setPostalCode("11111");
		taxEntity.setTaxEntityType(new TaxEntityType(TaxEntityType.INDIVIDUAL));
		taxEntity.setActiveDate(Calendar.getInstance());
		taxEntityDAO.saveOrUpdate(taxEntity);
		return taxEntity;
	}
}