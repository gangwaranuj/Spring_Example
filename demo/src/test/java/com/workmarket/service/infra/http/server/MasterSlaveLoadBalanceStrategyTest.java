package com.workmarket.service.infra.http.server;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class MasterSlaveLoadBalanceStrategyTest {

	@Test
	public void shouldAlwaysHitMasterFirst() {
		MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		strategy.loadHosts("foo.com,bar.com");
		assertEquals("foo.com", strategy.getNextHost());
	}

	@Test
	public void shouldFailToSecondaryWhenMasterFailsAndPromoteToMaster() {
		MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		strategy.loadHosts("foo.com,bar.com");
		assertEquals("foo.com", strategy.getPrimary().getHost());
		assertEquals("bar.com", strategy.getSecondary().getHost());
		strategy.setFailureNumberThreshold(1);
		strategy.incrementFailure(new Host(strategy.getPrimary().getHost()));
		assertEquals("foo.com", strategy.getSecondary().getHost());
		assertEquals("bar.com", strategy.getPrimary().getHost());
		strategy.incrementFailure(new Host(strategy.getPrimary().getHost()));
		assertEquals("foo.com", strategy.getPrimary().getHost());
		assertEquals("bar.com", strategy.getSecondary().getHost());
	}

	@Test
	public void testHostConfigurationSettings() {
		MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		assertEquals(null, strategy.getPrimary());
		assertEquals(null, strategy.getSecondary());
		strategy.loadHosts("foo.com,bar.com");
		assertEquals("foo.com", strategy.getPrimary().getHost());
		assertEquals("bar.com", strategy.getSecondary().getHost());
		strategy.loadHosts("foo.com");
		assertEquals("foo.com", strategy.getPrimary().getHost());
		assertEquals(null, strategy.getSecondary());
		strategy.loadHosts(",bar.com");
		assertEquals("bar.com", strategy.getPrimary().getHost());
		assertEquals(null, strategy.getSecondary());
		strategy.loadHosts("foo.com,bar.com,goob.y");
		assertEquals("foo.com", strategy.getPrimary().getHost());
		assertEquals("bar.com", strategy.getSecondary().getHost());
		strategy.loadHosts(",,,,,,,,foo.com,,,bar.com");
		assertEquals("foo.com", strategy.getPrimary().getHost());
		assertEquals("bar.com", strategy.getSecondary().getHost());
	}

	@Test
	public void testArrayCollapsing() {
		String[] split = ",,,,,,,,foo.com,,,bar.com".split(",", -1);
		String[] scrubbed = MasterSlaveLoadBalanceStrategy.collapseArray(split);
		assertEquals(2, scrubbed.length);
		assertEquals("foo.com", scrubbed[0]);
		assertEquals("bar.com", scrubbed[1]);

		String[] split1 = ",bar.com".split(",", -1);
		String[] scrubbed1 = MasterSlaveLoadBalanceStrategy.collapseArray(split1);
		assertEquals(1, scrubbed1.length);
		assertEquals("bar.com", scrubbed1[0]);
	}

	@Test
	public void testNumberHost() {
		MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		strategy.loadHosts("foo.com,bar.com");
		assertEquals(2, strategy.getNumHosts());
		strategy.loadHosts(",bar.com");
		assertEquals(1, strategy.getNumHosts());
		strategy.loadHosts("foo.com,bar.com,bit.com");
		assertEquals(2, strategy.getNumHosts());
		strategy.loadHosts("");
		assertEquals(0, strategy.getNumHosts());
	}

	@Test
	public void incrementFailureWithNullAndEmptyHost() {
		MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		strategy.incrementFailure("");
	}

	@Test
	public void testBasicSwapping() {
		MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		strategy.loadHosts("foo.com,bar.com");
		assertEquals("foo.com", strategy.getPrimary().getHost());
		assertEquals("bar.com", strategy.getSecondary().getHost());
		strategy.swap();
		assertEquals("bar.com", strategy.getPrimary().getHost());
		assertEquals("foo.com", strategy.getSecondary().getHost());
	}

	@Test
	public void testSwapFailureClearing() {
		MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		strategy.loadHosts("foo.com,bar.com");
		assertEquals("foo.com", strategy.getPrimary().getHost());
		assertEquals("bar.com", strategy.getSecondary().getHost());
		strategy.getPrimary().getFailures().incrementAndGet();
		strategy.getSecondary().getFailures().incrementAndGet();
		assertEquals(1, strategy.getPrimary().getFailures().get());
		assertEquals(1, strategy.getSecondary().getFailures().get());
		strategy.swap();
		assertEquals("foo.com", strategy.getSecondary().getHost());
		assertEquals("bar.com", strategy.getPrimary().getHost());
		assertEquals(0, strategy.getPrimary().getFailures().get());
		assertEquals(0, strategy.getSecondary().getFailures().get());
	}

	@Test
	public void testSwapWithOnlyMaster() {
		MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		strategy.loadHosts(",bar.com");
		assertEquals("bar.com", strategy.getPrimary().getHost());
		assertEquals(null, strategy.getSecondary());
		strategy.setFailureNumberThreshold(1);
		strategy.incrementFailure(new Host(strategy.getPrimary().getHost()));
		assertEquals("bar.com", strategy.getPrimary().getHost());
		assertEquals(null, strategy.getSecondary());
	}

	@Test
	public void testPrimarySecondarySwapping() throws Exception {
		MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		strategy.loadHosts("foo.com,bar.com");
		strategy.setFailureNumberThreshold(1);
		assertEquals(0, strategy.getPrimary().getFailures().get());
		assertEquals(0, strategy.getSecondary().getFailures().get());
	}

	@Test
	public void testPrimarySecondarySwappingInThreadedEnvironment() throws Exception {
		final int totalThreadCount = 500;

		final MasterSlaveLoadBalanceStrategy strategy = new MasterSlaveLoadBalanceStrategy();
		strategy.loadHosts("foo.com,bar.com");
		assertEquals("foo.com", strategy.getPrimary().getHost());
		assertEquals("bar.com", strategy.getSecondary().getHost());

		strategy.setFailureNumberThreshold(1);

		ExecutorService pool = Executors.newFixedThreadPool(10);
		final CountDownLatch pauseLatch = new CountDownLatch(totalThreadCount);

		for (int i = 0; i < totalThreadCount; i++) {
			pool.submit(new Runnable() {
				@Override
				public void run() {
					try {
						pauseLatch.countDown();
						pauseLatch.await(1, TimeUnit.SECONDS);

						strategy.incrementFailure(new Host(strategy.getPrimary().getHost()));

						String host = strategy.getNextHost();

						assertNotNull(host);
						assertTrue("foo.com".equals(host) || "bar.com".equals(host));
					} catch (InterruptedException e) {
						assertTrue("Error! " + e.toString(), false);
					}
				}
			});
		}
	}
}