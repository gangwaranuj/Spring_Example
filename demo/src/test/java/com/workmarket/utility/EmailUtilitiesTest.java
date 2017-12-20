package com.workmarket.utility;

import static com.google.common.collect.Lists.asList;
import static com.workmarket.utility.EmailUtilities.containsEmail;
import static com.workmarket.utility.EmailUtilities.findEmail;
import static com.workmarket.utility.EmailUtilities.isValidEmailAddress;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class EmailUtilitiesTest {

	@Test
	public void testIsValidEmail() {
		assertTrue(isValidEmailAddress("kristian@workmarket.com"));
		assertTrue(isValidEmailAddress("capitalist.in.disguise@freespeech.cu"));
		assertFalse(isValidEmailAddress("@yourmom.com"));
		assertFalse(isValidEmailAddress("dog@"));
		assertFalse(isValidEmailAddress("laces.out.dan"));
	}

	@Test
	public void testContainsEmail() {
		String[] keywordsArr = { "this", "is", "a", "list", "of", "keywords", "that", "do", "not", "have", "a", "valid", "email" };
		List<String> keywords = asList(keywordsArr);
		assertFalse(containsEmail(keywords));
		keywords = asList("fourty-two@lifetheuniverseandeverything.com", keywordsArr);
		assertTrue(containsEmail(keywords));
	}

	@Test
	public void testFindEmail() {
		String[] keywordsArr = { "this", "is", "a", "list", "of", "keywords", "that", "do", "not", "have", "a", "valid", "email" };
		List<String> keywords = asList("forty-two@lifetheuniverseandeverything.com", keywordsArr);
		Assert.assertEquals("forty-two@lifetheuniverseandeverything.com", findEmail(keywords));
	}

}
