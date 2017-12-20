package com.workmarket.vault.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class VaultKeyValuePairTest {
	private static final String KEY = "key";
	private static final String VALUE = "value";

	@Test
	public void shouldNotBeEmpty() {
		assertFalse(new VaultKeyValuePair(KEY, VALUE).isEmpty());
	}

	@Test
	public void shouldBeEmptyIfKeyIsNull() {
		assertTrue(new VaultKeyValuePair(null, VALUE).isEmpty());
	}

	@Test
	public void shouldBeEmptyIfKeyIsEmpty() {
		assertTrue(new VaultKeyValuePair("", VALUE).isEmpty());
	}

	@Test
	public void shouldBeEmptyIfValueIsNull() {
		assertTrue(new VaultKeyValuePair(KEY, null).isEmpty());
	}

	@Test
	public void shouldBeEmptyIfValueIsEmpty() {
		assertTrue(new VaultKeyValuePair(KEY, "").isEmpty());
	}
}