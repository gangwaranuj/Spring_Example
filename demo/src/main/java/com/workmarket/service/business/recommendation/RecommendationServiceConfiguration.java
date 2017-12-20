package com.workmarket.service.business.recommendation;

import com.workmarket.business.recommendation.RecommendationClient;
import com.workmarket.business.recommendation.SkillRecommenderClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wire up a singleton for recommendation-service client.
 */
@Configuration
public class RecommendationServiceConfiguration {
	@Bean
	public RecommendationClient getRecommendationClient() {
		return new RecommendationClient();
	}

	@Bean
	public SkillRecommenderClient skillRecommenderClient() {
		return new SkillRecommenderClient();
	}
}
