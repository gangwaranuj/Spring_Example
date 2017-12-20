package com.workmarket.domains.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ManageMyWorkMarketTest {
	private ManageMyWorkMarket mmw;

	@Before
	public void setup() {
		mmw = new ManageMyWorkMarket();
	}

	@Test
	public void mmw_DefaultShowInFeed() throws Exception {
		assertTrue(mmw.getShowInFeed());
	}

	@Test
	public void mmw_SetShowInFeed_SetsIt() throws Exception {
		mmw.setShowInFeed(false);
		assertFalse(mmw.getShowInFeed());
	}
}
