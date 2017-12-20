package com.workmarket.service.business.scheduler;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.business.event.MigrateBankAccountsEvent;
import com.workmarket.service.business.event.MigrateTaxEntitiesEvent;
import com.workmarket.service.business.event.RestoreBankAccountNumbersFromVault;
import com.workmarket.service.business.event.RestoreTaxEntityTaxNumbersFromVault;
import com.workmarket.service.business.event.RestoreTaxReportTaxNumbersFromVault;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.models.Secured;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.services.VaultServerService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@ManagedResource(objectName="bean:name=vaultMigrationExecutor", description="Kickoff vault migrations")
public class VaultMigrationExecutor {
	private Log logger = LogFactory.getLog(VaultMigrationExecutor.class);
	private static final int TAX_NUMBER_MAX_LENGTH = 24; // database defined; please see tax_entity.tax_number
	@Autowired TaxService taxService;
	@Autowired BankingService bankingService;
	@Autowired EventRouter eventRouter;
	@Autowired VaultServerService vaultServerService;
	@Autowired VaultHelper vaultHelper;
	@Autowired FeatureEvaluator featureEvaluator;

	@VisibleForTesting
	public void setLogger(final Log logger) {
		this.logger = logger;
	}

	@ManagedOperation(description = "Restore tax entity tax numbers from Vault")
	public void restoreTaxEntityTaxNumbers(final String fromModifiedDate) {
		eventRouter.sendEvent(new RestoreTaxEntityTaxNumbersFromVault(fromModifiedDate));
	}

	@ManagedOperation(description = "Restore bank account account numbers from Vault")
	public void restoreBankAccountNumbers(final String fromCreatedOnDate) {
		eventRouter.sendEvent(new RestoreBankAccountNumbersFromVault(fromCreatedOnDate));
	}

	@ManagedOperation(description = "Restore tax report tax numbers from Vault")
	public void restoreTaxReportTaxNumbers(final String fromCreatedOn) {
		eventRouter.sendEvent(new RestoreTaxReportTaxNumbersFromVault(fromCreatedOn));
	}

	@ManagedOperation(description = "Migrate all bank account info to Vault")
	public void migrateAllBankAccounts(int threadCount) {
		List<Long> allIds = bankingService.getAllIds();

		if (CollectionUtils.isEmpty(allIds)) {
			return;
		}

		int size = allIds.size()/threadCount;
		List<List<Long>> partitions = Lists.partition(allIds, size);
		for (List<Long> partition : partitions) {
			eventRouter.sendEvent(new MigrateBankAccountsEvent(Lists.newArrayList(partition)));
		}
	}

	@ManagedOperation(description = "Migrate all tax entity info to Vault")
	public void migrateTaxEntities(int threadCount, String commaSeparatedStringOfIds, boolean saveVaultedValues,
	                               boolean saveDuplicateTins) {
		List<Long> allIds = new ArrayList<>();

		if (StringUtils.isEmpty(commaSeparatedStringOfIds)) {
			allIds = taxService.getAllActivatedAccountIds();
		} else {
			List<String> list = Arrays.asList(commaSeparatedStringOfIds.split(","));
			for (String id : list) {
				allIds.add(Long.parseLong(id));
			}
		}

		int size = allIds.size()/threadCount;
		List<List<Long>> partitions = Lists.partition(allIds, size);
		for (List<Long> partition : partitions) {
			eventRouter.sendEvent(new MigrateTaxEntitiesEvent(Lists.newArrayList(partition), saveVaultedValues, saveDuplicateTins));
		}
	}

	@ManagedOperation(description = "Verify selected tax entities")
	public void verifySelectedTaxEntities(String commaSeparatedIds) throws Exception {
		List<String> list = Arrays.asList(commaSeparatedIds.split(","));
		List<Long> ids = new ArrayList<>();
		for (String id : list) {
			ids.add(Long.parseLong(id));
		}

		logger.info("=================== tax entity verification begin ================================");
		verifySelectedTaxEntities(ids);
		logger.info("=================== tax entity verification done ================================");
	}

