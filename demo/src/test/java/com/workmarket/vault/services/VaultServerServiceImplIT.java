package com.workmarket.vault.services;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import com.workmarket.vault.models.VaultKeyValuePair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class VaultServerServiceImplIT extends BaseServiceIT {
	private static final String ID = "key1";
	private static final String VALUE = "value1";
	private static final String ID2 = "key2";
	private static final String VALUE2 = "value2";

	@Autowired @Qualifier("vaultServerServiceImpl") VaultServerService vaultServerService;
	@Autowired VaultHelper vaultHelper;
	@Autowired BankingService bankingService;

	@Before
	public void setup() throws Exception {
		vaultServerService.remove(ID);
		vaultServerService.remove(ID2);
	}

	@Test
	@Ignore
	public void shouldMultiGet() throws Exception {
		List<String> keys = new ArrayList<>();
		List<Long> ids = bankingService.getAllIds();
		for (Long id : ids) {
			keys.add("BankAccount:" + id + ":accountNumber");
		}
		long startNanos = System.nanoTime();
		List<VaultKeyValuePair> pairs = vaultServerService.get(keys);

		assertEquals(keys.size(), pairs.size());
		Map dupes = new HashMap<String, Integer>();
		for (VaultKeyValuePair pair : pairs) {
			assertFalse(pair.isEmpty());
			if (dupes.containsKey(pair.getValue())) {
				throw new Exception("dupes found for (" + pair.getId() + ", " + pair.getValue() + ")");
			} else {
				dupes.put(pair.getValue(), 1);
			}
		}

		startNanos = System.nanoTime();
		for (String key : keys) {
			assertFalse(vaultServerService.get(key).isEmpty());
		}
	}

	@Test
	@Ignore
	public void shouldRemoveKey() throws Exception {
		VaultKeyValuePair pair = new VaultKeyValuePair(ID, VALUE);
		vaultServerService.post(pair);
		vaultServerService.remove(ID);
		pair = vaultServerService.get(ID);
		assertTrue(pair.isEmpty());
	}

	@Test
	@Ignore
	public void shouldASinglePostKeyValuePair() throws Exception {
		VaultKeyValuePair pair = new VaultKeyValuePair(ID, VALUE);
		vaultServerService.post(pair);
		pair = vaultServerService.get(ID);
		assertEquals(pair.getValue(), VALUE);
	}

	@Test
	@Ignore
	public void shouldPostMultiplePairs() throws Exception {
		VaultKeyValuePair pair1 = new VaultKeyValuePair(ID, VALUE);
		VaultKeyValuePair pair2 = new VaultKeyValuePair(ID2, VALUE2);
		vaultServerService.post(ImmutableList.of(pair1, pair2));
		pair1 = vaultServerService.get(ID);
		pair2 = vaultServerService.get(ID2);
		assertEquals(pair1.getValue(), VALUE);
		assertEquals(pair2.getValue(), VALUE2);
	}
}
