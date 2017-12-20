package com.workmarket.service.infra.http;

import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.service.infra.http.server.Host;
import com.workmarket.service.infra.http.server.LoadBalanceStrategy;
import com.workmarket.service.infra.http.vo.Nothing;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HttpRequestUtilCommandTest {

	@Test
	public void testLoadBalancerIteration() throws Exception {
		final CountDownLatch latch = new CountDownLatch(3);
		boolean exceptionThrown = false;
		try {
			new HttpRequestUtilCommand().usingLoadBalancerStrategy(new LoadBalanceStrategy() {
				@Override
				public String getNextHost() {
					latch.countDown();
					return "foo";
				}

				@Override
				public int getNumHosts() {
					return 3;
				}

				@Override
				public void setFailureNumberThreshold(int t) {
				}

				@Override
				public void incrementFailure(String host) {
				}

				@Override
				public void incrementFailure(Host host) {
				}
			}).callAndReturn(Nothing.class);
		} catch (ServiceUnavailableException e) {
			exceptionThrown = true;
		}

		assertEquals("Should have finished iterating over the hosts in our LB", 0, latch.getCount());
		assertTrue("Exception should have been thrown when we have unsuccessfully called each host.", exceptionThrown);
	}

	@Test
	public void testInstanceWithHostIsNullAndLoadBalancerIsIterated() {
		boolean exceptionThrown = false;
		try {
			new HttpRequestUtilCommand().callAndReturn(Nothing.class);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue("Exception should have been thrown when calling with a null host. ", exceptionThrown);
	}
}