	@ManagedOperation(description = "Verify all tax entities")
	public void verifyAllTaxEntities() throws Exception {
		verifyAllTaxEntitiesWithSize(null);
	}

	@ManagedOperation(description = "Verify tax entities with partition size")
	public void verifyAllTaxEntitiesWithSize(Integer size) throws Exception {
		List<Long> allIds = taxService.getAllActivatedAccountIds();

		if (CollectionUtils.isEmpty(allIds)) {
			return;
		}

		logger.info("=================== tax entity verification begin ================================");

		if (size == null) {
			size = 1000;
		}

		List<List<Long>> partitions = Lists.partition(allIds, size);
		for (List<Long> partition : partitions) {
			verifySelectedTaxEntities(partition);
		}

		logger.info("=================== tax entity verification done ================================");
	}

	private void verifySelectedTaxEntities(List<Long> ids) throws Exception {
		List<? extends AbstractTaxEntity> entities = taxService.findTaxEntitiesById(ids);
		for (AbstractTaxEntity e : entities) {
			if (StringUtils.isBlank(e.getTaxNumberSanitized())) {
				continue;
			}

			String key = vaultHelper.buildKey(e, "taxNumber");
			VaultKeyValuePair pair = vaultServerService.get(key);

			if (pair.isEmpty()) {
				logger.error("[vault log] missing vault value for key " + key);
				continue;
			}

			final String dbValue = StringUtilities.removePrepend(e.getTaxNumberSanitized()).trim();
			if (!dbValue.startsWith("xxxx")) { // not obfuscated
				String vaultValue = pair.getValue().trim();
				vaultValue = vaultValue.substring(0, Math.min(TAX_NUMBER_MAX_LENGTH, vaultValue.length()));
				if (featureEvaluator.hasGlobalFeature("vaultObfuscate")) {
					if (vaultValue.length() != dbValue.length()) {
						logger.error("[vault log] vault value length different from db value length for key " + key);
						continue;
					}
				} else if (!vaultValue.equals(dbValue)) {
					logger.error("[vault log] vault value different from db value for key " + key);
					continue;
				}
			}

			VaultKeyValuePair duplicatePair = vaultHelper.getDuplicateKeyValueEntry(e.getIsoCountry().getISO3(), pair.getValue());
			final boolean isActive = e.getActiveFlag() != null && e.getActiveFlag();

			if (duplicatePair.isEmpty() && isActive) {
				logger.error("[vault log] missing duplicate entry for active tax entity for key " + key);
				continue;
			}

			if (!duplicatePair.isEmpty() && isActive && !duplicatePair.getValue().equals(e.getCompany().getCompanyNumber())) {
				logger.error(String.format("[vault log] duplicate entry found for another company %s %s", duplicatePair.getValue(),
					e.getCompany().getCompanyNumber()));
			}
		}

	}
	@ManagedOperation(description = "Verify bank account numbers for unintended obfuscation")
	public void verifyBankAccountNumbersFromCreatedDate(final String fromCreatedOnDate) throws Exception {
		final List<BankAccount> accounts =
				bankingService.getAllBankAccountsFrom(DateUtilities.getCalendarFromISO8601(fromCreatedOnDate));

		log(accounts, "accountNumber");
		logger.info("=================== bank account number verification complete ================================");
	}

	@ManagedOperation(description = "Verify bank account numbers for unintended obfuscation")
	public void verifyBankAccountNumbersFromModifiedDate(final String fromModifiedOnDate) throws Exception {
		final List<BankAccount> accounts =
				bankingService.getAllBankAccountsFromModifiedOn(DateUtilities.getCalendarFromISO8601(fromModifiedOnDate));

		log(accounts, "accountNumber");
		logger.info("=================== bank account number verification complete ================================");
	}

	@ManagedOperation(description = "Verify tax numbers for unintended obfuscation")
	public void verifyTaxNumbersFromModifiedDate(final String fromModifiedOnDate) throws Exception {
		final List<? extends AbstractTaxEntity> entitites =
				taxService.findAllTaxEntitiesFromModifiedDate(DateUtilities.getCalendarFromISO8601(fromModifiedOnDate));

		log(entitites, "taxNumber");
		logger.info("=================== tax number verification complete ================================");
	}

