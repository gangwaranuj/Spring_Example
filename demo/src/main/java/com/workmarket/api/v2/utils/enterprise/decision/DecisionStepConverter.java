package com.workmarket.api.v2.utils.enterprise.decision;

import com.workmarket.api.v2.model.enterprise.decisionflow.DecisionStepDTO;
import com.workmarket.business.decision.gen.Messages.DecisionStepTemplate;
import com.workmarket.business.decision.gen.Messages.DecisionStepTemplateNode;

public class DecisionStepConverter {

	public static DecisionStepDTO convert(final DecisionStepTemplateNode decisionStepTemplateNode) {
		final DecisionStepTemplate decisionStepTemplate = decisionStepTemplateNode.getDecisionStepTemplate();
		switch (decisionStepTemplate.getType()) {
			case UNANIMOUS_USER:
				return UnanimousUserDecisionStepTemplateConverter.convert(decisionStepTemplateNode);
			default:
				final String errorMessage = decisionStepTemplate.getType() + " is an invalid template type";
				throw new IllegalArgumentException(errorMessage);
		}
	}
}
