package com.workmarket.service.infra.business;

import com.codahale.metrics.Meter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.helpers.WMCallable;
import com.workmarket.service.web.WebRequestContextProvider;
import rx.functions.Func2;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A per request cache.
 */
class PerRequestCache<T, F> {
	private final Meter attempts;
	private final Meter hits;
	private final Meter misses;

	private final Cache<String, T> cache;
	private WebRequestContextProvider context;
	private final Func2<String, F, T> cacheFiller;

	PerRequestCache(
			final MetricRegistryFacade facade,
			final String metricPrefix,
			final WebRequestContextProvider context,
			final Func2<String, F, T> cacheFiller) {
		this.cache = CacheBuilder.newBuilder()
			.maximumSize(100)
			.build();
		this.attempts = facade.meter(metricPrefix + ".attempts");
		this.hits = facade.meter(metricPrefix + ".hits");
		this.misses = facade.meter(metricPrefix + ".misses");
		this.context = context;
		this.cacheFiller = cacheFiller;
	}

	void set(final String userUuid, final T value) {
		final String left = makeCacheKey(userUuid);
		setItemWithCacheKey(left, value);
	}

	private void setItemWithCacheKey(final String left, final T value) {
		cache.put(left, value);
	}

	T get(final String userUuid, final F funcArg) {
		attempts.mark();
		final String cacheKey = makeCacheKey(userUuid);
		final AtomicBoolean hit = new AtomicBoolean(true);
		final T cached;
		try {
			cached = cache.get(cacheKey, new WMCallable<T>(context) {
				@Override
				public T apply() throws Exception {
					hit.set(false);
					return cacheFiller.call(userUuid, funcArg);
				}
			});
		} catch (final ExecutionException e) {
			throw new RuntimeException("failed executing cache filler for " + userUuid);
		}
		if (hit.get()) {
			hits.mark();
		} else {
			misses.mark();
		}
		return cached;
	}

	private String makeCacheKey(String uuid) {
		return context.getWebRequestContext().getRequestId() + uuid;
	}

	void setContext(final WebRequestContextProvider context) {
		this.context = context;
	}
}
