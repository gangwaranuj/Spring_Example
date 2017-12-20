package com.workmarket.service.infra.http.server;

import com.workmarket.integration.autotask.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MasterSlaveLoadBalanceStrategy implements LoadBalanceStrategy {

	private Host primary;
	private Host secondary;
	private int DEFAULT_FAILURE_THRESHOLD = 10;
	private int failureNumberThreshold = DEFAULT_FAILURE_THRESHOLD;

	private final Object incLock = new Object();

	private static final Logger logger = LoggerFactory.getLogger(MasterSlaveLoadBalanceStrategy.class);

	public void loadHosts(String rawHostsString) {
		clearConfig();
		String[] hosts = new String[]{};

		if (!StringUtil.isNullOrEmpty(rawHostsString)) {
			hosts = rawHostsString.split(",", -1);
		}

		String[] scrubbed = collapseArray(hosts);

		if (scrubbed.length >= 1) {
			primary = new Host(scrubbed[0]);
			if (scrubbed.length >= 2) {
				secondary = new Host(scrubbed[1]);
			} else {
				logger.warn("Only a master is set for the LB. No failover enabled.");
			}
		} else {
			logger.error("No hosts set for LB.");
		}
	}

	@Override
	public String getNextHost() {
		return primary.getHost();
	}

	static String[] collapseArray(String[] hosts) {
		List<String> scrubbed = new ArrayList<>();
		for (String host : hosts) {
			if (!StringUtil.isNullOrEmpty(host)) {
				scrubbed.add(host);
			}
		}
		return scrubbed.toArray(new String[scrubbed.size()]);
	}

	@Override
	public int getNumHosts() {
		int hostCount = 0;
		if (primary != null) {
			hostCount++;
		}
		if (secondary != null) {
			hostCount++;
		}
		return hostCount;
	}

	@Override
	public void setFailureNumberThreshold(int v) {
		failureNumberThreshold = v;
	}

	@Override
	public void incrementFailure(Host host) {
		synchronized (incLock) {
			if (host != null
				&& !StringUtil.isNullOrEmpty(host.getHost())
				&& isHostPrimary(host)
				&& primary.getFailures().incrementAndGet() >= failureNumberThreshold) {
				swap();
			}
		}
	}

	@Override
	public void incrementFailure(String host) {
		incrementFailure(new Host(host));
	}

	private boolean isHostPrimary(Host host) {
		return primary != null && host.getHost().equalsIgnoreCase(primary.getHost());
	}

	protected void swap() {
		if (secondary != null) {
			String primaryHost = primary.getHost();
			primary = new Host(secondary.getHost());
			secondary = new Host(primaryHost);
		} else if (primary != null) {
			primary.getFailures().set(0);
		}
	}

	public Host getPrimary() {
		return primary;
	}

	public void setPrimary(Host primary) {
		this.primary = primary;
	}

	public Host getSecondary() {
		return secondary;
	}

	public void setSecondary(Host secondary) {
		this.secondary = secondary;
	}

	private void clearConfig() {
		primary = null;
		secondary = null;
	}

}
