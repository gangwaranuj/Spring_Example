package com.workmarket.service.decisionflow;

import com.workmarket.business.decision.DecisionFlowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DecisionFlowServiceConfig {

	private DecisionFlowClient decisionFlowClient = null;

	@Bean
	DecisionFlowClient getDecisionFlowClient() {
		if (decisionFlowClient == null) {
			decisionFlowClient = new DecisionFlowClient();
		}
		return decisionFlowClient;
	}
}
