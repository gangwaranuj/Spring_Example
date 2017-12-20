package com.workmarket.utility;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.List;

@RunWith(BlockJUnit4ClassRunner.class)
public class SearchUtilitiesTest {

	@Test
	public void sanitizeKeywords() throws Exception {
		Assert.assertEquals("Do Work  312483438139116292" , SearchUtilities.sanitizeKeywords("Do Work!-312483438139116292"));
	}


	@Test
	public void joinWithOR() throws Exception {
		List<String> stringList = Lists.newArrayList("Hello", "Hellow", "Hallow");
		Assert.assertEquals("Hello OR Hellow OR Hallow" , SearchUtilities.joinWithOR(stringList));
	}


	@Test
	public void joinWithAND() throws Exception {
		List<String> stringList = Lists.newArrayList("Hello", "Hellow", "Hallow");
		Assert.assertEquals("Hello AND Hellow AND Hallow" , SearchUtilities.joinWithAND(stringList));
	}

	@Test
	public void encodeId_withNullId() {
		Assert.assertEquals("id__id", SearchUtilities.encodeId(null));
	}

	@Test
	public void encodeId_success() {
		Assert.assertEquals("id_1_id", SearchUtilities.encodeId(1L).trim());
	}

	@Test
	public void decodeId_success() {
		Assert.assertEquals(1, SearchUtilities.decodeId("id_1_id").intValue());
	}

	@Test
	public void decodeId_withNull_returnsNull() {
		Assert.assertEquals(null, SearchUtilities.decodeId(null));
	}

	@Test
	public void extractKeywords() {
		Assert.assertEquals("some text with \"\" quotes", SearchUtilities.extractKeywords("some text with \"\" quotes", 200));
	}

	@Test
	public void escapeReservedWords() {
		Assert.assertEquals("\\OR", SearchUtilities.escapeReservedWords("OR"));
	}
}
