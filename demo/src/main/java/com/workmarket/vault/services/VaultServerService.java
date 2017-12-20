package com.workmarket.vault.services;

import com.workmarket.common.exceptions.BadRequestException;
import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.vault.models.VaultKeyValuePair;

import java.util.List;


public interface VaultServerService {
	/**
	 * Remove a given key.
	 *
	 * @param key
	 * @throws ServiceUnavailableException
	 */
	void remove(String key) throws ServiceUnavailableException;

	void remove(List<VaultKeyValuePair> pairs) throws ServiceUnavailableException;

	/**
	 * Commit a key/value pair to the Vault. Will overwrite values for existing keys.
	 *
	 * @param pair
	 * @throws ServiceUnavailableException
	 * @throws BadRequestException
	 */
	void post(VaultKeyValuePair pair) throws ServiceUnavailableException, BadRequestException;

	/**
	 * Commit a collection of key/value pairs to the Vault. Will overwrite values for existing keys.
	 *
	 * @param pairs
	 * @throws ServiceUnavailableException
	 * @throws BadRequestException
	 */
	void post(List<VaultKeyValuePair> pairs) throws ServiceUnavailableException, BadRequestException;

	/**
	 * Get a pair by key.
	 *
	 * @param key
	 * @return
	 * @throws ServiceUnavailableException
	 */
	VaultKeyValuePair get(String key) throws ServiceUnavailableException;

	/**
	 * Multi-get a set of values. This operation is atomic: If any one get fails, a list of VaultKeyValuePair
	 * is returned, all of whoms isEmpty() is false.
	 *
	 * NOTE: Order of the result may not match order of keys.
	 * @param keys
	 * @return
	 * @throws ServiceUnavailableException
	 */
	List<VaultKeyValuePair> get(List<String> keys) throws ServiceUnavailableException;
}

