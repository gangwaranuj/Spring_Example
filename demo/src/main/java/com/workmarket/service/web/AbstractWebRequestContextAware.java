package com.workmarket.service.web;

/**
 * Created by joshlevine on 3/3/17.
 */
public abstract class AbstractWebRequestContextAware implements WebRequestContextAware {
	private static final long serialVersionUID = 15317209180938980L;

	private WebRequestContext webRequestContext;

	@Override
	public WebRequestContext getWebRequestContext() {
		return webRequestContext;
	}

	@Override
	public void setWebRequestContext(final WebRequestContext webRequestContext) {
		this.webRequestContext = webRequestContext;
	}
}
