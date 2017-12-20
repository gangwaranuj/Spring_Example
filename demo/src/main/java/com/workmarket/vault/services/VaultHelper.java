package com.workmarket.vault.services;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.models.Vaultable;
import com.workmarket.vault.models.Vaulted;

import java.util.List;
import java.util.Map;

public interface VaultHelper {
	/**
	 * Extract the values of vaulted properties on a given entity.
	 * Will recursively go up the class heirarchy and look for {@link Vaulted}
	 * fields within a {@link Vaultable} class.
	 *
	 * @param entity
	 * @return
	 */
	List<VaultKeyValuePair> getVaultedValues(AbstractEntity entity) throws RuntimeException;

	/**
	 * Get values from Vault and set on a given entity.
	 * @param entity
	 */
	void setVaultedValues(AbstractEntity entity);

	/**
	 * Build the vaulted key given an entity and field name on the entity.
	 *
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	String buildKey(AbstractEntity entity, String fieldName);

	/**
	 * Build duplicate vault entry key.
	 * @param iso3Country
	 * @param taxNumber
	 * @return
	 */
	String buildCountryTaxNumberKey(String iso3Country, String taxNumber);

	/**
	 * Build duplicate vault entry.
	 * @param entity
	 * @param taxNumber
	 * @return
	 */
	VaultKeyValuePair buildDuplicateTaxNumberCheckPair(AbstractTaxEntity entity, String taxNumber);

	/**
	 * Check whether a given tax number in a given country is a duplicate outside a given company.
	 * @param iso3Country
	 * @param taxNumber
	 * @param companyNumber
	 * @return
	 */
	boolean isDuplicateOutsideCompany(String iso3Country, String taxNumber, String companyNumber);

	/**
	 * Checks if given pair is a duplicate check entry for a given company.
	 * @param pair
	 * @param companyNumber
	 * @return
	 */
	boolean isDuplicateOutsideCompany(VaultKeyValuePair pair, String companyNumber);

	/**
	 * Fetch duplicate tin check entry.
	 * @param iso3Country
	 * @param taxNumber
	 * @return
	 */
	VaultKeyValuePair getDuplicateKeyValueEntry(String iso3Country, String taxNumber);

	/**
	 * Obfuscate vaulted values on a given entity.
	 *
	 * @param entity
	 */
	void secureEntity(AbstractEntity entity);

	/**
	 * Return vault value for given vaulted field on an entity.
	 *
	 * @param entity
	 * @param fieldName
	 * @param defaultValue
	 * @return Empty value "" if error encountered, a noop if the vault is turned off and return defaultValue.
	 */
	VaultKeyValuePair get(AbstractEntity entity, String fieldName, String defaultValue);

	String getFieldNameFromId(String id);
	Long getIdFromKey(String key);

	VaultKeyValuePair removeEntityIdFromDuplicatePair(VaultKeyValuePair duplicateEntry, String id);

	VaultKeyValuePair addEntityIdToDuplicatePair(VaultKeyValuePair duplicateEntry, String id);
	String getCompanyNumberFromDuplicateEntryValue(String value);
	VaultKeyValuePair getDuplicateKeyValueEntryFromVault(AbstractTaxEntity ent, String fieldName);

	List<VaultKeyValuePair> multiGet(List<? extends AbstractEntity> partition, String field);
	<T extends AbstractEntity> Map<Long, String> mapEntityIdToFieldValue(List<T> taxEntities, Class<T> type, String field);

	/**
	 * Unobfuscate secured fields for a set of entities by calling out to the Vault. This is a no-op if the Vault is
	 * inaccessible for whatever reason (feature toggle or service is down) or if the value is empty or non-existent.
	 *
	 * @param entities
	 * @param <T>
	 */
	<T extends AbstractEntity> void unobfuscateEntityFields(List<T> entities);

	/**
	 * @see {@link #unobfuscateEntityFields(List)}
	 *
	 * @param entity
	 * @param <T>
	 */
	<T extends AbstractEntity> void unobfuscateEntityFields(T entity);
}
