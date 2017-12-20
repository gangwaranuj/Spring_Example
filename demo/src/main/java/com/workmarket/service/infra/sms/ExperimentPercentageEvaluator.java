package com.workmarket.service.infra.sms;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ExperimentPercentageEvaluator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentPercentageEvaluator.class);
	private static final Map<String, PercentageHolder> PERCENTAGES = new ConcurrentHashMap<>();

	private final RedisAdapter adapter;
	private final Random random;
	private final ValueFetcher fetcher;
	private final MetricRegistryFacade metricFacade;

	private static final class PercentageHolder implements Gauge<Integer> {
		private int value;

		private PercentageHolder(int value) {
			this.value = value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		@Override
		public Integer getValue() {
			return value;
		}
	}

	@Autowired
	public ExperimentPercentageEvaluator(final RedisAdapter adapter, final MetricRegistry metricRegistry) {
		this(adapter, new SecureRandom(), new WMMetricRegistryFacade(metricRegistry, "experimentPercentage"));
	}

	@VisibleForTesting
	public ExperimentPercentageEvaluator(final RedisAdapter adapter,
	                                     final Random random,
	                                     final MetricRegistryFacade metricFacade) {
		this.adapter = adapter;
		this.random = random;
		this.metricFacade = metricFacade;

		final ScheduledThreadPoolExecutor experimentExecutor = new ScheduledThreadPoolExecutor(1);
		this.fetcher = new ValueFetcher();
		experimentExecutor.scheduleWithFixedDelay(fetcher, 1, 1, TimeUnit.MINUTES);
	}

	@VisibleForTesting
	void forceRefresh() {
		fetcher.run();
	}

	@VisibleForTesting
	void flush() {
		PERCENTAGES.clear();
	}

	private class ValueFetcher implements Runnable {
		@Override
		public void run() {
			final List<String> keys = Lists.newArrayList();
			final List<String> ourKeys = ImmutableList.copyOf(PERCENTAGES.keySet());

			for (final String key : ourKeys) {
				keys.add(RedisFilters.experimentPercentageKey(key));
			}

			if (keys.isEmpty()) {
				return;
			}
			final List<Object> values;
			try {
				values = adapter.getMultiple(keys);
			} catch (final RuntimeException e) {
				LOGGER.error("Blew up trying to update experiment percentages via redis", e);
				return;
			}
			final Iterator<String> kIt = ourKeys.iterator();
			final Iterator<Object> vIt = values.iterator();

			while (kIt.hasNext() && vIt.hasNext()) { // kIt.hasNext should always == vIt.hasNext
				final String key = kIt.next();
				final Object value = vIt.next();
				putPercentage(key, safeIntValue(value));
			}
		}
	}

	private void putPercentage(final String key, final int value) {
		synchronized (PERCENTAGES) {
			if (PERCENTAGES.containsKey(key)) {
				PERCENTAGES.get(key).setValue(value);
			} else {
				final PercentageHolder holder = new PercentageHolder(value);
				try {
					metricFacade.register(key, holder);
				} catch (final IllegalArgumentException e) {
					// There appears to be a side effect somewhere else in our test code that is
					// registering this same key in the metric facade. This is a TEMPORARY fix until
					// we know in more detail how this is happening.
					LOGGER.error("Error registering key", e);
				}
				PERCENTAGES.put(key, holder);
			}
		}
	}

	private int safeIntValue(final Object value) {
		if (value == null) {
			return 0;
		}
		try {
			return Integer.valueOf(value.toString());
		} catch (final NumberFormatException e) {
			LOGGER.error("number {} is not a number", value, e);
			return 0;
		}
	}

	private int getPercentageForExperimentFromRedis(final String key) {
		final Optional<Object> result;
		try {
			result = adapter.get(RedisFilters.experimentPercentageKey(key));
		} catch (final Exception e) {
			LOGGER.error("caught exception getting percentage", e);
			// If redis fails, we don't want to hammer it trying to get an initial value.
			putPercentage(key, 0);
			return 0;
		}
		final int value = result.isPresent() ? safeIntValue(result.get()) : 0;
		putPercentage(key, value);
		return value;
	}

	private int getPercentageForExperiment(final String key) {
		// If we have a cached value, use it.
		final PercentageHolder value = PERCENTAGES.get(key);
		if (value != null) {
			return value.getValue();
		}
		// First time through, we have to get it from Redis directly.
		return getPercentageForExperimentFromRedis(key);
	}

	public boolean shouldRunExperiment(final String experimentName) {
		final int percentageForExperiment = getPercentageForExperiment(experimentName);
		final int random = this.random.nextInt(100);
		return random < percentageForExperiment;
	}
}
