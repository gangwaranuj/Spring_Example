package com.workmarket.service.infra.business;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.service.web.WebRequestContextProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.functions.Func2;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Test that the cache works.
 */
@RunWith(MockitoJUnitRunner.class)
public class PerRequestCacheTest {
	private final MetricRegistry registry = new MetricRegistry();
	private final MetricRegistryFacade facade = new WMMetricRegistryFacade(registry, "test");

	private WebRequestContextProvider context = new WebRequestContextProvider();

	@Test
	public void basicThings() throws Exception {
		final AtomicLong count = new AtomicLong(0);
		final PerRequestCache<Long, Void> sic = new PerRequestCache<Long, Void>(facade, "test",
				context, new Func2<String, Void, Long>() {
			@Override
			public Long call(String s, Void aVoid) {
				return count.incrementAndGet();
			}
		});
		context.getWebRequestContext("REQUESTID", "TENANTID");

		assertEquals((Long) 1L, sic.get("FOO", null));
		assertEquals((Long) 1L, sic.get("FOO", null));
		assertEquals((Long) 2L, sic.get("BAR", null));
		assertEquals((Long) 2L, sic.get("BAR", null));
		context.getWebRequestContext("OTHERREQUESTID", "OTHERTENANTID");
		assertEquals((Long) 3L, sic.get("BAR", null));
	}
}
