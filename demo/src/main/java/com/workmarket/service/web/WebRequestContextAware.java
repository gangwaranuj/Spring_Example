package com.workmarket.service.web;

import java.io.Serializable;

/**
 * Created by joshlevine on 3/3/17.
 */
public interface WebRequestContextAware extends Serializable {
	WebRequestContext getWebRequestContext();
	void setWebRequestContext(WebRequestContext webRequestContext);
}
