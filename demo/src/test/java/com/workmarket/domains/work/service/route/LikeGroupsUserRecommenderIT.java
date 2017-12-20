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

import static org.junit.Assert.*;

/**
 * Test case for our LikeGroupsUserRecommender
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class LikeGroupsUserRecommenderIT extends BaseServiceIT {

	@Autowired
	private LikeGroupsUserRecommender likeGroupsUserRecommender;

	@Test
	public void testRoutingStrategy() throws Exception {

		// now create a new work assignment
		Work work = newWork(9393l);

		// now try our routing
		Recommendation recommendation = likeGroupsUserRecommender.recommend(work, true);

		assertNotNull(recommendation.getRecommendedResources());
		assertNotNull(recommendation.getExplain());
	}

	@Test
	public void testRoutingStrategyNoExplain() throws Exception {

		// now create a new work assignment
		Work work = newWork(9393l);

		// now try our routing
		Recommendation recommendation = likeGroupsUserRecommender.recommend(work, false);

		assertNotNull(recommendation.getRecommendedResources());
		assertNull(recommendation.getExplain());
	}

}
