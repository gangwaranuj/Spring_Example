package com.workmarket.vault.services;

import java.util.List;

public interface VaultMigrationService {
	void migrateBankAccounts(List<Long> bankAccountIds);
	void migrateTaxEntities(List<Long> taxEntityIds, boolean saveVaultedValues, boolean saveDuplicateTins);
}
