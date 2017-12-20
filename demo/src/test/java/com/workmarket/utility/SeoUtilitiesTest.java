package com.workmarket.utility;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SeoUtilitiesTest {

	@Test
	public void testBuildSEOFriendlyKeywords() {
		assertEquals("", SeoUtilities.buildSEOFriendlyKeywords(""));
		assertEquals("keyword1, keyword2, keyword3, New York, NY", SeoUtilities.buildSEOFriendlyKeywords("keyword1 keyword2 keyword3", "New York", "NY"));
	}

}