package com.workmarket.vault.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.ForeignTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.models.Secured;
import com.workmarket.vault.models.VaultKeyValuePair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VaultHelperImplTest {
	private static final String TAX_NUMBER = "23423423423";
	private static final String ACCOUNT_NUMBER = "678910";
	private static final Long BANK_ID = 1L;
	private static final Long ID = 2L;
	private static final String ACCOUNT_NUMBER_KEY = String.format("%s:%s:%s", "BankAccount", BANK_ID, "accountNumber");
	private static final String CANADIAN_TAX_KEY = String.format("%s:%s:%s", "CanadaTaxEntity", ID, "taxNumber");
	private static final String FOREIGN_TAX_KEY = String.format("%s:%s:%s", "ForeignTaxEntity", ID, "taxNumber");
	private static final String USA_TAX_KEY = String.format("%s:%s:%s", "UsaTaxEntity", ID, "taxNumber");
	private static final Long COMPANY_ID = 1L;

	@Mock VaultServerService vaultServerService;
	@Mock FeatureEvaluator featureEvaluator;
	@Mock AuthenticationService authenticationService;
	@InjectMocks private VaultHelperImpl vaultHelper = spy(new VaultHelperImpl());

	private CanadaTaxEntity canadaTaxEntity;
	private ForeignTaxEntity foreignTaxEntity;
	private UsaTaxEntity usaTaxEntity;
	private BankAccount bankAccount;

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

		when(authenticationService.getCurrentUserCompanyId()).thenReturn(COMPANY_ID);
		when(featureEvaluator.hasFeature(COMPANY_ID, "vaultRead")).thenReturn(true);
		when(featureEvaluator.hasFeature(COMPANY_ID, "vaultObfuscate")).thenReturn(true);
		when(featureEvaluator.hasGlobalFeature("vaultObfuscatePrepend")).thenReturn(false);
	}

	@Test
	public void shouldNotSecureValuesIfFeatureToggleOff() {
		when(featureEvaluator.hasFeature(COMPANY_ID, "vaultObfuscate")).thenReturn(false);
		vaultHelper.secureEntity(bankAccount);
		assertEquals(ACCOUNT_NUMBER, bankAccount.getAccountNumber());
	}

	@Test
	public void shouldSecurAccountNumber() {
		vaultHelper.secureEntity(bankAccount);
		assertEquals(StringUtilities.showLastNCharacters(ACCOUNT_NUMBER, "x", 0), bankAccount.getAccountNumber());
	}

	@Test
	public void shouldSecureCanTaxEntity() {
		vaultHelper.secureEntity(canadaTaxEntity);
		assertEquals(StringUtilities.showLastNCharacters(TAX_NUMBER, "x", 0), canadaTaxEntity.getTaxNumber());
	}

	@Test
	public void shouldSecureUsaTaxEntity() {
		vaultHelper.secureEntity(usaTaxEntity);
		assertEquals(StringUtilities.showLastNCharacters(TAX_NUMBER, "x", 0), usaTaxEntity.getTaxNumber());
	}

	@Test
	public void shouldSecureForeignTaxEntity() {
		vaultHelper.secureEntity(foreignTaxEntity);
		assertEquals(StringUtilities.showLastNCharacters(TAX_NUMBER, "x", 0), foreignTaxEntity.getTaxNumber());
	}

	@Test
	public void shouldPrependSecureValue() {
		when(featureEvaluator.hasGlobalFeature("vaultObfuscatePrepend")).thenReturn(true);
		vaultHelper.secureEntity(foreignTaxEntity);
		assertEquals(Secured.PREPEND_MASKING_PATTERN + TAX_NUMBER, foreignTaxEntity.getTaxNumber());
	}

	@Test
	public void shouldReturnDefaultValueIfFeatureToggleOff() {
		when(featureEvaluator.hasFeature(COMPANY_ID, "vaultRead")).thenReturn(false);
		String defaultVault = UUID.randomUUID().toString();
		assertEquals(defaultVault, vaultHelper.get(bankAccount, "taxNumber", defaultVault).getValue());
	}

	@Test
	public void shouldReturnEmptyOnVaultServiceUnavailable() throws Exception {
		when(vaultServerService.get(anyString())).thenThrow(ServiceUnavailableException.class);
		String defaultVault = UUID.randomUUID().toString();
		assertEquals("", vaultHelper.get(bankAccount, "taxNumber", defaultVault).getValue());
	}

	@Test
	public void shouldReturnVaultValue() throws Exception {
		String vaultValue = UUID.randomUUID().toString();
		when(vaultServerService.get(anyString())).thenReturn(new VaultKeyValuePair("key", vaultValue));
		assertEquals(vaultValue, vaultHelper.get(bankAccount, "taxNumber", "").getValue());
	}

	@Test
	public void shouldSetRoutingNumber() throws Exception {
		String randomAccountNumber = UUID.randomUUID().toString();
		bankAccount.setAccountNumber("");
		when(vaultServerService.get(ACCOUNT_NUMBER_KEY)).thenReturn(new VaultKeyValuePair("key2", randomAccountNumber));
		vaultHelper.setVaultedValues(bankAccount);
		assertEquals(randomAccountNumber, bankAccount.getAccountNumber());
	}

	@Test
	public void shouldSetForeignTaxNumber() throws Exception {
		String randomTaxNumber = UUID.randomUUID().toString();
		foreignTaxEntity.setTaxNumber("");
		when(vaultServerService.get(FOREIGN_TAX_KEY)).thenReturn(new VaultKeyValuePair("key1", randomTaxNumber));
		vaultHelper.setVaultedValues(foreignTaxEntity);
		assertEquals(randomTaxNumber, foreignTaxEntity.getTaxNumber());
	}

	@Test
	public void shouldSetUsaTaxNumber() throws Exception {
		String randomTaxNumber = UUID.randomUUID().toString();
		usaTaxEntity.setTaxNumber("");
		when(vaultServerService.get(USA_TAX_KEY)).thenReturn(new VaultKeyValuePair("key1", randomTaxNumber));
		vaultHelper.setVaultedValues(usaTaxEntity);
		assertEquals(randomTaxNumber, usaTaxEntity.getTaxNumber());
	}

	@Test
	public void shouldSetCanadianTaxNumber() throws Exception {
		String randomTaxNumber = UUID.randomUUID().toString();
		canadaTaxEntity.setTaxNumber("");
		when(vaultServerService.get(CANADIAN_TAX_KEY)).thenReturn(new VaultKeyValuePair("key1", randomTaxNumber));
		vaultHelper.setVaultedValues(canadaTaxEntity);
		assertEquals(randomTaxNumber, canadaTaxEntity.getTaxNumber());
	}

	@Test
	public void shouldReturnEmptyOnNullEntity() throws Exception {
		assertEquals(0, vaultHelper.getVaultedValues(null).size());
	}

	@Test
	public void shouldReturnEmptyOnNonVaultedEntity() throws Exception {
		assertEquals(0, vaultHelper.getVaultedValues(new UserGroup()).size());
	}

	@Test
	public void shouldReturnEmptyIfVaultedEntityIdIsNull() throws Exception {
		assertEquals(0, vaultHelper.getVaultedValues(new BankAccount()).size());
	}

	@Test
	public void shouldReturnEmptyIfAllVaultedPropertiesAreNull() throws Exception {
		bankAccount.setAccountNumber(null);
		bankAccount.setRoutingNumber(null);
		assertEquals(0, vaultHelper.getVaultedValues(bankAccount).size());
	}

	@Test
	public void shouldReturnEmptyIfAllVaultedPropertiesAreEmpty() throws Exception {
		bankAccount.setAccountNumber("");
		bankAccount.setRoutingNumber("");
		assertEquals(0, vaultHelper.getVaultedValues(bankAccount).size());
	}

	@Test
	public void shouldReturnAccountNumberValue() throws Exception {
		bankAccount.setRoutingNumber("");
		VaultKeyValuePair pair = vaultHelper.getVaultedValues(bankAccount).get(0);
		assertEquals(ACCOUNT_NUMBER, pair.getValue());
	}

	@Test
	public void shouldReturnAccountNumberKey() throws Exception {
		bankAccount.setRoutingNumber("");
		VaultKeyValuePair pair = vaultHelper.getVaultedValues(bankAccount).get(0);
		assertEquals(ACCOUNT_NUMBER_KEY, pair.getId());
	}

	@Test
	public void shouldReturnCanadianTaxNumberValue() throws Exception {
		VaultKeyValuePair pair = vaultHelper.getVaultedValues(canadaTaxEntity).get(0);
		assertEquals(TAX_NUMBER, pair.getValue());
	}

	@Test
	public void shouldReturnCanadianTaxNumberKey() throws Exception {
		VaultKeyValuePair pair = vaultHelper.getVaultedValues(canadaTaxEntity).get(0);
		assertEquals(CANADIAN_TAX_KEY, pair.getId());
	}

	@Test
	public void shouldReturnForeignTaxNumberValue() throws Exception {
		VaultKeyValuePair pair = vaultHelper.getVaultedValues(foreignTaxEntity).get(0);
		assertEquals(TAX_NUMBER, pair.getValue());
	}

	@Test
	public void shouldReturnForeignTaxNumberKey() throws Exception {
		VaultKeyValuePair pair = vaultHelper.getVaultedValues(foreignTaxEntity).get(0);
		assertEquals(FOREIGN_TAX_KEY, pair.getId());
	}

	@Test
	public void shouldReturnUsaTaxNumberValue() throws Exception {
		VaultKeyValuePair pair = vaultHelper.getVaultedValues(usaTaxEntity).get(0);
		assertEquals(TAX_NUMBER, pair.getValue());
	}

	@Test
	public void shouldReturnUsaTaxNumberKey() throws Exception {
		VaultKeyValuePair pair = vaultHelper.getVaultedValues(usaTaxEntity).get(0);
		assertEquals(USA_TAX_KEY, pair.getId());
	}

	@Test
	public void shouldReturnEmptyKeyOnNull() {
		assertTrue("".equals(vaultHelper.buildKey(null, "someFieldName")));
	}

	@Test
	public void shouldReturnCanadianTaxEntityTaxNumberKey() {
		assertTrue(CANADIAN_TAX_KEY.equals(vaultHelper.buildKey(canadaTaxEntity, "taxNumber")));
	}

	@Test
	public void shouldReturnForeignTaxEntityTaxNumberKey() {
		assertTrue(FOREIGN_TAX_KEY.equals(vaultHelper.buildKey(foreignTaxEntity, "taxNumber")));
	}

	@Test
	public void shouldReturnUsaTaxEntityTaxNumberKey() {
		assertTrue(USA_TAX_KEY.equals(vaultHelper.buildKey(usaTaxEntity, "taxNumber")));
	}

	@Test
	public void shouldReturnBankAccountAccountNumberKey() {
		assertTrue(ACCOUNT_NUMBER_KEY.equals(vaultHelper.buildKey(bankAccount, "accountNumber")));
	}

	@Test
	public void shouldBuildDuplicatePairsWithSameKeysAndDifferentValuesForSameCountryDifferentCompanies() {
		String taxNumber = UUID.randomUUID().toString();

		Company company1 = new Company();
		company1.setCompanyNumber(UUID.randomUUID().toString());
		UsaTaxEntity usaTaxEntity1 = new UsaTaxEntity();
		usaTaxEntity1.setId(2L);
		usaTaxEntity1.setCompany(company1);

		Company company2 = new Company();
		company2.setCompanyNumber(UUID.randomUUID().toString());
		UsaTaxEntity usaTaxEntity2 = new UsaTaxEntity();
		usaTaxEntity2.setId(1L);
		usaTaxEntity2.setCompany(company2);

		VaultKeyValuePair pair1 = vaultHelper.buildDuplicateTaxNumberCheckPair(usaTaxEntity1, taxNumber);
		VaultKeyValuePair pair2 = vaultHelper.buildDuplicateTaxNumberCheckPair(usaTaxEntity2, taxNumber);
		assertEquals(pair1.getId(), pair2.getId());
		assertNotEquals(pair1.getValue(), pair2.getValue());
	}

	@Test
	public void shouldBuildDuplicateKeyPairWithCompanyNumberAsValue() {
		Company company = new Company();
		String companyNumber = UUID.randomUUID().toString();
		company.setCompanyNumber(companyNumber);
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setId(1L);
		usaTaxEntity.setCompany(company);

		VaultKeyValuePair pair = vaultHelper.buildDuplicateTaxNumberCheckPair(usaTaxEntity, UUID.randomUUID().toString());

		assertEquals(vaultHelper.getCompanyNumberFromDuplicateEntryValue(pair.getValue()), companyNumber);
	}

	@Test
	public void shouldBuildDifferentDuplicateKeysIfTaxNumberIsDifferent() {
		String taxNumber1 = UUID.randomUUID().toString();
		String taxNumber2 = UUID.randomUUID().toString();

		Company company = new Company();
		String companyNumber = UUID.randomUUID().toString();
		company.setCompanyNumber(companyNumber);
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setId(1L);
		usaTaxEntity.setCompany(company);

		VaultKeyValuePair pair1 = vaultHelper.buildDuplicateTaxNumberCheckPair(usaTaxEntity, taxNumber1);
		VaultKeyValuePair pair2 = vaultHelper.buildDuplicateTaxNumberCheckPair(usaTaxEntity, taxNumber2);

		assertNotEquals(pair1.getId(), pair2.getId());
	}

	@Test
	public void shouldIsDuplicateFalseIfVaultEntryDoesNotExist() throws Exception {
		String taxNumber = UUID.randomUUID().toString();
		String companyNumber = UUID.randomUUID().toString();
		when(vaultServerService.get(anyString())).thenReturn(new VaultKeyValuePair());
		assertFalse(vaultHelper.isDuplicateOutsideCompany("CAN", taxNumber, companyNumber));
	}

	@Test
	public void shouldIsDuplicateFalseIfVaultEntryExistsAndSameCompany() throws Exception {
		String taxNumber = UUID.randomUUID().toString();
		String companyNumber = UUID.randomUUID().toString();
		String key = vaultHelper.buildCountryTaxNumberKey("CAN", taxNumber);
		when(vaultServerService.get(key)).thenReturn(new VaultKeyValuePair(key, companyNumber));
		assertFalse(vaultHelper.isDuplicateOutsideCompany("CAN", taxNumber, companyNumber));
	}

	@Test
	public void shouldIsDuplicateTrueIfVaultEntryExistsAndDifferentCompany() throws Exception {
		String taxNumber = UUID.randomUUID().toString();
		String companyNumber1 = UUID.randomUUID().toString();
		String companyNumber2 = UUID.randomUUID().toString();
		String key = vaultHelper.buildCountryTaxNumberKey("CAN", taxNumber);
		when(vaultServerService.get(key)).thenReturn(new VaultKeyValuePair(key, companyNumber1));
		assertTrue(vaultHelper.isDuplicateOutsideCompany("CAN", taxNumber, companyNumber2));
	}

	@Test
	public void shouldGetDuplicateEntryFromVault() throws Exception {
		Company company = new Company();
		String companyNumber = UUID.randomUUID().toString();
		company.setCompanyNumber(companyNumber);
		UsaTaxEntity usaTaxEntity = new UsaTaxEntity();
		usaTaxEntity.setId(1L);
		usaTaxEntity.setCompany(company);

		String key = vaultHelper.buildKey(usaTaxEntity, "taxNumber");
		String vaultValue = UUID.randomUUID().toString();
		String dupKey = vaultHelper.buildCountryTaxNumberKey("usa", vaultValue);
		String dupValue = companyNumber;

		when(vaultServerService.get(key)).thenReturn(new VaultKeyValuePair(key, vaultValue));
		VaultKeyValuePair result = vaultHelper.getDuplicateKeyValueEntryFromVault(usaTaxEntity, "taxNumber");

		assertEquals(dupKey, result.getId());
		assertEquals(dupValue, result.getValue());
	}

	@Test
	public void shouldMultiGetWithCorrectKeys() throws Exception {
		when(vaultServerService.get(any(ArrayList.class))).thenReturn(new ArrayList<VaultKeyValuePair>());
		List<AbstractTaxEntity> entities = Lists.newArrayList(foreignTaxEntity, usaTaxEntity, canadaTaxEntity);
		List<VaultKeyValuePair> pairs = vaultHelper.multiGet(entities, "taxNumber");
		verify(vaultServerService).get(new ArrayList<String>() {{
			add(vaultHelper.buildKey(canadaTaxEntity, "taxNumber"));
			add(vaultHelper.buildKey(foreignTaxEntity, "taxNumber"));
			add(vaultHelper.buildKey(usaTaxEntity, "taxNumber"));
		}});
	}

	@Test
	public void shouldMultiGetEmptyKeySet() throws Exception {
		when(vaultServerService.get(any(ArrayList.class))).thenReturn(new ArrayList<VaultKeyValuePair>());
		List<AbstractTaxEntity> entities = Lists.newArrayList();
		vaultHelper.multiGet(entities, "taxNumber");
		verify(vaultServerService).get(new ArrayList<String>());
	}

	@Test
	public void shouldMultiGetWithoutDuplicates() throws Exception {
		when(vaultServerService.get(any(ArrayList.class))).thenReturn(new ArrayList<VaultKeyValuePair>());
		List<AbstractTaxEntity> entities =
				Lists.newArrayList(
						foreignTaxEntity, usaTaxEntity, canadaTaxEntity, canadaTaxEntity, foreignTaxEntity, usaTaxEntity);
		List<VaultKeyValuePair> pairs = vaultHelper.multiGet(entities, "taxNumber");
		verify(vaultServerService).get(new ArrayList<String>() {{
			add(vaultHelper.buildKey(canadaTaxEntity, "taxNumber"));
			add(vaultHelper.buildKey(foreignTaxEntity, "taxNumber"));
			add(vaultHelper.buildKey(usaTaxEntity, "taxNumber"));
		}});
	}

	@Test
	public void shouldGetIdFromKey() throws Exception {
		String key = vaultHelper.buildKey(canadaTaxEntity, "taxNumber");
		Long id = vaultHelper.getIdFromKey(key);
		assertEquals(canadaTaxEntity.getId(), id);
	}

	@Test
	public void shouldMapIdToTaxNumber() throws Exception {
		List<VaultKeyValuePair> pairs = new ArrayList<VaultKeyValuePair>() {{
			add(new VaultKeyValuePair(vaultHelper.buildKey(foreignTaxEntity, "taxNumber"), foreignTaxEntity.getTaxNumber()));
			add(new VaultKeyValuePair(vaultHelper.buildKey(usaTaxEntity, "taxNumber"), foreignTaxEntity.getTaxNumber()));
			add(new VaultKeyValuePair(vaultHelper.buildKey(canadaTaxEntity, "taxNumber"), foreignTaxEntity.getTaxNumber()));
		}};
		when(vaultServerService.get(any(List.class))).thenReturn(pairs);
		List<AbstractTaxEntity> entities = Lists.newArrayList(foreignTaxEntity, usaTaxEntity, canadaTaxEntity);
		Map<Long, String> map = vaultHelper.mapEntityIdToFieldValue(entities, AbstractTaxEntity.class, "taxNumber");

		assertEquals(map.get(foreignTaxEntity.getId()), foreignTaxEntity.getTaxNumber());
		assertEquals(map.get(usaTaxEntity.getId()), usaTaxEntity.getTaxNumber());
		assertEquals(map.get(canadaTaxEntity.getId()), canadaTaxEntity.getTaxNumber());
	}

	@Test
	public void shouldMapIdToTaxNumberField() throws Exception {
		when(featureEvaluator.hasFeature(COMPANY_ID, "vaultRead")).thenReturn(false);
		List<AbstractTaxEntity> entities = Lists.newArrayList(foreignTaxEntity, usaTaxEntity, canadaTaxEntity);
		Map<Long, String> map = vaultHelper.mapEntityIdToFieldValue(entities, AbstractTaxEntity.class, "taxNumber");
		assertEquals(map.get(foreignTaxEntity.getId()), foreignTaxEntity.getTaxNumber());
		assertEquals(map.get(usaTaxEntity.getId()), usaTaxEntity.getTaxNumber());
		assertEquals(map.get(canadaTaxEntity.getId()), canadaTaxEntity.getTaxNumber());
	}

	@Test
	public void shouldUnobfuscateFieldsFromVault() throws Exception {
		final String obfuscatedValue = "some-obfuscated-value";
		final String unobfuscatedValue = "some-UNobfuscated-value";
		when(featureEvaluator.hasFeature(COMPANY_ID, "vaultObfuscate")).thenReturn(true);
		when(vaultServerService.get(anyString())).thenReturn(new VaultKeyValuePair("some-id", unobfuscatedValue));
		foreignTaxEntity.setTaxNumber(obfuscatedValue);
		usaTaxEntity.setTaxNumber(obfuscatedValue);
		canadaTaxEntity.setTaxNumber(obfuscatedValue);
		bankAccount.setAccountNumber(obfuscatedValue);

		vaultHelper.unobfuscateEntityFields(ImmutableList.of(foreignTaxEntity, usaTaxEntity, canadaTaxEntity, bankAccount));

		assertEquals(unobfuscatedValue, foreignTaxEntity.getTaxNumber());
		assertEquals(unobfuscatedValue, usaTaxEntity.getTaxNumber());
		assertEquals(unobfuscatedValue, canadaTaxEntity.getTaxNumber());
		assertEquals(unobfuscatedValue, bankAccount.getAccountNumber());
	}
}
