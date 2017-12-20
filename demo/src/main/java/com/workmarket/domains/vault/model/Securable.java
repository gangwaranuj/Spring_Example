package com.workmarket.domains.vault.model;

import java.util.Map;

/**
 * Created by ant on 3/9/15.
 */
public interface Securable {

	public static String SECURE_MODE = "secured";
	public static String PARTIALLY_SECURE_MODE = "partial";
	public static String NONSECURE_MODE = "nonsecured";

	public Long getId();
	public Map<String, String> getVaultedProperties();
	public void setVaultedProperties(Map<String, String> vaultedProperties);
	public String getSecuredMode();
	public void setSecuredMode(String securedMode);

}