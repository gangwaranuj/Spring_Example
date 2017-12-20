package com.workmarket.helpers;

import com.workmarket.service.web.AbstractWebRequestContextAware;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;

import java.util.concurrent.Callable;

/**
 * Created by joshlevine on 3/3/17.
 */
public abstract class WMCallable<T> extends AbstractWebRequestContextAware implements Callable<T> {

	private final WebRequestContextProvider webRequestContextProvider;

	public WMCallable(final WebRequestContextProvider webRequestContextProvider) {
		this.webRequestContextProvider = webRequestContextProvider;
		// store RC bits here;
		webRequestContextProvider.inject(this);
	}

	public final T call() throws Exception {
		// extract and return apply();
		webRequestContextProvider.extract(this);
		return apply();
	}

	public abstract T apply() throws Exception;
}