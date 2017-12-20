package com.workmarket.service.infra.sms;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.workmarket.common.metric.NoOpMetricRegistryFacade;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentPercentageEvaluatorTest {
	private static final String EXPERIMENT_NAME = "EXPERIMENT";
	private static final String EXPERIMENT_KEY = RedisFilters.experimentPercentageKey(EXPERIMENT_NAME);
	@Mock Random random;
	@Mock RedisAdapter adapter;
	@Captor ArgumentCaptor<List<String>> captor;

	@Test
	public void basic() throws Exception {
		final ExperimentPercentageEvaluator evaluator = new ExperimentPercentageEvaluator(adapter, random,
			new NoOpMetricRegistryFacade());
		// remove all randomness :)
		when(random.nextInt(100)).thenReturn(5);

		when(adapter.get(EXPERIMENT_KEY)).thenReturn(Optional.absent());
		assertFalse(evaluator.shouldRunExperiment(EXPERIMENT_NAME));

		when(adapter.get(EXPERIMENT_KEY)).thenReturn(Optional.<Object>of("25"));
		assertFalse(evaluator.shouldRunExperiment(EXPERIMENT_NAME));

		when(adapter.getMultiple(captor.capture())).thenReturn(ImmutableList.<Object>of("25"));
		evaluator.forceRefresh();
		assertEquals(1, captor.getValue().size());
		assertEquals(EXPERIMENT_KEY, captor.getValue().get(0));
		assertTrue(evaluator.shouldRunExperiment(EXPERIMENT_NAME));
	}
}
