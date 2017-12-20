package com.workmarket.domains.vault.service;

import com.workmarket.domains.vault.dao.LocalVaultDAOImpl;
import com.workmarket.domains.vault.dao.VaultDAO;
import com.workmarket.domains.vault.model.Securable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class LocalVaultServiceTest {

	protected VaultDAO localVaultDAO = new LocalVaultDAOImpl();

	@InjectMocks VaultServiceImpl vaultServiceImpl = new VaultServiceImpl();

	@Before
	public void setup() {
		vaultServiceImpl.setVaultDAO(localVaultDAO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calls_get_withNulls_throwsException() throws Exception {
		vaultServiceImpl.get(Securable.PARTIALLY_SECURE_MODE, null, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calls_get_withIdAndNulls_throwsException() throws Exception {
		vaultServiceImpl.get(Securable.PARTIALLY_SECURE_MODE, null,10);
	}

	@Test
	public void calls_get_withIdName_throwsException() throws Exception {
		vaultServiceImpl.get(Securable.PARTIALLY_SECURE_MODE, "taxNumberId", 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calls_put_withNulls_throwsException() throws Exception {
		vaultServiceImpl.put(null, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calls_put_withIdAndNulls_throwsException() throws Exception {
		vaultServiceImpl.put("BankAccount", 20, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calls_put_withIdNameAndEmptyMap_throwsException() throws Exception {
		Map<String,String> properties = new HashMap<>();
		vaultServiceImpl.put("TaxEntity", 20, properties);
	}

	@Test
	public void calls_put_withIdNameValue_success() throws Exception {
		Map<String,String> properties = new HashMap<>();
		properties.put("taxNumberId", "1234567898");
		vaultServiceImpl.put("TaxEntity", 20, properties);
		assertNotNull(vaultServiceImpl.get(Securable.PARTIALLY_SECURE_MODE, "TaxEntity", 20));
	}

}