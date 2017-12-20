package com.workmarket.domains.work.service.route;

import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class PolymathUserRecommenderIT extends BaseServiceIT {

	@Autowired
	private PolymathUserRecommender polymathUserRecommender;

	@Test
	public void testRoutingStrategy() throws Exception {

		// now create a new work assignment
		Work work = newWork(9393l);
		work.setTitle("Do work!");

		// now try our routing
		Recommendation recommendation = polymathUserRecommender.recommend(work, true);

		// we only test if recommendation is returned as long as there is no exception.
		assertTrue(recommendation.getRecommendedResources().size() >= 0);
		assertNotNull(recommendation.getExplain());
	}

	@Test
	public void testRoutingStrategyNoExplain() throws Exception {

		// now create a new work assignment
		Work work = newWork(9393l);
		work.setTitle("Do work!");

		// now try our routing
		Recommendation recommendation = polymathUserRecommender.recommend(work, false);

		// we only test if recommendation is returned as long as there is no exception.
		assertTrue(recommendation.getRecommendedResources().size() >= 0);
		assertNull(recommendation.getExplain());
	}
}
