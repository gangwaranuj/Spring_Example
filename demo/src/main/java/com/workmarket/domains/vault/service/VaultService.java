package com.workmarket.domains.vault.service;

import java.util.Map;

/**
 * Created by ant on 2/25/15.
 */
public interface VaultService {

	public void put(String className, long id, Map<String,String> properties);
	public Map<String,String> get(String securedMode, String className, long id);

}
