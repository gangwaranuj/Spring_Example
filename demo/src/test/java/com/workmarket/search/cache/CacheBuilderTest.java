package com.workmarket.search.cache;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * Author: rocio
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CacheBuilderTest {

	@Test
	public void synchronize() {
		CacheBuilder cacheBuilder = new CacheBuilder(Lists.newArrayList(1L, 3L));
		cacheBuilder.synchronize();
		assertTrue(cacheBuilder.getMisses().isEmpty());
		assertTrue(cacheBuilder.getEntityIdNameMap().isEmpty());
	}
}

