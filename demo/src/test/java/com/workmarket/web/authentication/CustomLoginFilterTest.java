package com.workmarket.web.authentication;

import com.workmarket.domains.authentication.filters.CustomLoginFilter;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class CustomLoginFilterTest {

	@Test
	public void testSerialization() {
		Serializable original = new CustomLoginFilter();
		Serializable copy = (Serializable) SerializationUtils.clone(original);
		assertEquals(original, copy);
	}

}