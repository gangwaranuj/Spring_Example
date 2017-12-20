package com.workmarket.context;

import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoader;

public class MockWorkmarketContext extends MockServletContext {

	public MockWorkmarketContext() {
		super("");
	}
	
	public void setContext(String resources) {
		addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, resources);
	}

	@Override
	public String getContextPath() {
		return null;
	}
}