	@ManagedOperation(description = "Verify tax numbers for unintended obfuscation")
	public void verifyTaxNumbersFromId(final long fromId) throws Exception {
		final List<? extends AbstractTaxEntity> entitites = taxService.findAllAccountsFromId(fromId);

		log(entitites, "taxNumber");
		logger.info("=================== tax number verification complete ================================");
	}

	private void log(final List<? extends AbstractEntity> entities, final String field) {
		final List<? extends List<? extends AbstractEntity>> partitions = Lists.partition(entities, 10); // partition for efficiency
		for (final List<? extends AbstractEntity> part : partitions) {
			final List<VaultKeyValuePair> vaultPairs = vaultHelper.multiGet(part, field);
			for (final VaultKeyValuePair pair : vaultPairs) {
				logIfObfuscated(pair);
			}
		}
	}

	private void logIfObfuscated(VaultKeyValuePair pair) {
		if (pair.isEmpty()) {
			return;
		}

		final String vaultValue = pair.getValue();
		final String obfuscatedValue = StringUtilities.showLastNCharacters(vaultValue, Secured.MASKING_PATTERN, 0);

		if (obfuscatedValue.equals(vaultValue)) {
			logger.error("[vault log] Key " + pair.getId() + " is obfuscated!");
		}
	}

	@ManagedOperation(description = "Verify bank entities")
	public void verifyBankAccounts(int threadCount) throws Exception {
		List<Long> allIds = bankingService.getAllIds();

		if (CollectionUtils.isEmpty(allIds)) {
			return;
		}

		int size = allIds.size()/threadCount;
		List<List<Long>> partitions = Lists.partition(allIds, size);
		for (List<Long> partition : partitions) {
			for (Long id : partition) {
				AbstractBankAccount abstractBankAccount = bankingService.findBankAccount(id);
				BankAccount bankAccount;
				if (abstractBankAccount instanceof BankAccount) {
					bankAccount = (BankAccount) abstractBankAccount;
				} else {
					continue;
				}

				VaultKeyValuePair pair;

				final String sanitized = bankAccount.getAccountNumberSanitized().trim();
				if (StringUtils.isNotBlank(sanitized)) {
					pair = vaultServerService.get(vaultHelper.buildKey(bankAccount, "accountNumber"));
					if (!pair.isEmpty()) {
						if (!sanitized.startsWith("xxxx") && !pair.getValue().trim().equals(sanitized)) {
							logger.error("[vault log] Unequal vault account number bankAccount for key " + pair.getId());
						}
					} else {
						logger.error("[vault log] Vault account number not found for " + bankAccount.getId());
					}
				}
			}
		}

		logger.info("=================== bank account verification complete ================================");
	}

	@ManagedOperation(description = "Removes duplicate entries for which the tax entity is not active")
	public void removeInactiveDuplicateKeys(final String commaSeparatedIds) throws Exception {
		List<String> list = Arrays.asList(commaSeparatedIds.split(","));
		List<Long> taxEntityIds = new ArrayList<>();
		for (String id : list) {
			taxEntityIds.add(Long.parseLong(id));
		}
		final List<? extends AbstractTaxEntity> entities = taxService.findTaxEntitiesById(taxEntityIds);
		final List<VaultKeyValuePair> addPairs = new ArrayList<>();
		final List<VaultKeyValuePair> removePairs = new ArrayList<>();

		for (final AbstractTaxEntity entity : entities) {
			if (entity.getActiveFlag() != null && entity.getActiveFlag()) {
				addPairs.add(vaultHelper.buildDuplicateTaxNumberCheckPair(entity, entity.getTaxNumberSanitized()));
			} else {
				removePairs.add(new VaultKeyValuePair(
					vaultHelper.buildCountryTaxNumberKey(
						entity.getIsoCountry().getISO3(),
						entity.getTaxNumberSanitized()),
					""));
			}
		}

		vaultServerService.remove(removePairs);
		vaultServerService.post(addPairs);
	}
}

