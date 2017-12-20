package com.workmarket.service.featuretoggle;

import com.workmarket.feature.FeatureToggleClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wire up a singleton for {@link FeatureToggleClient}.
 */
@Configuration
public class FeatureToggleServiceConfiguration {
	@Bean
	public FeatureToggleClient getFeatureToggleClient() {
		return new FeatureToggleClient();
	}
}
