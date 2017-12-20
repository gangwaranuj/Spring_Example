package com.workmarket.service.infra.sms;

import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ExperimentPercentageEvaluatorIT extends BaseServiceIT {
	private static final String EXPERIMENT_NAME = "foo";
	private static final String EXPERIMENT_PERCENTAGE_KEY = RedisFilters.experimentPercentageKey(EXPERIMENT_NAME);
	private static final String SECOND_EXPERIMENT_NAME = "found";
	private static final String SECOND_EXPERIMENT_KEY = RedisFilters.experimentPercentageKey(SECOND_EXPERIMENT_NAME);
	@Autowired
	ExperimentPercentageEvaluator evaluator;

	@Autowired
	RedisAdapter redisAdapter;

	@Test
	public void basic() throws Exception {
		evaluator.flush();
		redisAdapter.delete(EXPERIMENT_PERCENTAGE_KEY);
		assertFalse(evaluator.shouldRunExperiment(EXPERIMENT_NAME));
		redisAdapter.set(EXPERIMENT_PERCENTAGE_KEY, 100);
		// hasn't refreshed!
		assertFalse(evaluator.shouldRunExperiment(EXPERIMENT_NAME));
		evaluator.forceRefresh();
		assertTrue(evaluator.shouldRunExperiment(EXPERIMENT_NAME));

		redisAdapter.set(SECOND_EXPERIMENT_KEY, 100);
		assertTrue(evaluator.shouldRunExperiment(SECOND_EXPERIMENT_NAME));
	}
}
